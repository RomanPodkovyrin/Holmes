package com.server.models


data class BookData(
    val title: String,
    val author: String,
    val characters: ArrayList<Entity>,
    val locations: ArrayList<Entity>,
    val characterDistanceByChapter: ArrayList<HashMap<String, Distance>>,
)

data class Entity(
    val name: String,
    val aliases: Set<String>,
    val ner: String,
    val type: String, // PRONOMINAL, PROPER, NOMINAL
    val number: String, //SINGULAR, UNKNOWN, PLURAL
    val gender: String,//FEMALE, UNKNOWN, NEUTRAL, MALE
    val animacy: String, //ANIMATE, INANIMATE TODO: should it be a boolean?
    var mentions: ArrayList<Mention>,
    val byChapterMentions: ArrayList<ArrayList<Mention>>
)

data class Mention(
    var characterStart: Int,
    var characterEnd: Int,
    val tokenStart: Int,
    val tokenEnd: Int,
    val nerConfidences: Double
)

data class Distance(
    val tokenAverage: Int,
    val tokenMin: Int,
    val tokenMax: Int,
    val meanTokenDistance: Int,
    val medianTokenDistance: Int,
)

