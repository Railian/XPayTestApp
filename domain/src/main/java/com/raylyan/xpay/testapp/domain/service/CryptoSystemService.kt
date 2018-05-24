package com.raylyan.xpay.testapp.domain.service

interface CryptoSystemService {
    fun encrypt(string: String): String
    fun decrypt(string: String): String
}