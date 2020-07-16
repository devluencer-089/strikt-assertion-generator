package com.michaelom.strikt.generator.example

import com.michaelom.strikt.generator.example.Sex.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.*
import java.time.Instant.now

object PersonSpec : Spek({

    describe("generated assertions for Person.kt") {

        val car = Car(make = "Fiat", year = 1999)
        val date = now()
        val person = Person(
            name = "Hans",
            sex = Male,
            size = 183,
            dateOfBirth = date,
            car = car,
            child = Person(
                name = "Linda",
                sex = Female,
                size = 170,
                car = car,
                dateOfBirth = date,
                child = Person(
                    name = "Marie",
                    sex = Female,
                    size = 155,
                    car = car,
                    dateOfBirth = date
                )
            )
        )

        context("without generated assertions") {

            it("looks noisy") {
                expectThat(person) {
                    get { name } isEqualTo "Hans"
                    get { sex } isEqualTo Male
                    get { size } isEqualTo 183
                    get { dateOfBirth } isEqualTo date

                    with(Person::car) {
                        get { make } isEqualTo "Fiat"
                        get { year } isEqualTo 1999
                    }

                    with(Person::child) {
                        isNotNull().and {
                            get { name } isEqualTo "Linda"
                            get { sex } isEqualTo Female
                            get { size } isEqualTo 170

                            with(Person::child) {
                                isNotNull().and {
                                    get { name } isEqualTo "Marie"
                                    get { sex } isEqualTo Female
                                    get { size } isEqualTo 155
                                }
                            }

                        }
                    }
                }
            }
        }

        context("with generated assertions") {

            it("looks beautiful") {
                expectThat(person) {
                    name isEqualTo "Hans"
                    sex isEqualTo Male
                    size isEqualTo 183
                    dateOfBirth isEqualTo date
                    car {
                        make isEqualTo "Fiat"
                        year isEqualTo 1999
                    }
                    child {
                        name isEqualTo "Linda"
                        sex isEqualTo Female
                        size isEqualTo 170
                        child {
                            name isEqualTo "Marie"
                            sex isEqualTo Female
                            size isEqualTo 155
                        }
                    }
                }
            }
        }
    }
})
