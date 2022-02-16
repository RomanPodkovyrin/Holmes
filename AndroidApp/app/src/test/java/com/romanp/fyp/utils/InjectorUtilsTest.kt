package com.romanp.fyp.utils

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.getApplication
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class InjectorUtilsTest {

    @Test
    fun `test MainActivityViewModelFactory Injector`() {
        val factory = InjectorUtils.provideMainActivityViewModelFactory(getApplication())
        assertNotNull(factory)
    }

    @Test
    fun `test BookReaderActivityViewModelFactory Injector`() {
        val factory = InjectorUtils.provideBookReaderActivityViewModelFactory(getApplication(), 1)
        assertNotNull(factory)
    }

    @Test
    fun `test EntityListActivityViewModelFactory Injector`() {
        val factory =
            InjectorUtils.provideEntityListActivityViewModelFactory(getApplication(), 1, true)
        assertNotNull(factory)
    }

    @Test
    fun `test BookGraphActivityViewModelFactory Injector`() {
        val factory = InjectorUtils.provideBookGraphActivityViewModelFactory(getApplication(), 1)
        assertNotNull(factory)
    }
}