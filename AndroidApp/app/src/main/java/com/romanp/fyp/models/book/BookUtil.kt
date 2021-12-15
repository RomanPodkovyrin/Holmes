package com.romanp.fyp.models.book

import android.content.Context
import android.net.Uri
import android.util.Log
import com.romanp.fyp.R
import nl.siegmann.epublib.epub.EpubReader
import nl.siegmann.epublib.domain.Book as EpubBook
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.InputStream

class BookUtil {
    companion object {
        private const val TAG = "BookUtil"

        fun loadBook(context: Context, selectedFile: Uri?): BookInfo {
            val inputStreamNameFinder: InputStream? = selectedFile?.let {
                context.contentResolver.openInputStream(it)
            }

            if (inputStreamNameFinder == null) {
                Log.e(TAG, "There was an error while loading book $selectedFile")
                throw Error()
            }
            val epubReader: EpubReader = EpubReader()
            val book: nl.siegmann.epublib.domain.Book = epubReader.readEpub(inputStreamNameFinder)
            return processEpub(book)
        }

        fun processEpub(book: EpubBook): BookInfo {
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
            book.contents.forEach { chapter ->
                val doc: Document = Jsoup.parse(String(chapter.data))
                val temp = doc.body().select("h1, h2, h3, h4, h5, h6")
                temp.first()?.remove()
                val parts: ArrayList<String> = ArrayList()
                doc.body().html().split("<h(1|2|3|4|5|6)(.|\\n)*?<\\/h\\1>".toRegex())
                    .forEach { text ->
                        val text = Jsoup.parse(text).text()
                        if (text.isNotEmpty()) {
                            parts.add(text)
                        }

                    }

                //TODO: why is it only done for the first one ?
                val chapterTitle = if (temp == null) "" else temp.text()
//                println("Title $chapterTitle")
                contents.add(Chapter(chapterTitle,
                    parts.filter { part -> part != null || part == "" }
                        .joinToString(separator = "<br><br><br>------<br><br><br>")
                )
                )//doc.body().text()))
            }

            return BookInfo(R.drawable.ic_book_24, title, author, contents)
        }
    }
}