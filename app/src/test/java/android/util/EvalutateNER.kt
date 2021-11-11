package com.romanp.fyp

import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.NameSample
import opennlp.tools.namefind.TokenNameFinderEvaluator
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.util.ObjectStream
import opennlp.tools.util.PlainTextByLineStream
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets


fun main(args: Array<String>) {
//    print("hello")
//    val inputStreamNameFinder: InputStream = File("res/nlpModels/exampleModels/en-ner-person.bin").inputStream()
//    // Loading the pretrained model
//    val model = TokenNameFinderModel(inputStreamNameFinder)
//    val evaluator : TokenNameFinderEvaluator = TokenNameFinderEvaluator(NameFinderME(model))
//    val sampleDataIn = FileInputStream("en-ner-person.train")
//    val sampleStream: ObjectStream<NameSample> = PlainTextByLineStream(sampleDataIn.getChannel(), StandardCharsets.UTF_8)
//    evaluator.evaluate(sampleStream)
//
//    val result = evaluator.fMeasure
//
//    println(result.toString())

}

fun accuracy(tp:Int, tn:Int, fp:Int, fn:Int) : Int {
    val top = (tp + tn)
    val bottom = (tp+tn+fp+fn)

    if (bottom == 0) return 0

    return top / bottom
}

fun precision(tpClassified:Int, classified: Int): Int {
    if (classified == 0) return 0
    return tpClassified/classified
}

fun recall(tpClassified: Int, tpInCorpus: Int) : Int {
    if (tpInCorpus == 0) return 0
    return tpClassified/tpInCorpus
}

fun fCalc(precision: Int, recall: Int) : Int {
    val top = precision * recall
    val bottom = precision + recall

    if (bottom == 0) return 0
    return top / bottom
}
