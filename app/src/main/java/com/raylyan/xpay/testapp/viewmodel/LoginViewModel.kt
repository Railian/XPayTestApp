package com.raylyan.xpay.testapp.viewmodel

import androidx.lifecycle.ViewModel
import com.raylyan.xpay.testapp.domain.entity.Credentials
import com.raylyan.xpay.testapp.domain.service.CryptoSystemService
import com.raylyan.xpay.testapp.domain.service.FingerprintSystemService
import com.raylyan.xpay.testapp.domain.service.PreferencesSystemService

class LoginViewModel(
        private val cryptoService: CryptoSystemService,
        private val fingerprintService: FingerprintSystemService,
        private val preferencesService: PreferencesSystemService
) : ViewModel() {

    companion object {

        private const val PREF_LOGIN = "login"
        private const val PREF_ENCRYPTED_PASSWORD = "encrypted_password"

        const val MESSAGE_CREDENTIALS_EMPTY = "MESSAGE_CREDENTIALS_EMPTY"

        const val MESSAGE_LOGIN_REQUIRED = "MESSAGE_LOGIN_REQUIRED"
        const val MESSAGE_LOGIN_TOO_SHORT = "MESSAGE_LOGIN_TOO_SHORT"
        const val MESSAGE_LOGIN_TOO_LONG = "MESSAGE_LOGIN_TOO_LONG"

        const val MESSAGE_PASSWORD_IS_REQUIRED = "MESSAGE_PASSWORD_IS_REQUIRED"
        const val MESSAGE_PASSWORD_IS_TOO_SHORT = "MESSAGE_PASSWORD_IS_TOO_SHORT"
        const val MESSAGE_PASSWORD_IS_TOO_LONG = "MESSAGE_PASSWORD_IS_TOO_LONG"
    }

    fun saveCredentials(
            credentials: Credentials,
            onLoginNotValid: (message: String) -> Unit = {},
            onPasswordNotValid: (message: String) -> Unit = {}
    ): Boolean {
        @Suppress("LiftReturnOrAssignment")
        when (areEnteredCredentialsValid(credentials, onLoginNotValid, onPasswordNotValid)) {
            true -> {
                preferencesService[PREF_LOGIN] = credentials.login
                preferencesService[PREF_ENCRYPTED_PASSWORD] = cryptoService.encrypt(credentials.password)
                return true
            }
            else -> return false
        }
    }

    fun checkCredentials(
            enteredCredentials: Credentials,
            onLoginNotValid: (message: String) -> Unit = {},
            onPasswordNotValid: (message: String) -> Unit = {}
    ): Boolean {
        areEnteredCredentialsValid(enteredCredentials, onLoginNotValid, onPasswordNotValid)
        return savedCredentials?.let { savedCredentials ->
            enteredCredentials.login == savedCredentials.login &&
                    enteredCredentials.password == savedCredentials.password
        } ?: false
    }

    fun checkFingerprintCompatibility(): Boolean {
        return fingerprintService.checkFingerprintCompatibility()
    }

    fun authenticateWithFingerprint(
            onSuccess: (credentials: Credentials) -> Unit,
            onFailure: () -> Unit,
            onError: (message: String) -> Unit = {},
            onHelp: (message: String) -> Unit = {}
    ) {
        if (hasSavedCredentials()) {
            fingerprintService.authenticateWithFingerprint(
                    onSucceeded = {
                        savedCredentials?.let(onSuccess) ?: onError(MESSAGE_CREDENTIALS_EMPTY)
                    },
                    onFailed = onFailure,
                    onError = onError,
                    onHelp = onHelp
            )
        } else {
            onError(MESSAGE_CREDENTIALS_EMPTY)
        }
    }

    fun cancelFingerprintAuthentication() {
        fingerprintService.cancelFingerprintAuthentication()
    }

    private val savedCredentials: Credentials?
        get() {
            val savedLogin = preferencesService[PREF_LOGIN]
            val savedPassword = preferencesService[PREF_ENCRYPTED_PASSWORD]?.let(cryptoService::decrypt)
            return if (savedLogin == null || savedPassword == null) null
            else Credentials(login = savedLogin, password = savedPassword)
        }

    private fun areEnteredCredentialsValid(
            credentials: Credentials,
            onLoginNotValid: (message: String) -> Unit = {},
            onPasswordNotValid: (message: String) -> Unit = {}
    ): Boolean {
        val (login, password) = credentials
        val loginErrorMessage = login.length.let { length ->
            when {
                length == 0 -> MESSAGE_LOGIN_REQUIRED
                length < 3 -> MESSAGE_LOGIN_TOO_SHORT
                length > 20 -> MESSAGE_LOGIN_TOO_LONG
                else -> null
            }
        }
        loginErrorMessage?.let(onLoginNotValid)
        val passwordErrorMessage = password.length.let { length ->
            when {
                length == 0 -> MESSAGE_PASSWORD_IS_REQUIRED
                length < 4 -> MESSAGE_PASSWORD_IS_TOO_SHORT
                length > 20 -> MESSAGE_PASSWORD_IS_TOO_LONG
                else -> null
            }
        }
        passwordErrorMessage?.let(onPasswordNotValid)
        return loginErrorMessage == null && passwordErrorMessage == null
    }

    fun hasSavedCredentials(): Boolean {
        return PREF_LOGIN in preferencesService && PREF_ENCRYPTED_PASSWORD in preferencesService
    }
}