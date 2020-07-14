package com.michaelom.strikt.generator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.StopExecutionException

class AssertionPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.afterEvaluate { Project evaluatedProject ->

            checkRequiredPlugin(project, "org.jetbrains.kotlin.jvm")
            checkRequiredPlugin(project, "org.jetbrains.kotlin.kapt")

            Task kaptKotlinTask = evaluatedProject.tasks.findByName("kaptKotlin")

            File file = kaptKotlinTask.kotlinSourcesDestinationDir

            def logger = project.logger
            logger.debug("found kapt test sources directory at '$file'")

            File generatedAssertionsDir = file.toPath().parent.resolve("strikt").toFile()
            logger.debug("added the following directory as a test source '$generatedAssertionsDir'")

            project.sourceSets.test.kotlin.srcDirs += generatedAssertionsDir
            logger.debug("'${project.sourceSets.test.kotlin.srcDirs}'")
        }
    }

    private void checkRequiredPlugin(Project project, String pluginId) {
        if (!project.pluginManager.hasPlugin(pluginId)) {
            throw new StopExecutionException("Could not find plugin '$pluginId' in current project" +
                    " required by plugin 'com.michaelom.strikt.assertion-generator'")
        }
    }
}