package com.romanp.fyp


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    private lateinit var stringToBetyped: String

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity>
            = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun initValidString() {
        // Specify a valid string.
        stringToBetyped = "No book loaded"
    }

    @Test
    fun changeText_sameActivity() {
        // Check that the text was changed.
        onView(withId(R.id.book_title))
            .check(matches(withText(stringToBetyped)))
    }
}
