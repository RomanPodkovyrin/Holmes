package com.server.utils

import com.google.gson.JsonParser
import com.server.models.Entity
import com.server.models.ProcessedBook


fun extractUsefulTags(title: String, author: String, requestContent: String): ProcessedBook {
    //TODO: LOGGING
    val jsonObject = JsonParser.parseString(requestContent).asJsonObject
    val person: ArrayList<Entity> = arrayListOf()
    val location: ArrayList<Entity> = arrayListOf()
    jsonObject.get("sentences").asJsonArray.forEach { sentence ->
        sentence.asJsonObject.get("tokens").asJsonArray.forEach { token ->
            val token = token.asJsonObject
            when (token.get("ner").asString) {
                "PERSON" -> person.add(
                    Entity(
                        token.get("characterOffsetBegin").asInt,
                        token.get("characterOffsetEnd").asInt,
                        token.get("pos").toString(),
                        token.get("ner").asString,
                        token.get("word").asString
                    )
                )
                "LOCATION", "CITY", "STATE_OR_PROVINCE", "COUNTRY" -> location.add(
                    Entity(
                        token.get("characterOffsetBegin").asInt,
                        token.get("characterOffsetEnd").asInt,
                        token.get("pos").toString(),
                        token.get("ner").asString,
                        token.get("word").asString
                    )
                )
            }
        }
    }
    return ProcessedBook(title, author, characters = person, locations = location)
}