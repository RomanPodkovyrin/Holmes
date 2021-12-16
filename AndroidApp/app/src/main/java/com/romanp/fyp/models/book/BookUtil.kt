package com.romanp.fyp.models.book

import android.content.Context
import android.net.Uri
import android.util.Log
import com.romanp.fyp.R
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.InputStream
import nl.siegmann.epublib.domain.Book as EpubBook

/**
 * BookUtil implement helper functions for loading, processing and analysing books
 */
class BookUtil {
    companion object {
        private const val TAG = "BookUtil"

        /**
         * Loads a book from specified location
         * @param context app context to use content resolver
         * @param selectedFile uri of the book file to be loaded
         *
         * @return BookInfo if the loading is successful, will throw and error if not
         * @throws Error when loading file
         */
        fun loadBook(context: Context, selectedFile: Uri?): BookInfo {
            val inputStreamNameFinder: InputStream? = selectedFile?.let {
                context.contentResolver.openInputStream(it)
            }

            if (inputStreamNameFinder == null) {
                Log.e(TAG, "There was an error while loading book $selectedFile")
                throw Error()
            }
            val epubReader = EpubReader()
            val book: nl.siegmann.epublib.domain.Book = epubReader.readEpub(inputStreamNameFinder)
            return processEpub(book)
        }

        /**
         * Removes html from the book
         *
         * @param book epub book with html elements to be removed
         * @return BookInfo object
         */
        fun processEpub(book: EpubBook): BookInfo {
            val bookTitle = book.title
            val author = book.metadata.authors.joinToString { "${it.firstname} ${it.lastname}" }
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


            val chapters: ArrayList<Chapter> = ArrayList()
            book.contents.forEach { chapter ->
                val doc: Document = Jsoup.parse(String(chapter.data))
                val title = doc.body().select("h1, h2, h3, h4, h5, h6")
                title.first()?.remove()
                val chapterParts: ArrayList<String> = ArrayList()
                doc.body().html().split("<h([123456])(.|\\n)*?</h\\1>".toRegex())
                    .forEach { contentText ->
                        val text = Jsoup.parse(contentText).text()
                        if (text.isNotEmpty()) {
                            chapterParts.add(text)
                        }

                    }

                //TODO: why is it only done for the first one ?
                val chapterTitle = if (title == null) "" else title.text()
                println("Title $chapterTitle")
                chapters.add(Chapter(chapterTitle,
                    chapterParts.filter { part -> part != null || part == "" }
                        .joinToString(separator = "<br><br><br>------<br><br><br>")
                )
                )
            }

            return BookInfo(R.drawable.ic_book_24, bookTitle, author, chapters)
        }
    }
}