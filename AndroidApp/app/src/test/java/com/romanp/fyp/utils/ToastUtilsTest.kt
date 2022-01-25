package com.romanp.fyp.utils

import android.widget.Toast
import org.junit.Test
import org.junit.jupiter.api.fail
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.getApplication
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ToastUtilsTest {

    @Test
    fun `test simple Toast`() {
        try {
            val toast = ToastUtils()
            ToastUtils.toast(getApplication(), "Hello Unit Test")
        } catch (e: Exception) {
            fail("Should not have thrown any exception")
        }


    }
}