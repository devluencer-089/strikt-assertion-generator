package com.michaelom.strikt.generator.kapt.sources

import com.michaelom.strikt.generator.annotation.GenerateAssertions

@GenerateAssertions
data class Person(
    val name: String,
    val sex: Sex,
    val car: Car
)
