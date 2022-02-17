package com.romanp.fyp.models.book

//TODO: should probably be in corenlp folder
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
    var mentions: ArrayList<Pair<Int, Int>>,
    val byChapterMentions: ArrayList<ArrayList<Pair<Int, Int>>>
)

