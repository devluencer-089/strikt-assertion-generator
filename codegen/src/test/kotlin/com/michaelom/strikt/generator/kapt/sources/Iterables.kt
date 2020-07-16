package com.michaelom.strikt.generator.kapt.sources

import com.michaelom.strikt.generator.annotation.GenerateAssertions

@GenerateAssertions
data class Iterables(
    val iterable: Iterable<String> = listOf("element"),
    val collection: Collection<String> = listOf("element"),
    val list: List<String> = listOf("element"),
    val set: Set<String> = setOf("element"),
    val map: Map<String, String> = mapOf("key" to "value")
)
