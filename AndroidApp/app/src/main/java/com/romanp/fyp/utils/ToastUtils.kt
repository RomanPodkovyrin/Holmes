package com.romanp.fyp.utils

import android.content.Context
import android.widget.Toast

/**
 * Used to call Toast messages
 */
class ToastUtils {
    companion object {
        fun toast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}