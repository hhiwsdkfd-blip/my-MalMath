package com.example

import com.example.math.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.PI

class ExampleUnitTest {

    @Test
    fun testMathEvaluator() {
        val evaluator = MathExpressionEvaluator(isDegreeMode = false)

        // Arithmetic
        assertEquals(7.0, evaluator.evaluate("1 + 2 * 3"), 1e-6)
        assertEquals(8.0, evaluator.evaluate("2 ^ 3"), 1e-6)
        assertEquals(120.0, evaluator.evaluate("5!"), 1e-6)
        assertEquals(5.0, evaluator.evaluate("sqrt(25)"), 1e-6)

        // Trig & Constants
        assertEquals(0.0, evaluator.evaluate("sin(0)"), 1e-6)
        assertEquals(1.0, evaluator.evaluate("cos(0)"), 1e-6)
        assertEquals(1.0, evaluator.evaluate("sin(pi / 2)"), 1e-6)
        assertEquals(1.0, evaluator.evaluate("ln(e)"), 1e-6)

        // Degree mode
        val degEvaluator = MathExpressionEvaluator(isDegreeMode = true)
        assertEquals(0.5, degEvaluator.evaluate("sin(30)"), 1e-6)
        assertEquals(1.0, degEvaluator.evaluate("tan(45)"), 1e-6)
    }

    @Test
    fun testCalculusDerivatives() {
        val calculus = CalculusEngine()

        // f(x) = x^2, f'(3) = 6
        val res1 = calculus.calculateDerivative("x^2", 3.0, 1)
        assertEquals(6.0, res1.value, 1e-2)

        // f(x) = sin(x), f'(0) = cos(0) = 1
        val res2 = calculus.calculateDerivative("sin(x)", 0.0, 1)
        assertEquals(1.0, res2.value, 1e-2)

        // Second derivative of x^3 at x = 2 is 6*x = 12
        val res3 = calculus.calculateDerivative("x^3", 2.0, 2)
        assertEquals(12.0, res3.value, 1e-1)
    }

    @Test
    fun testCalculusIntegrals() {
        val calculus = CalculusEngine()

        // ∫_0^1 x^2 dx = 1/3 ≈ 0.333333
        val res1 = calculus.calculateDefiniteIntegral("x^2", 0.0, 1.0)
        assertEquals(1.0 / 3.0, res1.value, 1e-3)

        // ∫_0^pi sin(x) dx = 2.0
        val res2 = calculus.calculateDefiniteIntegral("sin(x)", 0.0, PI)
        assertEquals(2.0, res2.value, 1e-3)
    }

    @Test
    fun testCalculusLimits() {
        val calculus = CalculusEngine()

        // lim_{x -> 0} sin(x)/x = 1 (classic 0/0 indeterminate form)
        val res1 = calculus.calculateLimit("(sin(x)) / x", 0.0)
        assertNotNull(res1.limitValue)
        assertEquals(1.0, res1.limitValue!!, 1e-2)

        // lim_{x -> 2} (x^2 - 4)/(x - 2) = 4
        val res2 = calculus.calculateLimit("(x^2 - 4) / (x - 2)", 2.0)
        assertNotNull(res2.limitValue)
        assertEquals(4.0, res2.limitValue!!, 1e-2)
    }

    @Test
    fun testAlgebraQuadratic() {
        val algebra = AlgebraEngine()

        // x^2 - 5x + 6 = 0 -> roots 3 and 2
        val res = algebra.solveQuadratic(1.0, -5.0, 6.0)
        assertEquals(1.0, res.discriminant, 1e-6)
        assertEquals("3", res.root1)
        assertEquals("2", res.root2)
    }

    @Test
    fun testAlgebraLinearSystem() {
        val algebra = AlgebraEngine()

        // 2x + 3y = 8, x - y = -1 -> solution x = 1, y = 2
        val res = algebra.solve2x2(2.0, 3.0, 8.0, 1.0, -1.0, -1.0)
        assertEquals(1.0, res.x!!, 1e-6)
        assertEquals(2.0, res.y!!, 1e-6)
    }

    @Test
    fun testGeometry() {
        val geometry = GeometryEngine()

        // Circle with r = 5
        val circle = geometry.calculateCircle(5.0)
        assertEquals(PI * 25.0, circle.primaryMetricValue, 1e-3)

        // Right triangle 3, 4, 5 -> Area = 6
        val triangle = geometry.calculateTriangleSSS(3.0, 4.0, 5.0)
        assertEquals(6.0, triangle.primaryMetricValue, 1e-3)
    }

    @Test
    fun testFractionSDToggle() {
        val f1 = Fraction.fromDouble(0.75)
        assertEquals("3/4", f1.toPrettyString())

        val f2 = Fraction.fromDouble(0.3333333333)
        assertEquals("1/3", f2.toPrettyString())

        val f3 = Fraction(1, 2) + Fraction(1, 4)
        assertEquals("3/4", f3.toPrettyString())
    }

    @Test
    fun testComplexNumbers() {
        val z1 = ComplexNumber(3.0, 4.0)
        assertEquals(5.0, z1.magnitude(), 1e-6)
        assertEquals("3 + 4i", z1.toPrettyString())

        val z2 = ComplexNumber(1.0, -2.0)
        val zSum = z1 + z2
        assertEquals("4 + 2i", zSum.toPrettyString())
    }

    @Test
    fun testUnitConverter() {
        val cat = UnitConverter.categories.first { it.id == "length" }
        val m = cat.units.first { it.id == "m" }
        val km = cat.units.first { it.id == "km" }

        val res = UnitConverter.convert(2500.0, m, km)
        assertEquals(2.5, res, 1e-6)
    }

    @Test
    fun testMalMathOfflineStepSolver() {
        val solver = OfflineStepSolver()

        // Solve quadratic equation step by step completely offline
        val solQuad = solver.solveOffline("x^2 - 5*x + 6 = 0")
        assertTrue(solQuad.steps.isNotEmpty())
        assertTrue(solQuad.finalAnswer.contains("3") && solQuad.finalAnswer.contains("2"))

        // Solve derivative step by step offline
        val solDeriv = solver.solveOffline("d/dx [x^3]")
        assertTrue(solDeriv.steps.isNotEmpty())
        assertTrue(solDeriv.finalAnswer.contains("3x²") || solDeriv.finalAnswer.contains("x"))

        // Solve integral step by step offline
        val solInteg = solver.solveOffline("∫ x^2 dx")
        assertTrue(solInteg.steps.isNotEmpty())
        assertTrue(solInteg.finalAnswer.contains("C"))
    }
}
