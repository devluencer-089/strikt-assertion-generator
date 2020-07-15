package com.michaelom.strikt.generator.kapt.sources

import com.michaelom.strikt.generator.annotation.GenerateAssertions

@GenerateAssertions
data class TypeWithPrivateProperties(
    val public: String = "public",
    private val private: String = "private"
)
