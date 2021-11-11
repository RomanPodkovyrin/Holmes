package com.romanp.fyp.nlp

import android.content.Context
import android.util.Log
import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.SimpleTokenizer
import opennlp.tools.util.Span
import java.io.InputStream
import kotlin.collections.ArrayList


class nlpUtil {


    companion object {
        private const val TAG = "NLP Util"

        fun setUpModels(applicationContext: Context){
            val inputStreamNameFinder: InputStream = applicationContext.assets.open("en-ner-person.bin")
            val NerModel = TokenNameFinderModel(inputStreamNameFinder)
        }

        private fun getNerSpan(inputStream: InputStream, text: String, tokens: Array<String>):List<Array<Span>>{


            // Initialisation
            val inputStreamNameFinder: InputStream = inputStream
            // Loading the pretrained model
            Log.i(TAG, "Loaded file")
            val model = TokenNameFinderModel(inputStreamNameFinder)
            // Loading the model into ME stands for Maximum Entropy
            // NameFinderMe is not thread safe
            Log.i(TAG, "Model read")
            val nameFinderME = NameFinderME(model)
            Log.i(TAG, "Extracted names")
            val spans: List<Array<Span>> = listOf(nameFinderME.find(tokens))
            return spans
        }


        fun nerTagger(applicationContext: Context, text: String): MutableList<String> {
            Log.i(TAG, "Starting NER Tagger")
            val tokenizer: SimpleTokenizer = SimpleTokenizer.INSTANCE

            val tokens: Array<String> = tokenizer.tokenize(text)
            Log.i(TAG, "Tokenised text")
            val spans = getNerSpan(applicationContext.assets.open("en-ner-person.bin"), text, tokens)
            val names: MutableList<String> = ArrayList()
            val output: MutableList<String> = ArrayList()
//            for (el in spans[0]) {
//                print("Span $el ")
//                val tempName = tokens.slice(el.start until el.end).joinToString(separator = " ")
//                println("Name $tempName")
//                names.add(tempName)
//            }
//            for(i in spans[0].size-1..0) {
//                val el = spans[0][i]
//
//
//            }
            return names
        }
    }
}