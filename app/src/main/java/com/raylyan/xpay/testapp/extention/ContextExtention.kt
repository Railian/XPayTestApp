package com.raylyan.xpay.testapp.extention

import android.content.Context

inline fun <reified T> Context.systemService(): T = getSystemService(T::class.java)