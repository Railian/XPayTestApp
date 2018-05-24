package com.raylyan.xpay.testapp.domain.service

interface FingerprintSystemService {

    fun checkFingerprintCompatibility(): Boolean

    fun authenticateWithFingerprint(
            onSucceeded: () -> Unit,
            onFailed: () -> Unit,
            onError: (message: String) -> Unit = {},
            onHelp: (message: String) -> Unit = {}
    )

    fun cancelFingerprintAuthentication()

}