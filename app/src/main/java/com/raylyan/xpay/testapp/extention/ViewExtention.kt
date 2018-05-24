package com.raylyan.xpay.testapp.extention

import android.view.View

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide(saveSpace: Boolean = true) {
    visibility = if (saveSpace) View.INVISIBLE else View.GONE
}

object Views {
    fun show(vararg views: View) = views.forEach { it.show() }
    fun hide(vararg views: View, saveSpace: Boolean = true) = views.forEach { it.hide(saveSpace) }
}