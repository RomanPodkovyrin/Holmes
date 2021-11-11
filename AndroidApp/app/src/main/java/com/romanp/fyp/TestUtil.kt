package com.romanp.fyp

import android.util.Log

object TestUtil {
    private const val TAG = "AuthUtil"
    private val users = listOf("Roman", "Paul", "Polina")

    fun signUp(
        userName: String,
        password: String,
        passwordVerification: String
    ): Boolean {
        Log.i(TAG, "User $userName signed in")
        return when {
            userName.isEmpty() || password.isEmpty() -> false
            users.contains(userName) -> false
            password != passwordVerification -> false
            password.length < 2 -> false
            else -> true
        }
    }
}