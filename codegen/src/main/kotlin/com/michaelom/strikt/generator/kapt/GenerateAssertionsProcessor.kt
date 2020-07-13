package com.michaelom.strikt.generator.kapt

import com.google.auto.service.AutoService
import com.michaelom.strikt.generator.annotation.GenerateAssertions
import com.michaelom.strikt.generator.kapt.GenerateAssertionsProcessor.Companion.ASSERTION_DIR_NAME
import com.michaelom.strikt.generator.kapt.GenerateAssertionsProcessor.Companion.GENERATED_OPTION_NAME
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.classinspector.elements.ElementsClassInspector
import com.squareup.kotlinpoet.metadata.*
import com.squareup.kotlinpoet.metadata.specs.*
import net.ltgt.gradle.incap.*
import java.nio.file.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
@KotlinPoetMetadataPreview
class GenerateAssertionsProcessor : AbstractProcessor() {
    companion object {
        const val GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val ASSERTION_DIR_NAME = "strikt"
        val annotation = GenerateAssertions::class.java
    }

    private lateinit var messager: Messager
    private lateinit var inspector: ClassInspector

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()
    override fun getSupportedAnnotationTypes(): Set<String> = setOf(GenerateAssertions::class.qualifiedName!!)
    override fun getSupportedOptions(): Set<String> = setOf(GENERATED_OPTION_NAME)

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        this.messager = processingEnv.messager
        inspector = ElementsClassInspector.create(processingEnv.elementUtils, processingEnv.typeUtils)
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.errorRaised()) {
            // an error was raised in the prior round of processing, so we stop this round
            // kotlin does not support multiple rounds atm
            return false
        }

        processingEnv.options[GENERATED_OPTION_NAME] ?: run {
            messager.errorMessage("No target directory for generated Kotlin files. See 'kapt.kotlin.generated' processing option")
            return false
        }

        val assertionCandidates: Set<Element> = roundEnv.getElementsAnnotatedWith(annotation)
        if (assertionCandidates.isEmpty()) {
            return false
        }

        val assertionTypes = assertionCandidates.asSequence()
            .mapNotNull {
                when (it) {
                    is TypeElement -> it
                    else -> {
                        messager.errorMessage("@GenerateAssertions can't be applied to $it: must be a Kotlin data class")
                        null
                    }
                }
            }
            .filter { typeElement ->
                val kmClass = typeElement.toImmutableKmClass()
                //TODO define criteria
                if (!kmClass.isData || kmClass.isPrivate) {
                    messager.errorMessage("@GenerateAssertions can't be applied to $typeElement: must be a Kotlin data class")
                }
                true
            }.toSet()

        val assertionTypeNames = assertionTypes.map { element -> element.toImmutableKmClass().name }.toSet()

        val fileSpecs = assertionTypes
            .map { typeElement ->
                inspector.containerData(typeElement.toImmutableKmClass(), typeElement.asClassName(), null) as ClassData
            }
            .map { classData -> ClassDescriptor(classData, assertionTypeNames) }
            .map { classDescriptor -> generateAssertions(classDescriptor) }

        val targetDir = processingEnv.determineTargetDir()
        writeAssertionFiles(targetDir, fileSpecs)
        return false
    }

    private fun writeAssertionFiles(targetDir: Path, fileSpecs: List<FileSpec>) {
        messager.noteMessage("target directory for generated assertions sources is '$targetDir'")
        fileSpecs.forEach {
            it.writeTo(targetDir)
            messager.noteMessage("Generated ${targetDir.resolve("${it.name}.kt")}")
        }
    }
}

@KotlinPoetMetadataPreview
private fun ProcessingEnvironment.determineTargetDir(): Path {
    val defaultDir = Paths.get(options[GENERATED_OPTION_NAME]!!)
    return defaultDir.parent.resolve(ASSERTION_DIR_NAME)
}

private fun Messager.errorMessage(message: String) {
    this.printMessage(javax.tools.Diagnostic.Kind.ERROR, message)
}

private fun Messager.noteMessage(message: String) {
    this.printMessage(javax.tools.Diagnostic.Kind.NOTE, message)
}
