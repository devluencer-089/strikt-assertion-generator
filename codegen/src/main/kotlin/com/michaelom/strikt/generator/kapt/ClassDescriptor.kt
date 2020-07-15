package com.michaelom.strikt.generator.kapt

import com.squareup.kotlinpoet.ClassName
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
            .filter { (property) -> !property.isPrivate && !property.isAbstract && !property.isConst }
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

        val name: String
            get() = property.name

        private val classifier: KmClassifier.Class
            get() = property.returnType.classifier as KmClassifier.Class

        val className: ClassName
            get() = ClassInspectorUtil.createClassName(classifier.name)

        fun isNullable(): Boolean {
            return property.returnType.isNullable
        }

        fun isItselfAGeneratedAssertionType(): Boolean {
            return assertionCandidates.contains(classifier.name)
        }
    }
}
