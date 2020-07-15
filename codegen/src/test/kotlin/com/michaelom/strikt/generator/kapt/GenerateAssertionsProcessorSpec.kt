package com.michaelom.strikt.generator.kapt

import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import org.intellij.lang.annotations.Language
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.*
import strikt.assertions.*
import java.io.File

@KotlinPoetMetadataPreview
object GenerateAssertionsProcessorSpec : Spek({

    describe("assertion generation") {

        context("with data classes") {

            it("generates assertions for simple types [Int, String]") {
                val compilation = compileSources("Car.kt")
                expectThat(compilation.exitCode).isEqualTo(ExitCode.OK)

                @Language("kotlin")
                val expected = """
                    package com.michaelom.strikt.generator.kapt.sources
                    
                    import kotlin.Int
                    import kotlin.String
                    import strikt.api.Assertion
                    import strikt.api.Assertion.Builder
                    
                    val Assertion.Builder<Car>.make: Assertion.Builder<String>
                      get() = get("make", Car::make)
                    val Assertion.Builder<Car>.year: Assertion.Builder<Int>
                      get() = get("year", Car::year)
                """.trimIndent()

                expectThat(compilation.assertionFile())
                    .isNotNull()
                    .equalsLineByLine(expected)
            }

            it("generates assertions for optional types") {
                val compilation = compileSources("CarWithOptionalMake.kt")
                expectThat(compilation.exitCode).isEqualTo(ExitCode.OK)

                @Language("kotlin")
                val expected = """
                    package com.michaelom.strikt.generator.kapt.sources
                    
                    import kotlin.Int
                    import kotlin.String
                    import strikt.api.Assertion
                    import strikt.api.Assertion.Builder
                    
                    val Assertion.Builder<CarWithOptionalYear>.make: Assertion.Builder<String>
                      get() = get("make", CarWithOptionalYear::make)
                    val Assertion.Builder<CarWithOptionalYear>.year: Assertion.Builder<Int?>
                      get() = get("year", CarWithOptionalYear::year)
                """.trimIndent()

                expectThat(compilation.assertionFile())
                    .isNotNull()
                    .equalsLineByLine(expected)
            }

            it("generated nested assertions") {
                val compilation = compileSources("Person.kt", "Car.kt", "Sex.kt")
                expectThat(compilation.exitCode).isEqualTo(ExitCode.OK)

                @Language("kotlin")
                val expected = """
                    package com.michaelom.strikt.generator.kapt.sources
                    
                    import kotlin.String
                    import kotlin.Unit
                    import strikt.api.Assertion
                    import strikt.api.Assertion.Builder
                    
                    val Assertion.Builder<Person>.car: Assertion.Builder<Car>
                      get() = get("car", Person::car)
                    fun Assertion.Builder<Person>.car(block: Assertion.Builder<Car>.() -> Unit):
                        Assertion.Builder<Person> = with(function = { this.car }, block = block)
                    
                    val Assertion.Builder<Person>.name: Assertion.Builder<String>
                      get() = get("name", Person::name)
                    val Assertion.Builder<Person>.sex: Assertion.Builder<Sex>
                      get() = get("sex", Person::sex)
                """.trimIndent()

                expectThat(compilation.assertionFile("PersonAssertions.kt"))
                    .isNotNull()
                    .equalsLineByLine(expected)
            }

            it("skips private properties") {
                val compilation = compileSources("TypeWithPrivateProperties.kt")
                expectThat(compilation.exitCode).isEqualTo(ExitCode.OK)

                @Language("kotlin")
                val expected = """
                    package com.michaelom.strikt.generator.kapt.sources
                  
                    import kotlin.String
                    import strikt.api.Assertion
                    import strikt.api.Assertion.Builder
                  
                    val Assertion.Builder<TypeWithPrivateProperties>.public: Assertion.Builder<String>
                      get() = get("public", TypeWithPrivateProperties::public)
                """.trimIndent()

                expectThat(compilation.assertionFile("TypeWithPrivatePropertiesAssertions.kt"))
                    .isNotNull()
                    .equalsLineByLine(expected)
            }
        }
    }

})


private fun Assertion.Builder<File>.equalsLineByLine(expected: String): Assertion.Builder<List<String>> {
    return get { readText().lines() }.isEqualTo(expected.lines())
}
