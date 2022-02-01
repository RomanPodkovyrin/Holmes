package com.server.utils

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.server.models.BookData
import com.server.models.Entity


fun extractUsefulTags(title: String, author: String, requestContent: String): BookData {
    //TODO: LOGGING
    val (characters: ArrayList<Entity>, locations: ArrayList<Entity>) = getCharactersAndLocations(requestContent)

    return BookData(title, author, characters = characters, locations = locations)
}


private fun getCharactersAndLocations(requestContent: String): Pair<ArrayList<Entity>, ArrayList<Entity>> {
    val jsonObject = JsonParser.parseString(requestContent).asJsonObject
    val characters: ArrayList<Entity> = arrayListOf()
    val locations: ArrayList<Entity> = arrayListOf()

    // Get Sentences which contain ["basicDependencies", "enhancedDependencies", "enhancedPlusPlusDependencies", "entitiymentions","tokens"]
    val sentences = jsonObject.get("sentences").asJsonArray

    // Get Co-references
    jsonObject.get("corefs").asJsonObject.entrySet().forEach { ref ->
        val corefs = ref.value

        val mentions: ArrayList<Pair<Int, Int>> = arrayListOf()
        val aliases: MutableSet<String> = mutableSetOf()
        val temp = corefs.asJsonArray.get(0).asJsonObject
        val ners: ArrayList<String> = arrayListOf()

        //TODO: record others that are not proper
        if (temp.get("type").asString != "PROPER") return@forEach

        corefs.asJsonArray.forEach { obj ->
            val name = obj.asJsonObject.get("text").asString
            if (obj.asJsonObject.get("type").asString == "PROPER") aliases.add(name.lowercase())

            getMentions(obj, sentences, ners, mentions)
        }

        val entity = Entity(
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
        }
    }

    return Pair(characters, locations)
}

private fun getMentions(
    obj: JsonElement,
    sentences: JsonArray,
    ners: ArrayList<String>,
    mentions: ArrayList<Pair<Int, Int>>
) {
    //Locations of mentions in tokens
    val position = obj.asJsonObject.get("position")
    // Token index range
    val startIndex = obj.asJsonObject.get("startIndex").asInt - 1
    val endIndex = obj.asJsonObject.get("endIndex").asInt - 2
    //Sentence number
    val index1 = position.asJsonArray.get(0).asInt - 1
    //TODO: what is this?
    val index2 = position.asJsonArray.get(1).asInt
//TODO: also combine entity mentions with tokens before hand??? so i can get ner confidence

    val token = sentences.get(index1).asJsonObject.get("tokens").asJsonArray

    val start = token.get(startIndex).asJsonObject
    val end = token.get(endIndex).asJsonObject
    if (start.get("ner").asString != "O") ners.add(start.get("ner").asString)
    mentions.add(
        Pair(
            start.get("characterOffsetBegin").asInt, end.get("characterOffsetEnd").asInt
        )
    )
}
