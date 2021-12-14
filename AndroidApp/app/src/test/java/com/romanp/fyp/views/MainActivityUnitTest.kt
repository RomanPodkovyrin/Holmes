//package com.romanp.fyp
//
//import android.view.View
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.Observer
//import com.romanp.fyp.adapters.BookRecyclerViewAdapter
//import com.romanp.fyp.viewmodels.MainActivityViewModel
//import junit.framework.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.ArgumentCaptor
//import org.mockito.ArgumentMatchers
//import org.mockito.Captor
//import org.mockito.Mock
//import org.mockito.Mockito.`when`
//import org.mockito.Mockito.verify
//import org.mockito.junit.MockitoJUnit
//import org.mockito.junit.MockitoRule
//import org.robolectric.Robolectric
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.android.controller.ActivityController
//
// TODO: finish it
//@RunWith(RobolectricTestRunner::class)
//class MainActivityUnitTest {
//
//    private lateinit var activity: MainActivity
//
//    private lateinit var activityController: ActivityController<MainActivity>
//
//    @Mock
//    private lateinit var viewModel: MainActivityViewModel
//
//    @Mock
//    private lateinit var booksLiveData: LiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>>
//
//    @Captor
//    private lateinit var booksObserverCaptor: ArgumentCaptor<Observer<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>>>
//
//    @Rule
//    @JvmField
//    val mockitoRule: MockitoRule = MockitoJUnit.rule()
//
//    @Before
//    fun setUp() {
//        `when`(viewModel.getBooks()).thenReturn(booksLiveData)
//
//        activityController = Robolectric.buildActivity(MainActivity::class.java)
//        activity = activityController.get()
//
//        activityController.create()
//        activity.setTestViewModel(viewModel)
//
//        activityController.start()
//        verify(booksLiveData).observe(
//            ArgumentMatchers.any(LifecycleOwner::class.java),
//            booksObserverCaptor.capture()
//        )
//    }
//
//    @Test
//    fun `has visible loading view on create`() {
//        assertEquals(View.VISIBLE, activity.loading_spinner.visibility)
//    }
//
//    @Test
//    fun `has hidden recycler view and error view on create`() {
//        assertEquals(View.GONE, activity.teams_list.visibility)
//        assertEquals(View.GONE, activity.tv_error.visibility)
//    }
//}