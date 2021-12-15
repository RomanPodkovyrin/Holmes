package com.romanp.fyp.utils

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.RuntimeEnvironment.getApplication

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class InjectorUtilsTest {

    @Test
    fun `test MainActivityViewModelFactory Injector`() {
        val factory = InjectorUtils.provideMainActivityViewModelFactory(getApplication())
        assertNotNull(factory)
    }
}