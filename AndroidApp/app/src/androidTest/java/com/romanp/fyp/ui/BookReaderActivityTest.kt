package com.romanp.fyp.ui

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.romanp.fyp.views.BookReaderActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BookReaderActivityTest {

    val intent = Intent(
        ApplicationProvider.getApplicationContext(),
        BookReaderActivity::class.java
    ).putExtra("BookId", 1)

    @get:Rule
    val activityRule = ActivityScenarioRule<BookReaderActivity>(intent)

    @Before
    fun setUp() {
//        val intent = Intent()
        // add stuff to intent
    }

    @After
    fun cleanup() {
    }

    @Test
    fun verify_ServiceStatus() {

    }
}