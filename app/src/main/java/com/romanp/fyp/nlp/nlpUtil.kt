package com.romanp.fyp.nlp

import android.content.Context
import android.util.Log
import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.SimpleTokenizer
import opennlp.tools.util.Span
import java.io.InputStream

class nlpUtil {


    companion object {
        private const val TAG = "NLP Util"
        fun nerTagger(applicationContext: Context, text: String): MutableList<String> {
            Log.i(TAG, "Starting NER Tagger")
            val tokenizer: SimpleTokenizer = SimpleTokenizer.INSTANCE

//        val text: String = "Young Stamford looked rather strangely at me over his wineglass. " +
//                "\"You don't know Sherlock Holmes yet,\" he said; \"perhaps you would not care " +
//                "for him as a constant companion.\""
            val tokens: Array<String> = tokenizer.tokenize(text)
            Log.i(TAG, "Tokenised text")

            // Initialisation
//            R
            val inputStreamNameFinder: InputStream = applicationContext.assets.open("en-ner-person.bin")
//                File("res/nlpModels/exampleModels/en-ner-person.bin").inputStream()
//            val inputStreamNameFinder: InputStream = getResources().open
//                File("res/nlpModels/exampleModels/en-ner-person.bin").inputStream()
            // Loading the pretrained model
            Log.i(TAG, "Loaded file")
            val model = TokenNameFinderModel(inputStreamNameFinder)
            // Loading the model into ME stands for Maximum Entropy
            // NameFinderMe is not thread safe
            Log.i(TAG, "Model read")
            val nameFinderME = NameFinderME(model)
            Log.i(TAG, "Extracted names")
            val spans: List<Array<Span>> = listOf(nameFinderME.find(tokens))
            val names: MutableList<String> = ArrayList()
            for (el in spans[0]) {
                print("Span $el ")
                val tempName = tokens.slice(el.start until el.end).joinToString(separator = " ")
                println("Name $tempName")
                names.add(tempName)
            }
            return names
        }
    }
}