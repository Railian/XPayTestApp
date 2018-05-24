package com.raylyan.xpay.testapp.view

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.raylyan.xpay.testapp.R
import com.raylyan.xpay.testapp.domain.entity.Credentials
import com.raylyan.xpay.testapp.extention.*
import com.raylyan.xpay.testapp.viewmodel.LoginViewModel
import com.raylyan.xpay.testapp.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val MESSAGE_CREDENTIALS_VERIFIED = "MESSAGE_CREDENTIALS_VERIFIED"
        private const val MESSAGE_CREDENTIALS_NOT_VERIFIED = "MESSAGE_CREDENTIALS_NOT_VERIFIED"
        private const val MESSAGE_FINGERPRINT_AUTH_FAILED = "MESSAGE_FINGERPRINT_AUTH_FAILED"
        private const val MESSAGE_CREDENTIALS_SAVED = "MESSAGE_CREDENTIALS_SAVED"
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        injectDependency()
        initView()
    }

    override fun onStop() {
        super.onStop()
        onStopFingerprintAuthentication()
        viewModel.cancelFingerprintAuthentication()
    }

    private fun injectDependency() {
        viewModel = ViewModelProviders
                .of(this, ViewModelFactory(this))
                .get(LoginViewModel::class.java)
    }

    private fun initView() {
        editTextLogin.onContentChanged { textInputLogin.resetError() }
        editTextPassword.onContentChanged { textInputPassword.resetError() }
        buttonCheckCredentials.setOnClickListener { checkCredentials() }
        buttonSaveCredentials.setOnClickListener { saveCredentials() }
        buttonFingerprintAuthentication.isEnabled = viewModel.checkFingerprintCompatibility()
        buttonFingerprintAuthentication.setOnClickListener { fingerprintAuthentication() }
    }

    private val enteredCredentials: Credentials
        get() = Credentials(
                login = editTextLogin.content,
                password = editTextPassword.content
        )

    private fun onLoginNotValid(message: String) {
        textInputLogin.error = message.localize()
    }

    private fun onPasswordNotValid(message: String) {
        textInputPassword.error = message.localize()
    }

    private fun checkCredentials() {
        if (!viewModel.hasSavedCredentials()) {
            showMessage(LoginViewModel.MESSAGE_CREDENTIALS_EMPTY)
            return
        }
        when (viewModel.checkCredentials(
                enteredCredentials = enteredCredentials,
                onLoginNotValid = ::onLoginNotValid,
                onPasswordNotValid = ::onPasswordNotValid
        )) {
            true -> showMessage(MESSAGE_CREDENTIALS_VERIFIED)
            false -> showMessage(MESSAGE_CREDENTIALS_NOT_VERIFIED)
        }
    }

    private fun saveCredentials() {
        viewModel.saveCredentials(
                credentials = enteredCredentials,
                onLoginNotValid = ::onLoginNotValid,
                onPasswordNotValid = ::onPasswordNotValid
        ).let { isSaved -> if (isSaved) showMessage(MESSAGE_CREDENTIALS_SAVED) }
    }

    private fun bindCredentials(credentials: Credentials) {
        editTextLogin.content = credentials.login
        textInputLogin.resetError()
        editTextPassword.content = credentials.password
        textInputPassword.resetError()
    }

    private fun fingerprintAuthentication() {
        onStartFingerprintAuthentication()
        viewModel.authenticateWithFingerprint(
                onSuccess = {
                    onStopFingerprintAuthentication()
                    bindCredentials(it)
                    checkCredentials()
                },
                onFailure = {
                    onStopFingerprintAuthentication()
                    showMessage(MESSAGE_FINGERPRINT_AUTH_FAILED)
                },
                onError = {
                    onStopFingerprintAuthentication()
                    showMessage(it)
                },
                onHelp = ::showMessage
        )
    }

    private fun onStartFingerprintAuthentication() {
        buttonFingerprintAuthentication.hide(saveSpace = true)
        viewTouchInfo.show()
    }

    private fun onStopFingerprintAuthentication() {
        buttonFingerprintAuthentication.show()
        viewTouchInfo.hide(saveSpace = true)
    }

    private fun showMessage(message: String) {
        Snackbar.make(viewRoot, message.localize(), Snackbar.LENGTH_LONG).show()
    }

    private fun String.localize(): String {
        return when (this) {
            MESSAGE_CREDENTIALS_VERIFIED -> resources.getString(R.string.message_credentials_verified)
            MESSAGE_CREDENTIALS_NOT_VERIFIED -> resources.getString(R.string.message_credentials_not_verified)
            MESSAGE_FINGERPRINT_AUTH_FAILED -> resources.getString(R.string.message_fingerprint_auth_failed)
            MESSAGE_CREDENTIALS_SAVED -> resources.getString(R.string.message_credentials_saved)
            LoginViewModel.MESSAGE_CREDENTIALS_EMPTY -> resources.getString(R.string.message_credentials_empty)
            LoginViewModel.MESSAGE_LOGIN_REQUIRED -> resources.getString(R.string.message_login_required)
            LoginViewModel.MESSAGE_LOGIN_TOO_SHORT -> resources.getString(R.string.message_login_too_short)
            LoginViewModel.MESSAGE_LOGIN_TOO_LONG -> resources.getString(R.string.message_login_too_long)
            LoginViewModel.MESSAGE_PASSWORD_IS_REQUIRED -> resources.getString(R.string.message_password_required)
            LoginViewModel.MESSAGE_PASSWORD_IS_TOO_SHORT -> resources.getString(R.string.message_password_too_short)
            LoginViewModel.MESSAGE_PASSWORD_IS_TOO_LONG -> resources.getString(R.string.message_password_too_long)
            else -> this
        }
    }
}
