package com.romanp.fyp.viewmodels

import android.app.Application
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.BookUtil.Companion.loadBook
import com.romanp.fyp.nlp.CoreNlpAPI
import com.romanp.fyp.nlp.Runnables
import com.romanp.fyp.repositories.BookRepository
import com.romanp.fyp.utils.ToastUtils
import kotlinx.coroutines.launch


class MainActivityViewModel : AndroidViewModel {
    companion object {
        private const val TAG = "MainActivityViewModel"
    }

    private var repository: BookRepository

    // Live Data
    private var books: MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>> =
        MutableLiveData()
    private var serviceStatus: MutableLiveData<Boolean> = MutableLiveData()
    private var processedBook: MutableLiveData<BookInfo> = MutableLiveData()


    // Threads
    private lateinit var mainHandler: Handler
    private lateinit var pingRunnable: Runnables.PingNLPAPIRunnable
    private lateinit var checkRunnable: Runnables.CheckNLPAPIRunnable

    fun onDestroy() {
        Log.i(TAG, "View Destroyed")
        mainHandler.removeCallbacks(pingRunnable)
        mainHandler.removeCallbacks(checkRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "onCleared")
        mainHandler.removeCallbacks(pingRunnable)
        mainHandler.removeCallbacks(checkRunnable)
    }

    constructor(
        application: Application,
        repository: BookRepository
    ) : super(application) {
        //TODO: pass thread handler for easy testability
        Log.i(TAG, "View created")
        this.repository = repository

        setupThreads()
    }

    private fun setupThreads() {
        mainHandler = Handler(Looper.getMainLooper())
        pingRunnable = Runnables.PingNLPAPIRunnable(getApplication(), mainHandler, serviceStatus)
        checkRunnable = Runnables.CheckNLPAPIRunnable(getApplication(), mainHandler, books)
    }


    fun startThreads() {
        Log.i(TAG, "Starting thread handler")
        mainHandler.post(pingRunnable)
        mainHandler.post(checkRunnable)
    }


    fun getNLPServiceStatus(): LiveData<Boolean> = serviceStatus

    fun getBooks(): LiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>> {
        val value = repository.getRecyclerBookInfoList(getApplication())
        books.value = value.value
        return books
    }

    /**
     * @return id if successful or -1 if failed
     */
    fun addBook(selectedFile: Uri?) {

        viewModelScope.launch {
            val book: BookInfo = try {
                loadSelectedBook(selectedFile)
            } catch (e: Exception) {
                Log.e(TAG, "Error while loading the book $selectedFile")
                Toast.makeText(getApplication(), "Issue while loading", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }

            processedBook.value = book
            //TODO: call check book first  CoreNlpAPI.checkBook(context, it.title, it.author, it.id, books)
            CoreNlpAPI.nerTagger(getApplication(), book.toString(), book.title, book.author)


            val id = repository.addBookInfo(getApplication(), book)

            if (id < 0) {
                Log.e(TAG, "Error while saving book '${book.title}' to database")
                ToastUtils.toast(getApplication(), "Issue while loading")
            }

            books.value = getBooks().value


        }
    }

    private fun loadSelectedBook(selectedFile: Uri?): BookInfo {
        return loadBook(getApplication(), selectedFile)
    }


}

class MainViewModelFactory(
    private val application: Application,
    private val repository: BookRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(application, repository) as T
    }

}
