package com.romanp.fyp.models.book

import com.google.gson.annotations.SerializedName
import com.romanp.fyp.R
import java.io.Serializable

data class Chapter(
    @SerializedName("chapterTitle") val chapterTitle: String,
    @SerializedName("text") val text: String
) : Serializable

data class Entity(
    val characterOffsetBegin: Int,
    val characterOffsetEnd: Int,
    val pos: String,
    val ner: String,
    val name: String
)

data class BookInfo(
    @SerializedName("image") val image: Int,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("chapters") val chapters: ArrayList<Chapter>,
// TODO:   val chapters: another object consisting of chapter titles and text. What if it book doesn't have chapters
    val locations: ArrayList<Entity>,
    val characters: ArrayList<Entity>

) : Serializable {


}

//TODO: should probably be in corenlp folder
data class ProcessedBook(
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("characters") val chapters: ArrayList<Entity>,
    @SerializedName("locations") val locations: ArrayList<Entity>,
)
