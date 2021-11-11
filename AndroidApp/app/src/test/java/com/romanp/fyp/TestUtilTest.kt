package com.romanp.fyp

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
// TODO: delete when not needed as an example
internal class TestUtilTest {

    @Test
    fun `signUp function returns false when username or password is empty`(){
        val userName = ""
        val password = ""
        val repeatPassword = ""
        assertThat(TestUtil.signUp(userName, password,repeatPassword)).isFalse()
    }

    @Test
    fun `signUp function returns false when username is taken`(){
        val userName = "Roman"
        val password = "12345"
        val repeatPassword = "12345"
        assertThat(TestUtil.signUp(userName, password,repeatPassword)).isFalse()
    }

    @Test
    fun `signUp function returns false when password and repeat password don't match`(){
        val userName = "James"
        val password = "12345"
        val repeatPassword = "67890"
        assertThat(TestUtil.signUp(userName, password,repeatPassword)).isFalse()
    }

    @Test
    fun `signUp function returns false when password is less than two characters`(){
        val userName = "Brian"
        val password = "1"
        val repeatPassword = "1"
        assertThat(TestUtil.signUp(userName, password,repeatPassword)).isFalse()
    }

}