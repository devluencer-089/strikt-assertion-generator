package com.michaelom.strikt.generator.kapt

import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.tschuchort.compiletesting.*
import java.io.File

@KotlinPoetMetadataPreview
fun readSource(name: String): SourceFile {
    val source = GenerateAssertionsProcessor::class.java.getResource("/sources/${name}.txt")
    return SourceFile.kotlin(name = name, contents = source.readText())
}

@KotlinPoetMetadataPreview
fun compileSources(name: String, vararg more: String): KotlinCompilation.Result {
    return KotlinCompilation().apply {
        sources = listOf(readSource(name)) + more.toList().map { readSource(it) }
        annotationProcessors = listOf(GenerateAssertionsProcessor())
        inheritClassPath = true
        reportOutputFiles = true
        reportPerformance = false
        verbose = false
        correctErrorTypes = true
        allWarningsAsErrors = true
    }.compile()
}

@KotlinPoetMetadataPreview
fun KotlinCompilation.Result.assertionFiles(): List<File> {
    return File(outputDirectory.parent)
        .resolve("kapt")
        .resolve(GenerateAssertionsProcessor.ASSERTION_DIR_NAME)
        .listFilesRecursively()
}

@KotlinPoetMetadataPreview
fun KotlinCompilation.Result.assertionFile(name: String? = null): File? {
    val files = assertionFiles().filter { file -> name?.let { file.name == it } ?: true }
    if (files.size != 1) {
        throw AssertionError("Expected exactly 1 assertion file, found ${files.size}")
    }
    return files.first()
}

fun File.listFilesRecursively(): List<File> {
    return (listFiles() ?: emptyArray<File>()).flatMap { file ->
        if (file.isDirectory)
            file.listFilesRecursively()
        else
            listOf(file)
    }
}
