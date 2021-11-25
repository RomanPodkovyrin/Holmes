package android.util

fun main(args: Array<String>) {

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
