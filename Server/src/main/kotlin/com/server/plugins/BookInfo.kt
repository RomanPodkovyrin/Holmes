package com.server.plugins


import com.server.models.Entity
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
// TODO:   val chapters: another object consisting of chapter titles and text. What if it book doesn't have chapters
    val locations: ArrayList<Entity>,
    val characters: ArrayList<Entity>

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

fun getBookInfoErrorState() = BookInfo(-1, "", "", arrayListOf(), arrayListOf(), arrayListOf())
