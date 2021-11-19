package com.server.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.ArrayList

fun extractUsefulTags(requestContent: String): Pair<ArrayList<JsonObject>, ArrayList<JsonObject>> {
    //TODO: LOGGING
    val jsonObject = JsonParser.parseString(requestContent).asJsonObject
    val person: ArrayList<JsonObject> = arrayListOf()
    val location: ArrayList<JsonObject> = arrayListOf()
    jsonObject.get("sentences").asJsonArray.forEach { sentence ->
        sentence.asJsonObject.get("tokens").asJsonArray.forEach { token ->
            val token = token.asJsonObject
            when (token.get("ner").asString) {
                "PERSON" -> person.add(token)
                "LOCATION", "CITY", "STATE_OR_PROVINCE", "COUNTRY" -> location.add(token)
            }
        }
    }
    return Pair(person, location)
}