package com.ssafy.sotory

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@Serializable
data class Project(val name: String, val ingnoreNullalbe: String? = null)

val withUnknownKeys = Json {
    ignoreUnknownKeys = true
}

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val project = withUnknownKeys.decodeFromString<Project>("""{"name":"unknown", "version": 2.0}""")
        println(project)
        assertEquals("unknown", project.name)
        assertEquals(null, project.ingnoreNullalbe)


//        assertEquals(4, 2 + 2)
    }
}