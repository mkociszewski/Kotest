package org.json

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeSpecContainerScope
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.json.serializer.JsonSerializer
import org.json.serializer.SerializerFactory
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

@JvmRecord
data class RecordSample(val name: String?, val value: Int?)
enum class SampleEnum { VALUE1, VALUE2 }

suspend fun FreeSpecContainerScope.parsingTest(deserializedJson: Any, json: String, serializer: JsonSerializer) {
    "parse from json" {
        val result = serializer.fromJson(json, deserializedJson::class.java)
        result shouldBe deserializedJson
    }

    "write to json" {
        val result = serializer.toJson(deserializedJson)
        result shouldBe json
    }
}

class Requirements : FreeSpec({


    "Requirements" - {
        val jsonSerializer = SerializerFactory.createSerializer()

        "Simple object with empty constructor" - {

            class Sample(var name: String? = null, var value: Int? = null)

            @Language("JSON") val json = """
                {
                  "name": "someName",
                  "value": 5
                }
            """.trimIndent()

            val deserializedJson = Sample().apply {
                name = "someName"
                value = 5
            }


            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Simple object with all arg constructor and empty constructor" - {

            class Sample(var name: String?, var value: Int?) {
                constructor() : this(null, null)
            }

            @Language("JSON") val json = """
                {
                  "name": "someName",
                  "value": 5
                }
            """.trimIndent()

            val deserializedJson = Sample().apply {
                name = "someName"
                value = 5
            }

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Simple object with all final fields" - {

            class Sample(val name: String?, val value: Int?)

            @Language("JSON") val json = """
                {
                  "name": "someName",
                  "value": 5
                }
            """.trimIndent()

            val deserializedJson = Sample("someName", 5)

            parsingTest(deserializedJson, json, jsonSerializer)
        }


        "TRUDNE! Simple object with required args constructor" - {

            class Sample(val nameConstructor: String, val valueConstructor: Int) {
                var name: String? = null
                var value: Int? = null
            }

            @Language("JSON") val json = """
                {
                  "nameConstructor" : "nameFromConstructor",
                  "valueConstructor": 12,
                  "name": "someName",
                  "value": 5
                }
            """.trimIndent()

            val deserializedJson = Sample("nameFromConstructor", 12).apply {
                name = "someName"
                value = 5
            }

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Java record" - {

            @Language("JSON") val json = """
                {
                  "name": "someName",
                  "value": 5
                }
            """.trimIndent()

            val deserializedJson = RecordSample("someName", 5)

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Simple object with List" - {

            class Sample(val names: List<String>?, val value: Int?)

            @Language("JSON") val json = """
                {
                  "names": [
                    "name1",
                    "name2"
                  ],
                  "value": 5
                }
            """.trimIndent()

            val deserializedJson = Sample(listOf("name1", "name2"), 5)

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Nested object" - {

            class NestedMoreSample(val name: String?, val value: Int?)
            class NestedSample(val name: String, var nestedMore: NestedMoreSample? = null)
            class Sample(val nested: NestedSample, val value: Int?)

            @Language("JSON") val json = """
                {
                  "nested": {
                    "name": "someName",
                    "nestedMore": {
                      "name": "nestedName",
                      "value": 31
                    }
                  },
                  "value": 5
                }
            """.trimIndent()

            val deserializedJson = Sample(
                NestedSample("someName").apply {
                    nestedMore = NestedMoreSample("nestedName", 31)
                }, 5
            )

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Object with primitive type" - {

            class Sample(val name: String?, val value: Int)

            @Language("JSON") val json = """
                {
                  "name": "someName",
                  "value": 66
                }
            """.trimIndent()

            val deserializedJson = Sample("someName", 66)

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Object with Boolean" - {
            class Sample(val isActive: Boolean)

            @Language("JSON") val json = """
                {
                  "isActive": true
                }
            """.trimIndent()

            val deserializedJson = Sample(true)

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Object with nested maps" - {
            class Sample(val data: Map<String, Any>?)

            @Language("JSON") val json = """
                {
                  "data": {
                    "key1": "value1",
                    "key2": 42
                  }
                }
            """.trimIndent()

            val deserializedJson = Sample(mapOf("key1" to "value1", "key2" to 42))

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Object with null" - {
            class Sample(val name: String?, val value: Int?)

            @Language("JSON") val json = """
                {
                  "value": 66
                }
            """.trimIndent()

            val deserializedJson = Sample(null, 66)

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Object with enum fields" - {
            class Sample(val enumField: SampleEnum)

            @Language("JSON") val json = """
                {
                  "enumField": "VALUE1"
                }
            """.trimIndent()

            val deserializedJson = Sample(SampleEnum.VALUE1)

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Object with numeric and json with numeric as string" - {
            class Sample(val value: Int?)

            @Language("JSON") val json = """
                {
                  "value": "43"
                }
            """.trimIndent()

            val deserializedJson = Sample(43)

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Object with complex fields and collections" - {

            class Inner(val value: String)
            class Sample(val innerList: List<Inner>)

            @Language("JSON") val json = """
                {
                  "innerList": [
                    {
                      "value": "A"
                    },
                    {
                      "value": "B"
                    }
                  ]
                }
            """.trimIndent()

            val deserializedJson = Sample(listOf(Inner("A"), Inner("B")))

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Object with instant field" - {

            val now = Instant.now()

            class Sample(val timestamp: Instant?)

            @Language("JSON") val json = """
                {
                  "timestamp": $now
                }
            """.trimIndent()

            val deserializedJson = Sample(now)

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Object with date field" - {

            class Sample(val date: LocalDate, val dateTime: LocalDateTime)

            @Language("JSON") val json = """
                {
                  "date": "2023-11-28",
                  "dateTime": "2023-11-28T22:15:30"
                }
            """.trimIndent()

            val deserializedJson = Sample(LocalDate.of(2023, 11, 28), LocalDateTime.of(2023, 11, 28, 22, 15, 30))

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Object with period" - {

            class Sample(val period: Period?)

            @Language("JSON") val json = """
                {
                  "period": "P1Y2M3D"
                }
            """.trimIndent()

            val deserializedJson = Sample(Period.of(1, 2, 3))

            parsingTest(deserializedJson, json, jsonSerializer)
        }

        "Object with duration" - {

            class Sample(val period: Duration?)

            @Language("JSON") val json = """
                {
                  "period": "PT10H30M5.52S"
                }
            """.trimIndent()

            val duration = Duration.ZERO.plusHours(10).plusMinutes(30).plusSeconds(5).plusMillis(520)
            val deserializedJson = Sample(duration)

            parsingTest(deserializedJson, json, jsonSerializer)
        }

    }
})
