package com.romanp.fyp.models.book

import java.io.Serializable

data class Chapter(
    val chapterTitle: String,
    val text: String
) : Serializable

data class BookInfo(
    val image: Int,
    val title: String,
    val author: String,
    val chapters: ArrayList<Chapter>,
    val locations: ArrayList<Entity>,
    val characters: ArrayList<Entity>,
    val characterDistanceByChapter: ArrayList<HashMap<String, Distance>>,

    ) : Serializable {
    fun isError() = image == -1
    fun getBodyText(): String {
        var bodyText = ""
        chapters.forEach { chapter ->
            bodyText += chapter.text
        }
        return bodyText
    }
}

fun getBookInfoErrorState() =
    BookInfo(-1, "", "", arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf())

