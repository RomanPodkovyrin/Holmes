package com.example

import java.io.File
import java.net.URL
import kotlin.test.fail

class TestUtils {
    companion object{
        fun getFileFromPath(fileName: String): File? {
            var resource: URL? = null
            try {
                resource = this::class.java.classLoader.getResource(fileName)

            } catch (e: Error) {
                fail("Problem accessing test files")
            }
            if (resource == null) {
                fail("Problem accessing test files")
                return null
            }

            return File(resource.file)
        }
    }
}