package com.server.models


data class BookData(
    val title: String,
    val author: String,
    val characters: ArrayList<Entity>,
    val locations: ArrayList<Entity>,
)

data class Entity(
    val name: String,
    val aliases: Set<String>,
    val ner: String,
    val type: String, // PRONOMINAL, PROPER, NOMINAL
    val number: String, //SINGULAR, UNKNOWN, PLURAL
    val gender: String,//FEMALE, UNKNOWN, NEUTRAL, MALE
    val animacy: String, //ANIMATE, INANIMATE TODO: should it be a boolean?
    val mentions: ArrayList<Pair<Int, Int>>,// Pair<StartIndex, EndIndex>
//    val characterOffsetBegin: Int,
//    val characterOffsetEnd: Int,
)
