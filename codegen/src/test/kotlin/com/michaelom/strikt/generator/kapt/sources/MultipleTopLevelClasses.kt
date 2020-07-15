package com.michaelom.strikt.generator.kapt.sources

import com.michaelom.strikt.generator.annotation.GenerateAssertions

@GenerateAssertions
data class A(val property: String = "property")
@GenerateAssertions
data class B(val property: String = "property")
@GenerateAssertions
data class C(val property: String = "property")
