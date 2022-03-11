package com.romanp.fyp.ui

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.romanp.fyp.R
import com.romanp.fyp.views.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    private var loadBookButtonText: String = "Load Book"

//    lateinit var scenario: ActivityScenario<MainActivity>
//    val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
//
//    @get:Rule
//    var activityRule: ActivityScenarioRule<MainActivity> =
//        ActivityScenarioRule(MainActivity::class.java)


//    @get:Rule
//    val intentsTestRule = IntentsTestRule(MainActivity::class.java)

//    @get:Rule
//    var mRuntimePermissionRule = GrantPermissionRule
//        .grant(Manifest.permission.READ_EXTERNAL_STORAGE)


    //    lateinit var scenario: ActivityScenario<MainActivity>
    val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)

    @get:Rule
    val activityRule = ActivityScenarioRule<MainActivity>(intent)

    @Before
    fun setUp() {
//        val intent = Intent()
        // add stuff to intent
//        activityRule.
    }

    @After
    fun cleanup() {
//        scenario.close()
    }

    @Test
    fun verify_ServiceStatus() {
        onView(withId(R.id.tvServiceStatus))
            .check(matches(withText("Offline")))
//        onView(withId(R.id.loadBookButton)).perform(click())
    }

    @Test
    fun verify_can_click_load_book_button() {
        onView(withId(R.id.loadBookButton))
            .check(matches(withText(loadBookButtonText)))
        onView(withId(R.id.loadBookButton)).perform(click())
    }
//    @Test
//    fun `test_validateLoadEpubFileIntent`() {
//        // GIVEN
//        val expectedIntent: Matcher<Intent> = allOf(
//            hasType("application/epub+zip"),
//            hasAction(Intent.ACTION_GET_CONTENT),
//        )
//        val activityResult = createFilePickActivityResultStub()
//        intending(expectedIntent).respondWith(activityResult)
//
//        // Execute and Verify
//        onView(withId(R.id.loadBookButton))
//            .check(matches(withText(loadBookButtonText)))
//        onView(withId(R.id.loadBookButton)).perform(click())
//        intended(hasAction(Intent.ACTION_GET_CONTENT))
//
//    }

//    private fun createFilePickActivityResultStub(): Instrumentation.ActivityResult {
//        val resources: Resources = InstrumentationRegistry.getInstrumentation().context.resources
//        val uri = Uri.fromFile(File("file:///android_asset//alice.epub"))
//        val bookUri = Uri.parse(
//            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
//                    resources.getResourcePackageName(R.drawable.ic_launcher_background) + "/" +
//                    resources.getResourceTypeName(R.drawable.ic_launcher_background) + "/" +
//                    resources.getResourceEntryName(R.drawable.ic_launcher_background)
//        )
//        val resultIntent = Intent()
//        resultIntent.data = uri
//        resultIntent.
//        return Instrumentation.ActivityResult(RESULT_OK, resultIntent)
//    }


}
