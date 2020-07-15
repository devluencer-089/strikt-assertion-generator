package com.michaelom.strikt.generator.kapt.sources

import com.michaelom.strikt.generator.annotation.GenerateAssertions

class TopLevel {
    @GenerateAssertions
    data class Nested(val property: String = "property")
}
