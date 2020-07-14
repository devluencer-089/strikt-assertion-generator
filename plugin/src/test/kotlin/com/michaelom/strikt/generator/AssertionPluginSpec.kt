package com.michaelom.strikt.generator

import org.gradle.testkit.runner.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.*
import java.io.File

object AssertionPluginSpec : Spek({

    describe("Assertion Plugin") {

        context("when the build file is setup properly") {

            it("adds the generated assertions to the test sources") {
                val result: BuildResult = GradleRunner.create()
                    .withProjectDir(projectDir("success"))
                    .withPluginClasspath()
                    .withArguments("--info")
                    .forwardOutput()
                    .build()

                expectThat(result.tasks(TaskOutcome.FAILED)).isEmpty()
            }
        }

        context("when the build file is not setup properly") {

            it("throws an exception if the kotlin-plugin is missing") {

                val result = GradleRunner.create()
                    .withProjectDir(projectDir("missing-kotlin-plugin"))
                    .withPluginClasspath()
                    .forwardOutput()
                    .buildAndFail()

                expectThat(result.output).contains(
                    "Could not find plugin 'org.jetbrains.kotlin.jvm' in current project " +
                        "required by plugin 'com.michaelom.strikt.assertion-generator'")
            }

            it("throws an exception if the kapt-plugin is missing") {
                val result = GradleRunner.create()
                    .withProjectDir(projectDir("missing-kapt-plugin"))
                    .withPluginClasspath()
                    .forwardOutput()
                    .buildAndFail()

                expectThat(result.output).contains(
                    "Could not find plugin 'org.jetbrains.kotlin.kapt' in current project " +
                        "required by plugin 'com.michaelom.strikt.assertion-generator'")
            }
        }

    }
})

fun projectDir(name: String): File {
    val uri = Thread.currentThread().contextClassLoader.getResource("testcase/$name").toURI()
    return File(uri)
}
