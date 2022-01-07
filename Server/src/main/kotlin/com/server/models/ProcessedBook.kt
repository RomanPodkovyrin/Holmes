package com.server.models

data class Entity(
    val characterOffsetBegin: Int,
    val characterOffsetEnd: Int,
    val pos: String,
    val ner: String,
    val name: String
)


data class ProcessedBook(
    val title: String,
    val author: String,
    val characters: ArrayList<Entity>,
    val locations: ArrayList<Entity>,


    )
