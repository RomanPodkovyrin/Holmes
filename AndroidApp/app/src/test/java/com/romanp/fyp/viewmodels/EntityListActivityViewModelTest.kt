package com.romanp.fyp.viewmodels

import com.romanp.fyp.adapters.entityRecyclerView.EntityRecyclerViewAdapter
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Chapter
import com.romanp.fyp.models.book.Entity
import com.romanp.fyp.repositories.BookRepository
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class EntityListActivityViewModelTest {

    private lateinit var viewModel: EntityListActivityViewModel
    private val currentBookExpected = BookInfo(
        1,
        "Orwell",
        "1984",
        arrayListOf(
            Chapter("Chapter 1", "text 1"),
            Chapter("Chapter 2", "text 2"),
            Chapter("Chapter 3", "text 3")
        ),
        arrayListOf(
            Entity("Julia", setOf(), "", "", "", "", "", arrayListOf(), arrayListOf()),
            Entity("Winston", setOf(), "", "", "", "", "", arrayListOf(), arrayListOf())
        ),
        arrayListOf(
            Entity("Eastasia", setOf(), "", "", "", "", "", arrayListOf(), arrayListOf()),
            Entity("Oceania", setOf(), "", "", "", "", "", arrayListOf(), arrayListOf())
        ),
        arrayListOf()
    )

    private val expectedLocations =
        arrayListOf(
            EntityRecyclerViewAdapter.RecyclerEntityInfo(
                1L,
                currentBookExpected.locations[0].name,
                false
            ),
            EntityRecyclerViewAdapter.RecyclerEntityInfo(
                1L,
                currentBookExpected.locations[1].name,
                false
            )
        )
    private val expectedCharacters =
        arrayListOf(
            EntityRecyclerViewAdapter.RecyclerEntityInfo(
                1L,
                currentBookExpected.characters[0].name,
                true
            ),
            EntityRecyclerViewAdapter.RecyclerEntityInfo(
                1L,
                currentBookExpected.characters[1].name,
                true
            )
        )

    @Mock
    private val mockRepo = Mockito.mock(BookRepository::class.java)

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    fun setUp() {

        Mockito.`when`(mockRepo.getBookInfo(any(), any())).thenReturn(currentBookExpected)

        viewModel =
            EntityListActivityViewModel(RuntimeEnvironment.getApplication(), mockRepo, 1, true)
        Mockito.clearInvocations(mockRepo)
    }

    @After
    fun cleanUp() {
        Mockito.reset(mockRepo)
    }


    /*
    runBlocking {} to ensure that all running coroutines get a chance to complete before the test function returns.
     */
    @Test
    fun `returns correct book`() = runBlocking {
        val book = viewModel.getCurrentBookInfo()
        Assert.assertEquals("Correct book", currentBookExpected, book)
        return@runBlocking
    }

    @Test
    fun `returns correct characters list`() = runBlocking {
        val characters = viewModel.getCharacters()
        Assert.assertEquals("Correct characters list", currentBookExpected.characters, characters)
    }

    @Test
    fun `returns correct location list`() = runBlocking {
        val locations = viewModel.getLocations()
        Assert.assertEquals("Correct locations list", currentBookExpected.locations, locations)
    }

    @Test
    fun `returns list type characters`() = runBlocking {
        viewModel =
            EntityListActivityViewModel(RuntimeEnvironment.getApplication(), mockRepo, 1, true)
        val listType = viewModel.listType()
        Assert.assertEquals("Character list", true, listType)

        val characters = viewModel.getCurrentList()
        Assert.assertEquals("Correct characters list", expectedCharacters, characters)
    }

    @Test
    fun `returns list type locations`() = runBlocking {
        viewModel =
            EntityListActivityViewModel(RuntimeEnvironment.getApplication(), mockRepo, 1, false)
        val listType = viewModel.listType()
        Assert.assertEquals("Character list", false, listType)


        val locations = viewModel.getCurrentList()
        Assert.assertEquals("Correct locations list", expectedLocations, locations)
    }

}