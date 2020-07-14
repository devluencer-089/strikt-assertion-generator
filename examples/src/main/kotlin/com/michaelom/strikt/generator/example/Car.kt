package com.michaelom.strikt.generator.example

import com.michaelom.strikt.generator.annotation.GenerateAssertions

@GenerateAssertions
data class Car(val make: String, val year: Int)
