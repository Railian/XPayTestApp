package com.raylyan.xpay.testapp.platform.service

import android.content.Context
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import com.raylyan.xpay.testapp.domain.service.FingerprintSystemService

class FingerprintAndroidService(context: Context) : FingerprintSystemService {

    private val fingerprintManager = FingerprintManagerCompat.from(context)
    private var fingerprintCancellation: CancellationSignal? = null

    override fun checkFingerprintCompatibility(): Boolean {
        return fingerprintManager.isHardwareDetected
    }

    override fun authenticateWithFingerprint(
            onSucceeded: () -> Unit,
            onFailed: () -> Unit,
            onError: (message: String) -> Unit,
            onHelp: (message: String) -> Unit
    ) {
        fingerprintCancellation = CancellationSignal()
        fingerprintManager.authenticate(null, 0, fingerprintCancellation,
                object : FingerprintManagerCompat.AuthenticationCallback() {
                    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) = onError.invoke(errString.toString())
                    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) = onHelp.invoke(helpString.toString())
                    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult) = onSucceeded.invoke()
                    override fun onAuthenticationFailed() = onFailed.invoke()
                }, null)
    }

    override fun cancelFingerprintAuthentication() {
        fingerprintCancellation?.cancel()
    }
}