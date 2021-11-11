package com.romanp.fyp

import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.sentdetect.SentenceDetectorME
import opennlp.tools.sentdetect.SentenceModel
import opennlp.tools.tokenize.SimpleTokenizer
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.tokenize.TokenizerModel
import opennlp.tools.util.Span
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File
import java.io.InputStream

/**
 * This is an example of opennlp library on how it can be used as well as an example of unit tests
 */

// TODO: delete when not needed as an example
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun givenEnglishModel_whenTokenize_thenTokensAreDetected() {
        val inputStream: InputStream = File("res/nlpModels/exampleModels/en-token.bin").inputStream()
        val model = TokenizerModel(inputStream)
        val tokenizer = TokenizerME(model)
        val tokens = tokenizer.tokenize("Roman is working on his final year project")
        println("Tokens ${tokens.contentToString()}")

        assertEquals(
            listOf("Roman", "is", "working", "on", "his", "final", "year", "project").toString(),
            tokens.contentToString()
        )
    }

    @Test
    fun givenEnglishPersonModel_whenNER_thenPersonsAreDetected() {
        val tokenizer: SimpleTokenizer = SimpleTokenizer.INSTANCE
        val text: String = "Young Stamford looked rather strangely at me over his wineglass. " +
                "\"You don't know Sherlock Holmes yet,\" he said; \"perhaps you would not care " +
                "for him as a constant companion.\""
        val tokens: Array<String> = tokenizer.tokenize(text)

        // Initialisation
        val inputStreamNameFinder: InputStream = File("res/nlpModels/exampleModels/en-ner-person.bin").inputStream()
        // Loading the pretrained model
        val model = TokenNameFinderModel(inputStreamNameFinder)
        // Loading the model into ME stands for Maximum Entropy
        // NameFinderMe is not thread safe
        val nameFinderME = NameFinderME(model)

        val spans: List<Array<Span>> = listOf(nameFinderME.find(tokens))
        for (el in spans[0]) {
            print("Span $el ")
            println("Name ${tokens.slice(el.start until el.end).joinToString(separator = " ")}")
        }
        assertEquals("[[0..2) person, [17..19) person]", spans[0].contentToString())

    }

    @Test
    @Throws(Exception::class)
    fun givenEnglishModel_whenDetect_thenSentencesAreDetected() {
        val paragraph = "This is the first statement. This is another statement. " +
                "And now of course," +
                "we have another sentence. Good bye."

        val inputS: InputStream = File("res/nlpModels/exampleModels/en-sent.bin").inputStream()

        val model = SentenceModel(inputS)
        val sentenceDetector = SentenceDetectorME(model)
        val sentences = sentenceDetector.sentDetect(paragraph)

        val check = listOf("This is the first statement.",
            "This is another statement.",
            "And now of course,we have another sentence.",
            "Good bye.")

        assertEquals(
            check.toString(),
            sentences.contentToString()
        )
    }


}