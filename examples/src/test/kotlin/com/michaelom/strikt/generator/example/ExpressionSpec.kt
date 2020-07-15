import com.michaelom.strikt.generator.example.*
import com.michaelom.strikt.generator.example.Expr.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ExpressionSpec : Spek({

    describe("generated assertions for Expression.kt") {

        context("without generated assertions") {

            it("looks beautify") {

                expectThat(Const(number = 1.0)) {
                    get { number } isEqualTo 1.0
                }

                val sum = Sum(e1 = Const(1.0), e2 = Const(1.0))

                expectThat(sum) {
                    get { e1 } isEqualTo Const(1.0)
                    get { e2 } isEqualTo Const(1.0)
                }
            }
        }

        context("with generated assertions") {

            it("looks beautify") {

                expectThat(Const(number = 1.0)) {
                    number isEqualTo 1.0
                }

                val sum = Sum(e1 = Const(1.0), e2 = Const(1.0))

                expectThat(sum) {
                    e1 isEqualTo Const(1.0)
                    e2 isEqualTo Const(1.0)
                }
            }
        }
    }
})
