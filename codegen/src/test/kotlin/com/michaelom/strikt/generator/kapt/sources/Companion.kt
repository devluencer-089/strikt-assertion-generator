package com.michaelom.strikt.generator.kapt.sources

import com.michaelom.strikt.generator.annotation.GenerateAssertions

data class Companion(val property: String = "property") {

    @GenerateAssertions
    companion object {
        val companionProperty = "property"

    }
}
