package com.raylyan.xpay.testapp.extention

import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

var EditText.content: String
    get() = text.toString()
    set(value) = setText(value)

fun EditText.isEmpty(): Boolean = text.isEmpty()

fun EditText.clear(): Unit = text.clear()

fun EditText.onContentChanged(
        before: (string: String, start: Int, count: Int, after: Int) -> Unit = { _, _, _, _ -> },
        during: (string: String, start: Int, count: Int, after: Int) -> Unit = { _, _, _, _ -> },
        after: (s: String) -> Unit
): TextWatcher {
    return object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = before(s.toString(), start, count, after)
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = during(s.toString(), start, before, count)
        override fun afterTextChanged(s: Editable) = after(s.toString())
    }.also(::addTextChangedListener)
}

fun EditText.hideKeyboard() {
    val inputManager = context.systemService<InputMethodManager>()
    inputManager.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.showKeyboard() {
    val inputManager = context.systemService<InputMethodManager>()
    inputManager.showSoftInput(this, 0)
}

fun TextInputLayout.resetError() {
    error?.let { error = null }
}
