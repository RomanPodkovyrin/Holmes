package com.romanp.fyp.book

class Chapter (
    val chapterTitle: String,
    val text: String
        )


class Book (
    val title: String,
    val author: String,
//    val chapters: ArrayList<String>,
    val chapters: ArrayList<Chapter>,
//    val chapters: another object consisting of chapter titles and text. What if it book doesn't have chapters
) {





}