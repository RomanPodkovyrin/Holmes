package com.romanp.fyp


import androidx.lifecycle.Lifecycle
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
import com.romanp.fyp.views.MainActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    private lateinit var buttonText: String

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity>
            = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun initValidString() {
        // Specify a valid string.
        buttonText = "Load Book"
    }

    @Test
    fun changeText_sameActivity() {
        onView(withId(R.id.loadBookButton))
            .check(matches(withText(buttonText)))
    }
}
