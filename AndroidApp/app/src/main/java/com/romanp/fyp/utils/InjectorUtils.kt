package com.romanp.fyp.utils

import android.app.Application
import com.romanp.fyp.repositories.BookRepository
import com.romanp.fyp.viewmodels.BookReaderViewModelFactory
import com.romanp.fyp.viewmodels.MainViewModelFactory

/**
 * Used to help with injecting dependencies to help with testing and refactoring
 */
object InjectorUtils {

    fun provideMainActivityViewModelFactory(application: Application): MainViewModelFactory {
        //TODO: try to remove the use application
        return MainViewModelFactory(application, BookRepository.getInstance())
    }

    fun provideBookReaderActivityViewModelFactor(
        application: Application,
        bookId: Long
    ): BookReaderViewModelFactory {
        return BookReaderViewModelFactory(application, BookRepository.getInstance(), bookId)
    }

}