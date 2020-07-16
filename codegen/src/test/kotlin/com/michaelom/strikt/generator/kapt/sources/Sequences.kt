package com.michaelom.strikt.generator.kapt.sources

import com.michaelom.strikt.generator.annotation.GenerateAssertions

@GenerateAssertions
data class Sequences(
    val sequence: Sequence<String> = sequenceOf("element")
)
