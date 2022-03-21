package com.romanp.fyp.utils

import android.app.Application
import com.romanp.fyp.repositories.BookRepository
import com.romanp.fyp.viewmodels.BookReaderViewModelFactory
import com.romanp.fyp.viewmodels.EntityListActivityViewModelFactory
import com.romanp.fyp.viewmodels.MainViewModelFactory
import com.romanp.fyp.viewmodels.graph.BookGraphActivityViewModelFactory

/**
 * Used to help with injecting dependencies to help with testing and refactoring
 */
object InjectorUtils {

    fun provideMainActivityViewModelFactory(application: Application): MainViewModelFactory {
        return MainViewModelFactory(application, BookRepository.getInstance())
    }

    fun provideBookReaderActivityViewModelFactory(
        application: Application,
        bookId: Long
    ): BookReaderViewModelFactory {
        return BookReaderViewModelFactory(application, BookRepository.getInstance(), bookId)
    }

    fun provideEntityListActivityViewModelFactory(
        application: Application,
        bookId: Long,
        listType: Boolean
    ): EntityListActivityViewModelFactory {
        return EntityListActivityViewModelFactory(
            application,
            BookRepository.getInstance(),
            bookId,
            listType
        )
    }

    fun provideBookGraphActivityViewModelFactory(
        application: Application,
        bookId: Long
    ): BookGraphActivityViewModelFactory {
        return BookGraphActivityViewModelFactory(application, BookRepository.getInstance(), bookId)
    }
}