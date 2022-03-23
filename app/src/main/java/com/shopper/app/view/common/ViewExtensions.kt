package com.shopper.app.view.common

import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.showSoftInputInDialog() {
    if (isFocusable) {
        if (requestFocus()) {
            val inputManager: InputMethodManager? = context.systemService()
            inputManager?.showSoftInput(this, 0)
        }
    }
}

fun View.hideSoftInputFromDialog() {
    clearFocus()
    val inputManager: InputMethodManager? = context.systemService()
    inputManager?.hideSoftInputFromWindow(windowToken, 0)
}
