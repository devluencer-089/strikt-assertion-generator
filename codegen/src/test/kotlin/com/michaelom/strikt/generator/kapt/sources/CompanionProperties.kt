package com.michaelom.strikt.generator.kapt.sources

import com.michaelom.strikt.generator.annotation.GenerateAssertions

@GenerateAssertions
data class CompanionProperties(val property: String = "property") {

    companion object {
        val companionProperty1 = "property"
        var companionProperty2 = "property"
        const val companionProperty3 = "property"
    }
}
