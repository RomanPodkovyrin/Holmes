package com.server.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.server.models.BookData
import com.server.models.Distance
import com.server.models.Entity
import com.server.models.Mention
import com.server.plugins.Chapter
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.streams.toList

private val locationTAGS = arrayListOf("LOCATION", "CITY", "STATE_OR_PROVINCE", "COUNTRY")
private val characterTAGS = arrayListOf("PERSON")

/**
 * Extracts useful information from CoreNLP processed files
 */
fun extractUsefulTags(title: String, author: String, requestContent: String, chapters: ArrayList<Chapter>): BookData {
    //TODO: LOGGING
    val (characters: ArrayList<Entity>, locations: ArrayList<Entity>) = getEntityMentions(requestContent)

    sortByChapter(chapters, characters, locations)

    // Check if there are duplicates in location and character and remove
//    removeCharacterAndLocationDuplicates(characters, locations)

    val characterDistanceByChapter = calculateDistancesBetweenCharacters(chapters, characters)


//    println("Character distances")
//    characterDistanceByChapter.forEach { chapter ->
//        println("########")
//        chapter.forEach { (t, u) ->
//            println("$t: $u")
//        }
//    }


    return BookData(
        title,
        author,
        characters = characters,
        locations = locations,
        characterDistanceByChapter.toCollection(ArrayList())
    )
}

private fun calculateDistancesBetweenCharacters(
    chapters: ArrayList<Chapter>,
    characters: ArrayList<Entity>
): Array<HashMap<String, Distance>> {
    // <source name>,< target name>
    val characterDistanceByChapter = Array<HashMap<String, Distance>>(chapters.size) { HashMap() }

    // Calculate character distances
    for (i in characters.indices) {
        val iCharacter = characters[i]
        val iLocationsByChapter = arrayListOf<ArrayList<Int>>()
        getTokenLocationByChapter(iCharacter, iLocationsByChapter)

        // Starting from "i" as once before have already been processed
        // +1 to not check characters against themselves
        for (j in i + 1 until characters.size) {
            val jCharacter = characters[j]
            val jLocationsByChapter = arrayListOf<ArrayList<Int>>()
            getTokenLocationByChapter(jCharacter, jLocationsByChapter)

            // Calculate distance between i and j
            // For each chapter and the whole book
//            var bookTotal = 0
//            var bookMinDistance = Int.MAX_VALUE
//            var bookMaxDistance = 0
            val numberOfChapters = min(iLocationsByChapter.size, jLocationsByChapter.size)
            for (chapterIndex in 0 until numberOfChapters) {

                var total = 0
                var minDistance = Int.MAX_VALUE
                var maxDistance = 0

                val iChapter = iLocationsByChapter[chapterIndex]
                val jChapter = jLocationsByChapter[chapterIndex]
                if (iChapter.isEmpty() || jChapter.isEmpty()) continue
                val (iChapterMeanLocation, iChapterMedianLocation) = getMeanAndMedianLocations(iChapter)

                val (jChapterMeanLocation, jChapterMedianLocation) = getMeanAndMedianLocations(jChapter)


                iChapter.forEach { iLocation ->
                    jChapter.forEach { jLocation ->
                        val distance = abs(iLocation - jLocation)
                        total += distance
                        minDistance = min(minDistance, distance)
                        maxDistance = max(maxDistance, distance)
                    }
                }

                val meanTokenDistance = abs(jChapterMeanLocation - iChapterMeanLocation)
                val medianTokenDistance = abs(jChapterMedianLocation - iChapterMedianLocation)

                val averageDistance = total / (iChapter.size * jChapter.size)
                //TODO: also calculate number of punctuation between it
                // Slice chapter between character end and start and calculate number of punctuation
                //. , ? : ; !
                characterDistanceByChapter[chapterIndex]["${iCharacter.name},${jCharacter.name}"] =
                    Distance(averageDistance, minDistance, maxDistance, meanTokenDistance, medianTokenDistance)


                // Whole Book
//                bookTotal += averageDistance
//                bookMinDistance = min(bookMinDistance, minDistance)
//                bookMaxDistance = max(bookMaxDistance, maxDistance)
            }
            // TODO: Issues
            //  Problem 1: Not every character appears in each chapter therefore dividing by chapters does not make sense
            // Problem 2:
//            characterDistance["wholeBook,${iCharacter.name},${jCharacter.name}"] =
//                Distance(bookTotal/numberOfChapters, bookMinDistance, bookMaxDistance)


        }
    }
    return characterDistanceByChapter
}

private fun getMeanAndMedianLocations(iChapter: ArrayList<Int>): Pair<Int, Int> {
    val iChapterMeanLocation = iChapter.toIntArray().sum() / iChapter.size
    val iChapterMedianLocation = (iChapter.toIntArray().sortedArray()).let {
        val size = it.size
        if (size % 2 == 1) {
            it[size / 2]
        } else {
            (it[(size / 2) - 1] + it[size / 2]) / 2
        }
    }
    return Pair(iChapterMeanLocation, iChapterMedianLocation)
}

private fun removeCharacterAndLocationDuplicates(
    characters: ArrayList<Entity>,
    locations: ArrayList<Entity>
) {
    for (c in characters.indices) {
        val character = characters[c]
        var temp = arrayListOf<String>()
        character.mentions.forEach { temp.add(it.nerConfidences.toString()) }
        val cConfidence = temp.toString()
//        println("Checking character ${character.name}")
        for (l in locations.indices) {
            val location = locations[l]
            temp = arrayListOf()
            location.mentions.forEach { temp.add(it.nerConfidences.toString()) }
            val lConfidence = temp.toString()
            if (location.name == character.name) {
                println("Match between l: ${location.name} with $lConfidence and c: ${character.name} with $cConfidence")
            }

        }
    }
}

private fun getTokenLocationByChapter(
    iCharacter: Entity,
    iLocationsByChapter: ArrayList<ArrayList<Int>>
) {

    //TODO: can this be calculated when getting entities form JSON?
    iCharacter.byChapterMentions.forEach { iChapter ->
        val iLocations = arrayListOf<Int>()
        iChapter.forEach { iMention ->
            val location = (iMention.tokenStart + iMention.tokenEnd) / 2
            iLocations.add(location)
        }
        iLocationsByChapter.add(iLocations)
    }
}

private fun sortByChapter(
    chapters: ArrayList<Chapter>,
    characters: ArrayList<Entity>,
    locations: ArrayList<Entity>
) {
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
            val matchedMentions: ArrayList<Mention> = getMentionBetweenBounds(character, lowerBound, upperBound)
            normalizeSpan(matchedMentions, lowerBound)
            character.byChapterMentions.add(matchedMentions)

        }

        // Sort Locations by chapters
        locations.forEach { location ->
            val matchedMentions: ArrayList<Mention> = getMentionBetweenBounds(location, lowerBound, upperBound)
            normalizeSpan(matchedMentions, lowerBound)
            location.byChapterMentions.add(matchedMentions)
        }
    }
}

/**
 * Normalise start and end of mentions so that they can be found withing each of the chapter
 */
private fun normalizeSpan(
    matchedMentions: ArrayList<Mention>, lowerBound: Int
) {
    matchedMentions.forEach { match ->
        match.characterStart = match.characterStart - lowerBound
        match.characterEnd = match.characterEnd - lowerBound
    }
//    for (i in matchedMentions.indices) {
//        val mentionSpan = matchedMentions[i]
//        matchedMentions[i] = Pair(
//            mentionSpan.characterStart - lowerBound, mentionSpan.characterEnd - lowerBound
//        )
//    }
}

/**
 * Get all mentions between lower and upper bound of the book chapter text
 */
private fun getMentionBetweenBounds(
    character: Entity, lowerBound: Int, upperBound: Int
) = ArrayList(character.mentions.stream().filter { mention ->
    mention.characterStart <= upperBound && mention.characterEnd <= upperBound && mention.characterStart >= lowerBound && mention.characterEnd >= lowerBound
}.toList())

/**
 * Get entity mentions from corenlp
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
    println("Object ${entity.toString()}")
    // Extract useful info
    val entityNER = entity.get("ner").asString
    val name = entity.get("text").asString
    val characterStart = entity.get("characterOffsetBegin").asInt
    val characterEnd = entity.get("characterOffsetEnd").asInt
    val tokenStart = entity.get("docTokenBegin").asInt
    val tokenEnd = entity.get("docTokenEnd").asInt
    val nerConfidences =
        entity.get("nerConfidences").asJsonObject.entrySet().stream().map { it.value.asDouble }.toList()[0]

    if (characterTAGS.contains(entityNER)) {
//        val nerConfidences = entity.get("nerConfidences").asJsonObject.get(entityNER).asDouble
        updateHM(characterHashMap, name, characterStart, characterEnd, tokenStart, tokenEnd, entityNER, nerConfidences)
    } else if (locationTAGS.contains(entityNER)) {
//        val nerConfidences = entity.get("nerConfidences").asJsonObject.get("LOCATION").asDouble
        updateHM(locationHashMap, name, characterStart, characterEnd, tokenStart, tokenEnd, entityNER, nerConfidences)
    }


}

private fun updateHM(
    entityHashMap: HashMap<String, Entity>,
    name: String,
    characterStart: Int,
    characterEnd: Int,
    tokenStart: Int,
    tokenEnd: Int,
    entityNER: String,
    nerConfidences: Double
) {
    val mention = Mention(characterStart, characterEnd, tokenStart, tokenEnd, nerConfidences)
    if (entityHashMap.contains(name)) {
        entityHashMap[name]?.mentions?.add(mention)
    } else {

        val newEntity = Entity(
            name,
            setOf(),
            entityNER,
            "",
            "",
            "",
            "",
            arrayListOf(mention),
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
//    val index2 = position.asJsonArray.get(1).asInt
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
