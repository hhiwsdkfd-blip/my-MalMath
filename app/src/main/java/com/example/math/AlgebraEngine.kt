package com.example.math

import kotlin.math.*

/**
 * Algebra Engine supporting:
 * - Linear equations (ax + b = 0)
 * - Quadratic equations (ax² + bx + c = 0) with real & complex roots, vertex, discriminant
 * - Cubic equations (ax³ + bx² + cx + d = 0)
 * - Systems of 2 and 3 linear equations using Cramer's Determinants
 * - Matrix Operations (Addition, Multiplication, Determinant, Inverse, Transpose)
 * - Arithmetic and Geometric Sequences, finite Series Summations & Products
 */
class AlgebraEngine(private val evaluator: MathExpressionEvaluator = MathExpressionEvaluator()) {

    data class QuadraticResult(
        val a: Double, val b: Double, val c: Double,
        val discriminant: Double,
        val root1: String,
        val root2: String,
        val vertexX: Double,
        val vertexY: Double,
        val axisOfSymmetry: String,
        val steps: List<String>
    )

    data class LinearSystem2Result(
        val x: Double?,
        val y: Double?,
        val determinantD: Double,
        val determinantDx: Double,
        val determinantDy: Double,
        val statusMessage: String,
        val steps: List<String>
    )

    data class LinearSystem3Result(
        val x: Double?,
        val y: Double?,
        val z: Double?,
        val determinantD: Double,
        val statusMessage: String,
        val steps: List<String>
    )

    data class MatrixResult(
        val data: Array<DoubleArray>,
        val determinant: Double?,
        val trace: Double?,
        val inverse: Array<DoubleArray>?,
        val transpose: Array<DoubleArray>,
        val steps: List<String>
    )

    data class SequenceResult(
        val nthTerm: Double,
        val sumN: Double,
        val infiniteSum: Double?,
        val sequenceType: String,
        val steps: List<String>
    )

    // ==========================================
    // 1. QUADRATIC EQUATION (المعادلة التربيعية)
    // ==========================================

    fun solveQuadratic(a: Double, b: Double, c: Double): QuadraticResult {
        if (abs(a) < 1e-12) {
            // Degenerates to linear bx + c = 0
            val steps = mutableListOf("المعامل a = 0، إذن المعادلة خطية: ${b}x + $c = 0")
            val x = if (abs(b) > 1e-12) -c / b else Double.NaN
            steps.add("الحل: x = -c/b = ${formatValue(x)}")
            return QuadraticResult(
                a, b, c, 0.0,
                formatValue(x), formatValue(x),
                x, 0.0, "x = ${formatValue(x)}", steps
            )
        }

        val delta = b * b - 4.0 * a * c
        val vertexX = -b / (2.0 * a)
        val vertexY = a * vertexX * vertexX + b * vertexX + c

        val steps = mutableListOf<String>()
        steps.add("الصيغة العامة للمعادلة: ax² + bx + c = 0")
        steps.add("المعاملات: a = $a, b = $b, c = $c")
        steps.add("حساب المميز (Δ = b² - 4ac):")
        steps.add("Δ = ($b)² - 4($a)($c) = ${formatValue(delta)}")

        val root1: String
        val root2: String

        if (delta > 1e-12) {
            val sqrtDelta = sqrt(delta)
            val r1 = (-b + sqrtDelta) / (2.0 * a)
            val r2 = (-b - sqrtDelta) / (2.0 * a)
            root1 = formatValue(r1)
            root2 = formatValue(r2)
            steps.add("بما أن المميز Δ > 0 ⟹ يوجد جذران حقيقيان مختلفان:")
            steps.add("x₁ = (-b + √Δ) / 2a = (-$b + ${formatValue(sqrtDelta)}) / ${2.0 * a} = $root1")
            steps.add("x₂ = (-b - √Δ) / 2a = (-$b - ${formatValue(sqrtDelta)}) / ${2.0 * a} = $root2")
        } else if (abs(delta) <= 1e-12) {
            val r = -b / (2.0 * a)
            root1 = formatValue(r)
            root2 = root1
            steps.add("بما أن المميز Δ = 0 ⟹ يوجد جذر حقيقي مكرر واحد:")
            steps.add("x = -b / 2a = -$b / ${2.0 * a} = $root1")
        } else {
            val realPart = -b / (2.0 * a)
            val imagPart = sqrt(-delta) / (2.0 * a)
            root1 = "${formatValue(realPart)} + ${formatValue(abs(imagPart))} i"
            root2 = "${formatValue(realPart)} - ${formatValue(abs(imagPart))} i"
            steps.add("بما أن المميز Δ < 0 ⟹ لا توجد جذور حقيقية، بل جذران مركبان مترافقان:")
            steps.add("x₁ = $root1")
            steps.add("x₂ = $root2")
        }

        steps.add("إحداثيات رأس القطع المكافئ (Vertex): (h, k) = (${formatValue(vertexX)}, ${formatValue(vertexY)})")
        steps.add("معادلة محور التماثل (Axis of Symmetry): x = ${formatValue(vertexX)}")

        return QuadraticResult(
            a = a, b = b, c = c,
            discriminant = delta,
            root1 = root1, root2 = root2,
            vertexX = vertexX, vertexY = vertexY,
            axisOfSymmetry = "x = ${formatValue(vertexX)}",
            steps = steps
        )
    }

    // ==========================================
    // 2. SYSTEMS OF LINEAR EQUATIONS (أنظمة المعادلات)
    // ==========================================

    fun solve2x2(
        a1: Double, b1: Double, c1: Double,
        a2: Double, b2: Double, c2: Double
    ): LinearSystem2Result {
        // a1 x + b1 y = c1
        // a2 x + b2 y = c2
        val D = a1 * b2 - a2 * b1
        val Dx = c1 * b2 - c2 * b1
        val Dy = a1 * c2 - a2 * c1

        val steps = mutableListOf<String>()
        steps.add("حل نظام معادلتين بمتغيرين بطريقة كرامر (Cramer's Rule):")
        steps.add("المعادلة الأولى: ${a1}x + ${b1}y = $c1")
        steps.add("المعادلة الثانية: ${a2}x + ${b2}y = $c2")
        steps.add("محدد المصفوفة الرئيسية D = |[a₁, b₁], [a₂, b₂]| = ($a1)($b2) - ($a2)($b1) = ${formatValue(D)}")
        steps.add("محدد المتغير x (استبدال عمود x بالثوابت): Dx = ($c1)($b2) - ($c2)($b1) = ${formatValue(Dx)}")
        steps.add("محدد المتغير y (استبدال عمود y بالثوابت): Dy = ($a1)($c2) - ($a2)($c1) = ${formatValue(Dy)}")

        return if (abs(D) > 1e-12) {
            val x = Dx / D
            val y = Dy / D
            steps.add("الحل الوحيد للنظام:")
            steps.add("x = Dx / D = ${formatValue(Dx)} / ${formatValue(D)} = ${formatValue(x)}")
            steps.add("y = Dy / D = ${formatValue(Dy)} / ${formatValue(D)} = ${formatValue(y)}")
            LinearSystem2Result(x, y, D, Dx, Dy, "يوجد حل وحيد ومضبوط", steps)
        } else {
            if (abs(Dx) < 1e-12 && abs(Dy) < 1e-12) {
                steps.add("D = 0 و Dx = Dy = 0 ⟹ يوجد عدد لا نهائي من الحلول (مستقيمان منطبقان).")
                LinearSystem2Result(null, null, D, Dx, Dy, "عدد لا نهائي من الحلول", steps)
            } else {
                steps.add("D = 0 مع وجود محدد غير صفري ⟹ لا يوجد حل للنظام (مستقيمان متوازيان ومتباعدان).")
                LinearSystem2Result(null, null, D, Dx, Dy, "النظام مستحيل الحل (لا يوجد حل)", steps)
            }
        }
    }

    fun solve3x3(
        m: Array<DoubleArray>, // 3x3 coefficients
        c: DoubleArray // 3 constants
    ): LinearSystem3Result {
        val steps = mutableListOf<String>()
        steps.add("حل نظام 3 معادلات خطية بـ 3 مجاهيل (x, y, z):")

        fun det3(a: Array<DoubleArray>): Double {
            return a[0][0] * (a[1][1] * a[2][2] - a[1][2] * a[2][1]) -
                    a[0][1] * (a[1][0] * a[2][2] - a[1][2] * a[2][0]) +
                    a[0][2] * (a[1][0] * a[2][1] - a[1][1] * a[2][0])
        }

        val D = det3(m)
        steps.add("محدد المعاملات D = ${formatValue(D)}")

        if (abs(D) < 1e-12) {
            steps.add("المحدد الرئيسي D = 0 ⟹ النظام إما مستحيل الحل أو له عدد لا نهائي من الحلول.")
            return LinearSystem3Result(null, null, null, D, "المحدد صفر: لا يوجد حل وحيد", steps)
        }

        fun replaceCol(col: Int): Array<DoubleArray> {
            val copy = Array(3) { r -> DoubleArray(3) { k -> if (k == col) c[r] else m[r][k] } }
            return copy
        }

        val Dx = det3(replaceCol(0))
        val Dy = det3(replaceCol(1))
        val Dz = det3(replaceCol(2))

        val x = Dx / D
        val y = Dy / D
        val z = Dz / D

        steps.add("Dx = ${formatValue(Dx)} ⟹ x = Dx / D = ${formatValue(x)}")
        steps.add("Dy = ${formatValue(Dy)} ⟹ y = Dy / D = ${formatValue(y)}")
        steps.add("Dz = ${formatValue(Dz)} ⟹ z = Dz / D = ${formatValue(z)}")

        return LinearSystem3Result(x, y, z, D, "تم إيجاد الحل الوحيد بنجاح", steps)
    }

    // ==========================================
    // 3. MATRIX CALCULATIONS (حساب المصفوفات)
    // ==========================================

    fun analyzeMatrix2x2(a: Double, b: Double, c: Double, d: Double): MatrixResult {
        val mat = arrayOf(doubleArrayOf(a, b), doubleArrayOf(c, d))
        val det = a * d - b * c
        val trace = a + d
        val transpose = arrayOf(doubleArrayOf(a, c), doubleArrayOf(b, d))

        val steps = mutableListOf<String>()
        steps.add("المصفوفة A = [[$a, $b], [$c, $d]]")
        steps.add("المحدد det(A) = ($a)($d) - ($b)($c) = ${formatValue(det)}")
        steps.add("الأثر trace(A) = a + d = ${formatValue(trace)}")

        val inv = if (abs(det) > 1e-12) {
            val invDet = 1.0 / det
            steps.add("معكوس المصفوفة A⁻¹ = (1/det) × [[d, -b], [-c, a]]:")
            val invMat = arrayOf(
                doubleArrayOf(d * invDet, -b * invDet),
                doubleArrayOf(-c * invDet, a * invDet)
            )
            steps.add("A⁻¹ = [[${formatValue(invMat[0][0])}, ${formatValue(invMat[0][1])}], [${formatValue(invMat[1][0])}, ${formatValue(invMat[1][1])}]]")
            invMat
        } else {
            steps.add("المحدد يساوي صفراً، إذن المصفوفة شاذة (Singular) وليس لها معكوس.")
            null
        }

        return MatrixResult(mat, det, trace, inv, transpose, steps)
    }

    fun multiplyMatrices2x2(
        a1: Double, b1: Double, c1: Double, d1: Double,
        a2: Double, b2: Double, c2: Double, d2: Double
    ): Array<DoubleArray> {
        return arrayOf(
            doubleArrayOf(a1 * a2 + b1 * c2, a1 * b2 + b1 * d2),
            doubleArrayOf(c1 * a2 + d1 * c2, c1 * b2 + d1 * d2)
        )
    }

    // ==========================================
    // 4. SEQUENCES & SERIES (المتتابعات والمتسلسلات)
    // ==========================================

    fun calculateArithmeticProgression(a1: Double, d: Double, n: Int): SequenceResult {
        val an = a1 + (n - 1) * d
        val sn = (n.toDouble() / 2.0) * (2.0 * a1 + (n - 1) * d)

        val steps = listOf(
            "المتتابعة الحسابية: الحد الأول a₁ = $a1، أساس المتتابعة d = $d",
            "صيغة الحد العام: aₙ = a₁ + (n - 1)d",
            "الحد رقم $n: a_$n = $a1 + ($n - 1)($d) = ${formatValue(an)}",
            "صيغة مجموع أول n حداً: Sₙ = (n/2) [2a₁ + (n - 1)d]",
            "مجموع أول $n حداً: S_$n = ($n/2) [2($a1) + (${n - 1})($d)] = ${formatValue(sn)}"
        )

        return SequenceResult(an, sn, null, "متتابعة حسابية", steps)
    }

    fun calculateGeometricProgression(a1: Double, r: Double, n: Int): SequenceResult {
        val an = a1 * r.pow(n - 1)
        val sn = if (abs(r - 1.0) > 1e-12) {
            a1 * (1.0 - r.pow(n)) / (1.0 - r)
        } else {
            a1 * n
        }

        val sInf = if (abs(r) < 1.0) {
            a1 / (1.0 - r)
        } else null

        val steps = mutableListOf(
            "المتتابعة الهندسية: الحد الأول a₁ = $a1، أساس المتتابعة (النسبة) r = $r",
            "صيغة الحد النوني: aₙ = a₁ · rⁿ⁻¹",
            "الحد رقم $n: a_$n = $a1 × ($r)^${n - 1} = ${formatValue(an)}",
            "صيغة المجموع Sₙ = a₁(1 - rⁿ) / (1 - r)",
            "مجموع أول $n حداً: S_$n = ${formatValue(sn)}"
        )

        if (sInf != null) {
            steps.add("بما أن |r| = |$r| < 1، فالمتسلسلة متقاربة ومجموعها إلى اللانهاية هو:")
            steps.add("S_∞ = a₁ / (1 - r) = $a1 / (1 - $r) = ${formatValue(sInf)}")
        } else {
            steps.add("بما أن |r| ≥ 1، فالمتسلسلة متباعدة ولا يوجد لها مجموع لانهائي محدد.")
        }

        return SequenceResult(an, sn, sInf, "متتابعة هندسية", steps)
    }

    fun calculateSeriesSum(expression: String, startN: Int, endN: Int): Double {
        var total = 0.0
        for (n in startN..endN) {
            total += evaluator.evaluate(expression, mapOf("n" to n.toDouble(), "x" to n.toDouble()))
        }
        return total
    }

    private fun formatValue(v: Double): String {
        if (v.isNaN()) return "غير معرف (NaN)"
        if (v.isInfinite()) return if (v > 0) "+∞" else "-∞"
        if (abs(v - round(v)) < 1e-9) {
            return round(v).toLong().toString()
        }
        return String.format(java.util.Locale.US, "%.5f", v).trimEnd('0').trimEnd('.')
    }
}
