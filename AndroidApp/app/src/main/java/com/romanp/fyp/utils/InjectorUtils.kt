package com.romanp.fyp.utils

import android.app.Application
import com.romanp.fyp.repositories.BookInfoRepository
import com.romanp.fyp.viewmodels.MainViewModelFactory

/**
 * Used to help with injecting dependencies to help with testing and refactoring
 */
object InjectorUtils {

    fun provideMainActivityViewModelFactory(application: Application): MainViewModelFactory {
        //TODO: try to remove the use application
        return MainViewModelFactory(application, BookInfoRepository.getInstance())
    }

}