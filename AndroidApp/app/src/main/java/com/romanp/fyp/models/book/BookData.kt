package com.romanp.fyp.models.book

//TODO: should probably be in corenlp folder
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
    val tokenAverage: Float,
    val meanTokenDistance: Int,
    val medianTokenDistance: Int,
    val averagePunctuationDistance: HashMap<Char, Float>
)
