package com.server.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.server.models.BookData
import com.server.models.Entity
import com.server.plugins.Chapter
import kotlin.streams.toList

private val locationTAGS = arrayListOf("LOCATION", "CITY", "STATE_OR_PROVINCE", "COUNTRY")
private val characterTAGS = arrayListOf("PERSON")

/**
 * Extracts useful information from CoreNLP processed files
 */
fun extractUsefulTags(title: String, author: String, requestContent: String, chapters: ArrayList<Chapter>): BookData {
    //TODO: LOGGING
    val (characters: ArrayList<Entity>, locations: ArrayList<Entity>) = getEntityMentions(requestContent)

    // Calculate lower and upper bound word counts for each chapter
    var currentTotalWordCount = 0
    val chapterLimits = arrayListOf<Pair<Int, Int>>()
    chapters.forEach { chapter ->
        val lowerBound = currentTotalWordCount
        currentTotalWordCount += chapter.text.length
        val upperBound = currentTotalWordCount
        chapterLimits.add(Pair(lowerBound, upperBound))
    }

    // Sort characters and locations mentions into chapters and normalise the start and end values
    chapters.forEachIndexed { chapterIndex, _ ->
        val lowerBound = chapterLimits[chapterIndex].first
        val upperBound = chapterLimits[chapterIndex].second

        // Sort Characters by chapters
        characters.forEach { character ->
            val matchedMentions: ArrayList<Pair<Int, Int>> = getMentionBetweenBounds(character, lowerBound, upperBound)
            normalizeSpan(matchedMentions, lowerBound)
            character.byChapterMentions.add(matchedMentions)

        }

        // Sort Locations by chapters
        locations.forEach { location ->
            val matchedMentions: ArrayList<Pair<Int, Int>> = getMentionBetweenBounds(location, lowerBound, upperBound)
            normalizeSpan(matchedMentions, lowerBound)
            location.byChapterMentions.add(matchedMentions)
        }
    }


    return BookData(title, author, characters = characters, locations = locations)
}

/**
 * Normalise start and end of mentions so that they can be found withing each of the chapter
 */
private fun normalizeSpan(
    matchedMentions: ArrayList<Pair<Int, Int>>, lowerBound: Int
) {
    for (i in matchedMentions.indices) {
        val mentionSpan = matchedMentions[i]
        matchedMentions[i] = Pair(
            mentionSpan.first - lowerBound, mentionSpan.second - lowerBound
        )
    }
}

/**
 * Get all mentions between lower and upper bound of the book chapter text
 */
private fun getMentionBetweenBounds(
    character: Entity, lowerBound: Int, upperBound: Int
) = ArrayList(character.mentions.stream().filter { mention ->
    mention.first <= upperBound && mention.second <= upperBound && mention.first >= lowerBound && mention.second >= lowerBound
}.toList())

/**
 * Get entity mentions form corenlp
 */
private fun getEntityMentions(requestContent: String): Pair<ArrayList<Entity>, ArrayList<Entity>> {
    val jsonObject = JsonParser.parseString(requestContent).asJsonObject
    val characters: ArrayList<Entity> = arrayListOf()
    val locations: ArrayList<Entity> = arrayListOf()

    val characterHashMap: HashMap<String, Entity> = HashMap()
    val locationHashMap: HashMap<String, Entity> = HashMap()

    val sentences = jsonObject.get("sentences").asJsonArray

    sentences.forEach { sentence ->
        val entityMentions = sentence.asJsonObject.get("entitymentions").asJsonArray
        entityMentions.forEach { it ->
            val entity = it.asJsonObject
            if (entity.keySet().contains("nerConfidences")) {
                saveEntity(entity, characterHashMap, locationHashMap)

            }

        }
    }

    // Move Entities from HashMap to ArrayList
    characterHashMap.forEach { characters.add(it.value) }
    locationHashMap.forEach { locations.add(it.value) }

    return Pair(characters, locations)
}

private fun saveEntity(
    entity: JsonObject,
    characterHashMap: HashMap<String, Entity>,
    locationHashMap: HashMap<String, Entity>
) {
    // Extract useful info
    //TODO: ADD ner Confidence?
    val entityNER = entity.get("ner").asString
    val name = entity.get("text").asString
    val start = entity.get("characterOffsetBegin").asInt
    val end = entity.get("characterOffsetEnd").asInt

    if (characterTAGS.contains(entityNER)) {
        updateHM(characterHashMap, name, start, end, entityNER)
    } else if (locationTAGS.contains(entityNER)) {
        updateHM(locationHashMap, name, start, end, entityNER)
    }


}

private fun updateHM(
    entityHashMap: HashMap<String, Entity>,
    name: String,
    start: Int,
    end: Int,
    entityNER: String
) {
    if (entityHashMap.contains(name)) {
        entityHashMap[name]?.mentions?.add(Pair(start, end))
    } else {

        val newEntity = Entity(
            name,
            setOf(),
            entityNER,
            "",
            "",
            "",
            "",
            arrayListOf(Pair(start, end)),
            arrayListOf()
        )
        entityHashMap[name] = newEntity
    }
}
//
//private fun getCharactersAndLocations(requestContent: String): Pair<ArrayList<Entity>, ArrayList<Entity>> {
//    val jsonObject = JsonParser.parseString(requestContent).asJsonObject
//    val characters: ArrayList<Entity> = arrayListOf()
//    val locations: ArrayList<Entity> = arrayListOf()
//
//    // Get Sentences which contain ["basicDependencies", "enhancedDependencies", "enhancedPlusPlusDependencies", "entitiymentions","tokens"]
//    val sentences = jsonObject.get("sentences").asJsonArray
//
//    // Get Co-references
//    jsonObject.get("corefs").asJsonObject.entrySet().forEach { ref ->
//        val corefs = ref.value
//
//        val mentions: ArrayList<Pair<Int, Int>> = arrayListOf()
//        val aliases: MutableSet<String> = mutableSetOf()
//        val temp = corefs.asJsonArray.get(0).asJsonObject
//        val ners: ArrayList<String> = arrayListOf()
//
//        //TODO: record others that are not proper
//        if (temp.get("type").asString != "PROPER") return@forEach
//
//        corefs.asJsonArray.forEach { obj ->
//            val name = obj.asJsonObject.get("text").asString
//            if (obj.asJsonObject.get("type").asString == "PROPER") aliases.add(name.lowercase())
//
//            getMentions(obj, sentences, ners, mentions)
//        }
//
//        val entity = Entity(
//            temp.get("text").asString,
//            aliases,
//            ners.toString(),
//            temp.get("type").asString,
//            temp.get("number").asString,
//            temp.get("gender").asString,
//            temp.get("animacy").asString,
//            mentions,
//            arrayListOf()
//        )
//        if (entity.ner != "[]") {
//            if (ners.contains("PERSON")) characters.add(entity)
//            else if (arrayListOf("LOCATION", "CITY", "STATE_OR_PROVINCE", "COUNTRY").map { ners.contains(it) }
//                    .toBooleanArray().contains(true)) locations.add(entity)
//        }
//    }
//
//    return Pair(characters, locations)
//}
//
//private fun getMentions(
//    obj: JsonElement, sentences: JsonArray, ners: ArrayList<String>, mentions: ArrayList<Pair<Int, Int>>
//) {
//    //Locations of mentions in tokens
//    val position = obj.asJsonObject.get("position")
//    // Token index range
//    val startIndex = obj.asJsonObject.get("startIndex").asInt - 1
//    val endIndex = obj.asJsonObject.get("endIndex").asInt - 2
//    //Sentence number
//    val index1 = position.asJsonArray.get(0).asInt - 1
//    //TODO: what is this?
//    val index2 = position.asJsonArray.get(1).asInt
////TODO: also combine entity mentions with tokens before hand??? so i can get ner confidence
//
//    val token = sentences.get(index1).asJsonObject.get("tokens").asJsonArray
//
//    val start = token.get(startIndex).asJsonObject
//    val end = token.get(endIndex).asJsonObject
//    if (start.get("ner").asString != "O") ners.add(start.get("ner").asString)
//    mentions.add(
//        Pair(
//            start.get("characterOffsetBegin").asInt, end.get("characterOffsetEnd").asInt
//        )
//    )
//}
