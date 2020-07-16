package com.michaelom.strikt.generator.kapt

import com.squareup.kotlinpoet.*

fun ClassName.toNullable(): ClassName {
    if (isNullable) {
        return this
    }
    return copy(
        nullable = true,
        annotations = annotations,
        tags = tags
    )
}

fun TypeName.toNonNullable(): TypeName {
    if (isNullable) {
        return copy(
            nullable = false,
            annotations = annotations,
            tags = tags
        )
    }
    return this;
}
