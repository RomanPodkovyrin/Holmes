package com.romanp.fyp.book

import nl.siegmann.epublib.domain.Book as EpubBook
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class BookUtil {
    companion object{
        fun processEpub(book: EpubBook): Book{
            val title = book.title
            val author = book.metadata.authors.toString()
            book.metadata.dates
            book.metadata.contributors
            book.metadata.descriptions
            book.metadata.firstTitle
            book.metadata.format
            book.metadata.identifiers
            book.metadata.language
            book.metadata.publishers
            book.metadata.subjects
            book.metadata.titles


            val contents: ArrayList<Chapter> = ArrayList()
            book.contents.forEach {chapter ->
                val doc: Document = Jsoup.parse(String(chapter.data))
                val temp = doc.body().select("h1, h2, h3, h4, h5, h6")
                temp.first()?.remove()
                val parts: ArrayList<String> = ArrayList()
                doc.body().html().split("<h(1|2|3|4|5|6)(.|\\n)*?<\\/h\\1>".toRegex()).forEach { text->
                    val text = Jsoup.parse(text).text()
                    if (text.isNotEmpty()){
                        parts.add(text)
                        println("content \"${text}\"")
                    }

                }

                //TODO: why is it only done for the first one ?
                val chapterTitle = if (temp == null) ""  else temp.text()
                println("Title $chapterTitle")
                contents.add(Chapter(chapterTitle,parts.filter {part -> part != null || part == "" }.joinToString(separator = "<br><br><br>------<br><br><br>")))//doc.body().text()))
            }

            return Book(title, author , contents)
        }
    }
}