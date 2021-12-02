package com.romanp.fyp.models.book

import com.romanp.fyp.R
import java.io.Serializable

data class Chapter (
    val chapterTitle: String,
    val text: String
        ) : Serializable


data class BookInfo (
    val image: Int,
    val title: String,
    val author: String,
//    val chapters: ArrayList<String>,
    val chapters: ArrayList<Chapter>,
//    val chapters: another object consisting of chapter titles and text. What if it book doesn't have chapters
): Serializable {


//    override fun compareTo(other: Book) = when {
//        title < other.title -> -1
//        title > other.title -> 1
//        else -> 0
//    }


}