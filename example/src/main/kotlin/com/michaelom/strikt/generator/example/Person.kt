package com.michaelom.strikt.generator.example

import com.michaelom.strikt.generator.annotation.GenerateAssertions
import java.time.Instant

@GenerateAssertions
data class Person(
    val name: String,
    val sex: Sex,
    val size: Int,
    val dateOfBirth: Instant,
    val car: Car,
    val child: Person? = null
)
