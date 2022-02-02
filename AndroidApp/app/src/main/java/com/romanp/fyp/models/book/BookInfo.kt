package com.romanp.fyp.models.book

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Chapter(
    @SerializedName("chapterTitle") val chapterTitle: String,
    @SerializedName("text") val text: String
) : Serializable

data class Entity(
    val name: String,
    val aliases: Set<String>,
    val ner: String,
    val type: String, // PRONOMINAL, PROPER, NOMINAL
    val number: String, //SINGULAR, UNKNOWN, PLURAL
    val gender: String,//FEMALE, UNKNOWN, NEUTRAL, MALE
    val animacy: String, //ANIMATE, INANIMATE TODO: should it be a boolean?
    val mentions: ArrayList<Pair<Int, Int>>,// Pair<StartIndex, EndIndex>
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
    fun isError() = image == -1
}

fun getBookInfoErrorState() = BookInfo(-1, "", "", arrayListOf(), arrayListOf(), arrayListOf())

//TODO: should probably be in corenlp folder
//TODO: change name to BookData?
data class ProcessedBook(
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("characters") val chapters: ArrayList<Entity>,
    @SerializedName("locations") val locations: ArrayList<Entity>,
)
