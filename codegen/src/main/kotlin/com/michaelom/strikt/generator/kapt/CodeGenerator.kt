package com.michaelom.strikt.generator.kapt

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview

@KotlinPoetMetadataPreview
fun generateAssertions(descriptor: ClassDescriptor): FileSpec {
    val assertionFileName = descriptor.qualifiedName + "Assertions"
    val file = FileSpec.builder(descriptor.pkgName, assertionFileName)

    descriptor.members
        .forEach { member ->
            file.addProperty(member.toPropertyAssertion())
            if (member.isItselfAGeneratedAssertionType()) {
                file.addFunction(member.toNestedAssertion())
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
private fun ClassDescriptor.Member.toNestedAssertion(): FunSpec {
    return FunSpec
        .builder(name)
        .receiver(receiverType = assertionBuilderOf(enclosingClass.className))
        .addParameter("block", LambdaTypeName.get(assertionBuilderOf(typeName.toNonNullable()), emptyList(), UNIT))
        .returns(returnType = assertionBuilderOf(enclosingClass.className))
        .addStatement(
            if (isNullable()) {
                "return with(${enclosingClass.qualifiedName}::$name) { isNotNull().and(block) }"
            } else {
                "return with(function = ${enclosingClass.qualifiedName}::$name, block = block)"
            }
        )
        .build()
}

@KotlinPoetMetadataPreview
private fun ClassDescriptor.Member.toPropertyAssertion(): PropertySpec {
    return PropertySpec
        .builder(name = name, type = assertionBuilderOf(typeName))
        .receiver(receiverType = assertionBuilderOf(enclosingClass.className))
        .getter(
            FunSpec
                .getterBuilder()
                .addCode("return get(\"${name}\", ${enclosingClass.qualifiedName}::$name)")
                .build())
        .build()
}

// `Assertion.Builder<Car>`
fun assertionBuilderOf(type: TypeName): TypeName {
    return ClassName("strikt.api", "Assertion.Builder").parameterizedBy(type)
}

