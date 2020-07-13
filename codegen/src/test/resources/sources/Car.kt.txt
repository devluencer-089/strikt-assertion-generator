package com.michaelom.strikt.generator.kapt.sources

import com.michaelom.strikt.generator.annotation.GenerateAssertions

@GenerateAssertions
data class Car(val make: String, val year: Int)
