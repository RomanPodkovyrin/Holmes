package com.server.utils

import com.google.gson.JsonParser
import com.server.models.BookData
import com.server.models.Entity


fun extractUsefulTags(title: String, author: String, requestContent: String): BookData {
    //TODO: LOGGING
//    val jsonObject = JsonParser.parseString(requestContent).asJsonObject
    val (characters: ArrayList<Entity>, locations: ArrayList<Entity>) = getCharactersAndLocations(requestContent)

    return BookData(title, author, characters = characters, locations = locations)
}


private fun getCharactersAndLocations(requestContent: String): Pair<ArrayList<Entity>, ArrayList<Entity>> {
    val jsonObject = JsonParser.parseString(requestContent).asJsonObject
    val characters: ArrayList<Entity> = arrayListOf()
    val locations: ArrayList<Entity> = arrayListOf()
    val sentences = jsonObject.get("sentences").asJsonArray

    val coref = jsonObject.get("corefs").asJsonObject.entrySet().forEach { ref ->
        val refT = ref.value
        val mentions: ArrayList<Pair<Int, Int>> = arrayListOf()
        val aliases: MutableSet<String> = mutableSetOf()
        val temp = refT.asJsonArray.get(0).asJsonObject
        val ners: ArrayList<String> = arrayListOf()
        if (temp.get("type").asString != "PROPER") return@forEach
        println(temp.get("text").asString)
        refT.asJsonArray.forEach { obj ->
            val name = obj.asJsonObject.get("text").asString
            //TODO: record others that are not proper
            if (obj.asJsonObject.get("type").asString == "PROPER" || true) {
                if (obj.asJsonObject.get("type").asString == "PROPER") aliases.add(name.lowercase())
                val position = obj.asJsonObject.get("position")
                val startIndex = obj.asJsonObject.get("startIndex").asInt - 1
                val endIndex = obj.asJsonObject.get("endIndex").asInt - 2
                val index1 = position.asJsonArray.get(0).asInt - 1
                val index2 = position.asJsonArray.get(1).asInt
//TODO: also combine entity mentions with tokens before hand??? so i can get ner confidence

                val token = sentences.get(index1).asJsonObject.get("tokens").asJsonArray

                val start = token.get(startIndex).asJsonObject
                val end = token.get(endIndex).asJsonObject
                if (start.get("ner").asString != "O") ners.add(start.get("ner").asString)
                mentions.add(
                    Pair(
                        start.get("characterOffsetBegin").asInt,
                        end.get("characterOffsetEnd").asInt
                    )
                )
            }
        }
        val entity: Entity = Entity(
            temp.get("text").asString,
            aliases,
            ners.toString(),
            temp.get("type").asString,
            temp.get("number").asString,
            temp.get("gender").asString,
            temp.get("animacy").asString,
            mentions
        )
        if (entity.ner != "[]") {
            if (ners.contains("PERSON")) characters.add(entity)
            else if (arrayListOf("LOCATION", "CITY", "STATE_OR_PROVINCE", "COUNTRY").map { ners.contains(it) }
                    .toBooleanArray().contains(true)) locations.add(entity)
//            characters.add(entity)
        }
    }

    return Pair(characters, locations)
//    val book = BookData("Title", "Author", characters, locations)
//    File("book.json").writeText(book.json)
}


//jsonObject.get("sentences").asJsonArray.forEach { sentence ->
//        sentence.asJsonObject.get("tokens").asJsonArray.forEach { token ->
//            val token = token.asJsonObject
//            when (token.get("ner").asString) {
//                "PERSON" -> person.add(
//                    Entity(
//                        token.get("characterOffsetBegin").asInt,
//                        token.get("characterOffsetEnd").asInt,
//                        token.get("pos").toString(),
//                        token.get("ner").asString,
//                        token.get("word").asString
//                    )
//                )
//                "LOCATION", "CITY", "STATE_OR_PROVINCE", "COUNTRY" -> location.add(
//                    Entity(
//                        token.get("characterOffsetBegin").asInt,
//                        token.get("characterOffsetEnd").asInt,
//                        token.get("pos").toString(),
//                        token.get("ner").asString,
//                        token.get("word").asString
//                    )
//                )
//            }
//        }
//    }
