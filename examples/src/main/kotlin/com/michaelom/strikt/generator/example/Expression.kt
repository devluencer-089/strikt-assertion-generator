package com.michaelom.strikt.generator.example

import com.michaelom.strikt.generator.annotation.GenerateAssertions

sealed class Expr {

    @GenerateAssertions
    data class Const(val number: Double) : Expr()

    @GenerateAssertions
    data class Sum(val e1: Expr, val e2: Expr) : Expr()

    object NotANumber : Expr()
}
