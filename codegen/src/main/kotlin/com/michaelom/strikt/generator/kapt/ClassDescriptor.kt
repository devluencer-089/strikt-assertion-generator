package com.michaelom.strikt.generator.kapt

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.metadata.*
import com.squareup.kotlinpoet.metadata.specs.*
import com.squareup.kotlinpoet.metadata.specs.internal.ClassInspectorUtil

import kotlinx.metadata.KmClassifier

@KotlinPoetMetadataPreview
class ClassDescriptor(
    private val containerData: ClassData,
    private val assertionCandidates: Set<String>
) {

    val members: List<Member> by lazy {
        containerData.properties.asSequence()
            .filter { (property) -> property.isVal || property.isVar }
            .filter { (property) -> !property.isPrivate }
            .map { (property, data) -> Member(property, data) }
            .toList()
    }

    val pkgName: String
        get() = className.packageName

//    private val simpleName: String
//        get() = className.simpleName
//
//    private val canonicalName: String
//        get() = className.canonicalName

    // qualifiedName = enclosing class name(s) + simpleName
    val qualifiedName: String
        get() = className.simpleNames.joinToString(separator = ".")

    val className: ClassName
        get() = containerData.className

    @KotlinPoetMetadataPreview
    inner class Member(private val property: ImmutableKmProperty, private val data: PropertyData) {

        val enclosingClass: ClassDescriptor
            get() = this@ClassDescriptor

        val name: String
            get() = property.name

        val typeName: TypeName by lazy { typeNameOf(property.returnType) }

        private val returnTypeName: String
            get() = (property.returnType.classifier as KmClassifier.Class).name

        fun isNullable(): Boolean {
            return property.returnType.isNullable
        }

        fun isItselfAGeneratedAssertionType(): Boolean {
            return assertionCandidates.contains(returnTypeName)
        }

        @KotlinPoetMetadataPreview
        private fun typeNameOf(type: ImmutableKmType): TypeName {
            val name = (type.classifier as KmClassifier.Class).name
            val className = name.bestGuessClassName(type.isNullable)
            if (type.arguments.isEmpty()) {
                return className
            }

            val parameterClassNames = type.arguments.map { arg -> typeNameOf(arg.type!!) }
            return className.parameterizedBy(parameterClassNames)
        }
    }
}

@KotlinPoetMetadataPreview
private fun String.bestGuessClassName(nullable: Boolean): ClassName {
    val className = ClassInspectorUtil.createClassName(this)
    return if (nullable) className.toNullable() else className
}
