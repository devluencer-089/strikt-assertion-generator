package com.michaelom.strikt.generator.example

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.*

object IterablesSpec : Spek({

    describe("generated assertions for Iterables.kt") {

        context("without generated assertions") {

            it("looks noisy") {
                expectThat(Iterables()) {
                    get { iterable }.contains("element")
                    get { collection }.contains("element")
                    get { list }.contains("element")
                    get { set }.contains("element")
                    get { map }.hasEntry("key", "value")
                }
            }
        }

        context("with generated assertions") {

            it("looks beautiful") {

                expectThat(Iterables()) {
                    iterable.contains("element")
                    collection.contains("element")
                    list.contains("element")
                    set.contains("element")
                    map.hasEntry("key", "value")
                }
            }
        }
    }
})
