package com.romanp.fyp.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.romanp.fyp.models.book.AlreadyOnTheFirstPageException
import com.romanp.fyp.models.book.Chapter
import com.romanp.fyp.models.book.Mention
import com.romanp.fyp.models.book.NoMorePagesException
import com.romanp.fyp.repositories.BookRepository
import com.romanp.fyp.views.EntityListActivity
import com.romanp.fyp.views.EntityType

class BookReaderActivityViewModel
/**
 * @param application
 * @param repository
 * @param bookId id of the book to be loaded from repository
 */(application: Application, repository: BookRepository, bookId: Long) : BookViewModel(
    application, repository, bookId
) {

    companion object {
        private const val TAG = "BookReaderActivityViewModel"
    }


    private var currentPage: Int = 0

    init {
        getBookInfo(bookId)
    }


    /**
     * @return next chapter
     * @throws NoMorePagesException
     */
    fun nextButton(): Chapter {
        if (currentPage >= currentBook.chapters.size - 1) {
            Log.i(TAG, "Max page reached")
            throw NoMorePagesException("Reached the last page of the book")
        }
        currentPage++
        return getCurrentChapter()
    }

    /**
     * @return previous chapter
     * @throws AlreadyOnTheFirstPageException
     */
    fun backButton(): Chapter {
        if (currentPage <= 0) {
            Log.i(TAG, "Min page reached")
            throw AlreadyOnTheFirstPageException("There is not page before this one")
        }
        currentPage--
        return getCurrentChapter()
    }

    fun getCurrentChapter(): Chapter {
        return currentBook.chapters[currentPage]
    }

    fun getCurrentChapterEntityMentionsSpans(): Pair<ArrayList<Pair<String, ArrayList<Mention>>>, ArrayList<Pair<String, ArrayList<Mention>>>> {
        val locations = arrayListOf<Pair<String, ArrayList<Mention>>>()
        val characters = arrayListOf<Pair<String, ArrayList<Mention>>>()
        currentBook.locations.forEach { location ->
            locations.add(Pair(location.name, location.byChapterMentions[currentPage]))
        }

        currentBook.characters.forEach { character ->
            characters.add(Pair(character.name, character.byChapterMentions[currentPage]))
        }

        return Pair(characters, locations)
    }


    fun switchToEntityList(context: Context, entityType: EntityType) {
        val intent = Intent(context, EntityListActivity::class.java)
        intent.putExtra(EntityListActivity.EXTRA_MESSAGE, getBookID())
        intent.putExtra(EntityListActivity.EXTRA_MESSAGE_TYPE, entityType.message)
        context.startActivity(intent)
    }


}

class BookReaderViewModelFactory(
    private val application: Application,
    private val repository: BookRepository,
    private val bookId: Long
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookReaderActivityViewModel(application, repository, bookId) as T
    }

}
