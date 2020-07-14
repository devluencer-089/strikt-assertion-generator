import com.michaelom.strikt.generator.example.*
import com.michaelom.strikt.generator.example.Sex.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.*
import java.time.Instant.now

class PersonSpec : Spek({

    describe("generated assertions") {

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

        it("generates lazy assertions") {
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
})
