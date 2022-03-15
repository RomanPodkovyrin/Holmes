package com.romanp.fyp.utils

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Provides compress and uncompress functionality of the string
 */
class Compression {
    companion object {

        fun compress(content: String): ByteArray {
            val bos = ByteArrayOutputStream()
            GZIPOutputStream(bos).bufferedWriter(StandardCharsets.UTF_8).use { it.write(content) }
            return bos.toByteArray()
        }

        fun uncompress(content: ByteArray): String {
            return GZIPInputStream(content.inputStream()).bufferedReader(StandardCharsets.UTF_8)
                .use { it.readText() }
        }
    }
}