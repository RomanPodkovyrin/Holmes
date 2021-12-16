package com.romanp.fyp.models.book

import com.google.gson.annotations.SerializedName
import com.romanp.fyp.R
import java.io.Serializable

data class Chapter(
    @SerializedName("chapterTitle") val chapterTitle: String,
    @SerializedName("text") val text: String
) : Serializable


data class BookInfo(
    @SerializedName("image") val image: Int,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("chapters") val chapters: ArrayList<Chapter>,
// TODO:   val chapters: another object consisting of chapter titles and text. What if it book doesn't have chapters
) : Serializable {


}