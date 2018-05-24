package com.raylyan.xpay.testapp.platform.service

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.raylyan.xpay.testapp.domain.service.PreferencesSystemService

class PreferencesAndroidService(context: Context) : PreferencesSystemService {

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun contains(key: String): Boolean = key in preferences
    override fun set(key: String, pref: String): Unit = preferences.edit().putString(key, pref).apply()
    override fun get(key: String): String? = preferences.getString(key, null)
}