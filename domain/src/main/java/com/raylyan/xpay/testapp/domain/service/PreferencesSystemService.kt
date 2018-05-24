package com.raylyan.xpay.testapp.domain.service

interface PreferencesSystemService {
    operator fun contains(key: String): Boolean
    operator fun set(key: String, pref: String)
    operator fun get(key: String): String?
}