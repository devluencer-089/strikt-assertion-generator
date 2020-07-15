package com.michaelom.strikt.generator.kapt.sources

import com.michaelom.strikt.generator.annotation.GenerateAssertions

@GenerateAssertions
data class TypeWithSuperTypes(val someInt: Int = 1): NotSupported_AbstractClass(), NotSupported_Interface
