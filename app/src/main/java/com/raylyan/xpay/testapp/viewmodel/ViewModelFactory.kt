package com.raylyan.xpay.testapp.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.raylyan.xpay.testapp.domain.service.CryptoSystemService
import com.raylyan.xpay.testapp.domain.service.FingerprintSystemService
import com.raylyan.xpay.testapp.domain.service.PreferencesSystemService
import com.raylyan.xpay.testapp.platform.service.CryptoAndroidService
import com.raylyan.xpay.testapp.platform.service.FingerprintAndroidService
import com.raylyan.xpay.testapp.platform.service.PreferencesAndroidService

class ViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val cryptoService: CryptoSystemService by lazy { CryptoAndroidService() }
    private val fingerprintService: FingerprintSystemService  by lazy { FingerprintAndroidService(context) }
    private val preferencesService: PreferencesSystemService  by lazy { PreferencesAndroidService(context) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            LoginViewModel::class.java -> LoginViewModel(cryptoService, fingerprintService, preferencesService) as T
            else -> throw IllegalArgumentException("Unsupported view model")
        }
    }
}