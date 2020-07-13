package com.michaelom.strikt.generator.kapt

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview

@KotlinPoetMetadataPreview
fun generateAssertions(descriptor: ClassDescriptor): FileSpec {
    val assertionFileName = descriptor.simpleName + "Assertions"
    val file = FileSpec.builder(descriptor.pkgName, assertionFileName)

    descriptor.members
        .forEach { member ->
            file.addProperty(member.toPropertyAssertion(descriptor))
            if (member.isItselfAGeneratedAssertionType()) {
                file.addFunction(member.toNestedAssertion(descriptor))
                if (member.isNullable()) {
                    file.addImport("strikt.assertions", "isNotNull")
                }
            }
        }

    file.addImport("strikt.api", "Assertion")

    return file.build()
}

/**
```fun Assertion.Builder<Person>.car(block: Assertion.Builder<Car>.() -> Unit): Assertion.Builder<Person> {
return with(function = { this.car }, block = block)
}
fun Assertion.Builder<Person>.child(block: Assertion.Builder<Person>.() -> Unit): Assertion.Builder<Person> {
return child.isNotNull().and(block)
}
```
 */

@KotlinPoetMetadataPreview
private fun ClassDescriptor.Member.toNestedAssertion(classDescriptor: ClassDescriptor): FunSpec {
    return FunSpec
        .builder(name)
        .receiver(assertionBuilderOf(classDescriptor.className))
        .addParameter("block", LambdaTypeName.get(assertionBuilderOf(className), emptyList(), UNIT))
        .returns(assertionBuilderOf(classDescriptor.className))
        .addStatement(
            if (isNullable()) {
                "return ${name}.isNotNull().and(block)"
            } else {
                "return with(function = { this.${name} }, block = block)"
            }
        )
        .build()
}

@KotlinPoetMetadataPreview
private fun ClassDescriptor.Member.toPropertyAssertion(classDescriptor: ClassDescriptor): PropertySpec {
    return PropertySpec
        .builder(name = name, type = assertionBuilderTypeName(this))
        .receiver(receiverType = assertionBuilderOf(classDescriptor.className))
        .getter(
            FunSpec
                .getterBuilder()
                .addCode("return get(\"${name}\", ${classDescriptor.simpleName}::$name)")
                .build())
        .build()
}

// `Assertion.Builder` without type parameter
val assertionBuilder = ClassName("strikt.api", "Assertion.Builder")

// `Assertion.Builder<Car>`
fun assertionBuilderOf(clazz: ClassName): TypeName {
    return assertionBuilder.parameterizedBy(clazz)
}

@KotlinPoetMetadataPreview
fun assertionBuilderTypeName(memberDescriptor: ClassDescriptor.Member): TypeName {
    val propertyTypeClassName = memberDescriptor.className
    val type = if (memberDescriptor.isNullable()) propertyTypeClassName.toNullable() else propertyTypeClassName
    return assertionBuilder.parameterizedBy(type)
}

fun ClassName.toNullable(): ClassName {
    return copy(
        nullable = true,
        annotations = annotations,
        tags = tags
    )
}
