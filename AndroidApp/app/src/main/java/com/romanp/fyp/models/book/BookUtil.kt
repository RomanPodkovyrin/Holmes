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
            book.coverImage

            val chapters: ArrayList<Chapter> = ArrayList()

            var index = 1
            book.contents.forEach { chapter ->
                val doc: Document = Jsoup.parse(String(chapter.data))

                val subChapters: ArrayList<String> = ArrayList()

                // Split on titles with n tag and treat them as separate parts of a chapter
                // Really only happens when book has sub chapters
                // or when it has chapter on the page, this method removes it from the page
                splitIntoSubchapters(doc, subChapters)

                // Now join subchapters back into one chapter with line separators.
                if (subChapters.isNotEmpty()) {
                    Log.d(
                        TAG,
                        "Book $bookTitle hash subchapters in chapter"
                    )
                    joinSubChapters("Chapter $index", chapters, subChapters)
                    index++
                }
            }

            return BookInfo(
                R.drawable.ic_book_24,
                bookTitle,
                author,
                chapters,
                ArrayList(),
                ArrayList(),
                ArrayList()
            )
        }

        private fun splitIntoSubchapters(
            doc: Document,
            subChapters: ArrayList<String>
        ) {
            doc.body().html().split("<h([123456])(.|\\n)*?</h\\1>".toRegex())
                .forEach { contentText ->
                    val text = Jsoup.parse(contentText).text()
                    if (text.isNotEmpty()) {
                        subChapters.add(text)
                    }

                }
        }

        private fun joinSubChapters(
            chapterTitle: String,
            chapters: ArrayList<Chapter>,
            subChapters: ArrayList<String>
        ) {

            chapters.add(Chapter(chapterTitle,
                subChapters.filter { part -> part != null || part == "" }
                    .joinToString(separator = "\n\n\n\n\n")
            )
            )
        }
    }
}