package com.wildan.nirlababpjscianjur.utils

import android.text.TextUtils
import android.util.Patterns

class Validation {

    companion object {
        fun validate(input: String?): Boolean {
            return TextUtils.isEmpty(input)
        }
    }
}