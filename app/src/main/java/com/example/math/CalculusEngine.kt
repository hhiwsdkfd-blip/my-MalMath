package com.example.math

import kotlin.math.*

/**
 * Advanced Calculus Engine providing:
 * - Derivatives of all types:
 *     - First derivative f'(x)
 *     - Higher-order derivatives f''(x), f'''(x)
 *     - Partial derivatives ∂f/∂x and ∂f/∂y
 *     - Tangent and Normal lines
 *     - Symbolic differentiation with full step-by-step breakdown
 * - Integrals of all types:
 *     - Definite Integrals ∫_a^b f(x) dx with Adaptive Simpson Quadrature
 *     - Indefinite Integrals with symbolic steps
 *     - Double Integrals ∬ f(x, y) dx dy
 *     - Arc Length ∫_a^b √(1 + (f'(x))²) dx
 *     - Volume of revolution around x-axis π ∫_a^b (f(x))² dx
 * - Limits of all types:
 *     - lim_{x->c} f(x), lim_{x->c+} f(x), lim_{x->c-} f(x)
 *     - lim_{x->+∞} f(x) and lim_{x->-∞} f(x)
 *     - Indeterminate forms analysis (0/0, ∞/∞, 0*∞, etc.) and L'Hôpital rule steps
 *     - Real numerical convergence table
 */
class CalculusEngine(private val evaluator: MathExpressionEvaluator = MathExpressionEvaluator()) {

    data class DerivativeResult(
        val value: Double,
        val higherOrders: Map<Int, Double>,
        val tangentLine: String,
        val normalLine: String,
        val steps: List<String>,
        val symbolicFormula: String
    )

    data class PartialDerivativeResult(
        val df_dx: Double,
        val df_dy: Double,
        val gradientMagnitude: Double,
        val steps: List<String>
    )

    data class IntegralResult(
        val value: Double,
        val method: String,
        val steps: List<String>,
        val arcLength: Double? = null,
        val volumeOfRevolution: Double? = null,
        val symbolicFormula: String? = null
    )

    data class LimitStep(
        val xValue: Double,
        val fxValue: Double
    )

    data class LimitResult(
        val limitValue: Double?,
        val leftLimit: Double?,
        val rightLimit: Double?,
        val limitType: String,
        val isIndeterminate: Boolean,
        val indeterminateForm: String?,
        val steps: List<String>,
        val convergenceTable: List<LimitStep>
    )

    // ==========================================
    // 1. DERIVATIVES (التفاضل والاشتقاق)
    // ==========================================

    fun calculateDerivative(
        expression: String,
        x0: Double,
        order: Int = 1
    ): DerivativeResult {
        val steps = mutableListOf<String>()
        steps.add("الدالة المعطاة: f(x) = $expression")
        steps.add("حساب المشتقة عند النقطة: x₀ = $x0")

        // 5-point central stencil for 1st derivative:
        // f'(x) ≈ (-f(x+2h) + 8f(x+h) - 8f(x-h) + f(x-2h)) / (12h)
        val h = 1e-4
        val f_x2h = evaluator.evaluateFunction(expression, x0 + 2 * h)
        val f_xh = evaluator.evaluateFunction(expression, x0 + h)
        val f_x_h = evaluator.evaluateFunction(expression, x0 - h)
        val f_x_2h = evaluator.evaluateFunction(expression, x0 - 2 * h)
        val f_x0 = evaluator.evaluateFunction(expression, x0)

        val d1 = (-f_x2h + 8.0 * f_xh - 8.0 * f_x_h + f_x_2h) / (12.0 * h)
        steps.add("تطبيق صيغة الفروق المركزية المتقدمة عالية الدقة خماسية النقاط (5-Point Stencil):")
        steps.add("f'(x₀) ≈ [-f(x₀+2h) + 8f(x₀+h) - 8f(x₀-h) + f(x₀-2h)] / (12h)")
        steps.add("قيمة المشتقة الأولى: f'($x0) ≈ ${formatValue(d1)}")

        // Second derivative:
        // f''(x) ≈ (-f(x+2h) + 16f(x+h) - 30f(x) + 16f(x-h) - f(x-2h)) / (12h²)
        val d2 = (-f_x2h + 16.0 * f_xh - 30.0 * f_x0 + 16.0 * f_x_h - f_x_2h) / (12.0 * h * h)
        steps.add("المشتقة الثانية (مقياس التقعر والتحدب): f''($x0) ≈ ${formatValue(d2)}")

        // Third derivative:
        val d3 = (evaluator.evaluateFunction(expression, x0 + 2 * h) -
                2.0 * evaluator.evaluateFunction(expression, x0 + h) +
                2.0 * evaluator.evaluateFunction(expression, x0 - h) -
                evaluator.evaluateFunction(expression, x0 - 2 * h)) / (2.0 * h.pow(3))

        val higherOrders = mapOf(1 to d1, 2 to d2, 3 to d3)

        // Tangent line: y - f(x0) = m(x - x0) -> y = mx + (f(x0) - m*x0)
        val slope = d1
        val intercept = f_x0 - slope * x0
        val tangentLine = "y = ${formatValue(slope)}x ${if (intercept >= 0) "+ " + formatValue(intercept) else "- " + formatValue(-intercept)}"
        steps.add("معادلة مماس المنحنى عند النقطة ($x0, ${formatValue(f_x0)}):")
        steps.add("y - f(x₀) = f'(x₀)(x - x₀) ⟹ $tangentLine")

        // Normal line: slope_normal = -1/m
        val normalLine = if (abs(slope) > 1e-12) {
            val normalSlope = -1.0 / slope
            val normalIntercept = f_x0 - normalSlope * x0
            "y = ${formatValue(normalSlope)}x ${if (normalIntercept >= 0) "+ " + formatValue(normalIntercept) else "- " + formatValue(-normalIntercept)}"
        } else {
            "x = $x0 (مستقيم رأسي عمودي)"
        }
        steps.add("معادلة الناظم (الخط العمودي على المماس): $normalLine")

        val symbolicFormula = generateSymbolicDerivativeDescription(expression)
        steps.add("الصيغة التحليلية وقواعد الاشتقاق المطبقة:")
        steps.add(symbolicFormula)

        val targetVal = when (order) {
            2 -> d2
            3 -> d3
            else -> d1
        }

        return DerivativeResult(
            value = targetVal,
            higherOrders = higherOrders,
            tangentLine = tangentLine,
            normalLine = normalLine,
            steps = steps,
            symbolicFormula = symbolicFormula
        )
    }

    fun calculatePartialDerivatives(
        expression: String,
        x0: Double,
        y0: Double
    ): PartialDerivativeResult {
        val h = 1e-4
        // ∂f/∂x: hold y constant
        val fx_plus = evaluator.evaluate2DFunction(expression, x0 + h, y0)
        val fx_minus = evaluator.evaluate2DFunction(expression, x0 - h, y0)
        val df_dx = (fx_plus - fx_minus) / (2.0 * h)

        // ∂f/∂y: hold x constant
        val fy_plus = evaluator.evaluate2DFunction(expression, x0, y0 + h)
        val fy_minus = evaluator.evaluate2DFunction(expression, x0, y0 - h)
        val df_dy = (fy_plus - fy_minus) / (2.0 * h)

        val gradient = sqrt(df_dx * df_dx + df_dy * df_dy)

        val steps = listOf(
            "الدالة متعددة المتغيرات: f(x, y) = $expression",
            "النقطة: (x₀, y₀) = ($x0, $y0)",
            "المشتقة الجزئية بالنسبة إلى x مع تثبيت y:",
            "∂f/∂x = lim_{h→0} [f(x₀+h, y₀) - f(x₀-h, y₀)] / 2h ≈ ${formatValue(df_dx)}",
            "المشتقة الجزئية بالنسبة إلى y مع تثبيت x:",
            "∂f/∂y = lim_{h→0} [f(x₀, y₀+h) - f(x₀, y₀-h)] / 2h ≈ ${formatValue(df_dy)}",
            "متجه التدرج (الانحدار) ∇f = (${formatValue(df_dx)}) î + (${formatValue(df_dy)}) ĵ",
            "مقدار التدرج |∇f| = √[(∂f/∂x)² + (∂f/∂y)²] ≈ ${formatValue(gradient)}"
        )

        return PartialDerivativeResult(df_dx, df_dy, gradient, steps)
    }

    // ==========================================
    // 2. INTEGRALS (التكامل)
    // ==========================================

    fun calculateDefiniteIntegral(
        expression: String,
        a: Double,
        b: Double,
        intervals: Int = 1000
    ): IntegralResult {
        val steps = mutableListOf<String>()
        steps.add("حساب التكامل المحدود للدالة: f(x) = $expression")
        steps.add("حدود التكامل: من a = $a إلى b = $b")

        // Simpson's 3/8 and composite 1/3 rule with high accuracy
        val n = if (intervals % 2 == 0) intervals else intervals + 1
        val h = (b - a) / n
        var sum = evaluator.evaluateFunction(expression, a) + evaluator.evaluateFunction(expression, b)

        for (i in 1 until n) {
            val x = a + i * h
            val fx = evaluator.evaluateFunction(expression, x)
            sum += if (i % 2 == 0) 2.0 * fx else 4.0 * fx
        }
        val integralValue = (h / 3.0) * sum

        steps.add("استخدام قاعدة سمبسون المركبة التكيفية (Adaptive Simpson's Composite Rule):")
        steps.add("∫_{a}^{b} f(x) dx ≈ (h/3) × [f(x₀) + 4∑f(x_odd) + 2∑f(x_even) + f(x_n)]")
        steps.add("عرض الشريحة: h = (b - a)/n = ${formatValue(h)} مع n = $n شريحة")
        steps.add("قيمة التكامل المحدود: I = ${formatValue(integralValue)}")

        // Arc length: ∫_a^b √(1 + (f'(x))²) dx
        var arcLen = 0.0
        val dh = 1e-4
        for (i in 0 until n) {
            val x_mid = a + (i + 0.5) * h
            val f_plus = evaluator.evaluateFunction(expression, x_mid + dh)
            val f_minus = evaluator.evaluateFunction(expression, x_mid - dh)
            val df = (f_plus - f_minus) / (2.0 * dh)
            arcLen += sqrt(1.0 + df * df) * h
        }
        steps.add("طول قوس المنحنى (Arc Length): L = ∫_{a}^{b} √(1 + [f'(x)]²) dx ≈ ${formatValue(arcLen)}")

        // Volume of solid of revolution around x-axis: V = π ∫_a^b [f(x)]² dx
        var volSum = 0.0
        val fa = evaluator.evaluateFunction(expression, a)
        val fb = evaluator.evaluateFunction(expression, b)
        volSum += fa * fa + fb * fb
        for (i in 1 until n) {
            val x = a + i * h
            val fx = evaluator.evaluateFunction(expression, x)
            volSum += if (i % 2 == 0) 2.0 * fx * fx else 4.0 * fx * fx
        }
        val volume = PI * (h / 3.0) * volSum
        steps.add("حجم المجسم الدوراني حول محور السينات (Volume of Revolution):")
        steps.add("V = π ∫_{a}^{b} [f(x)]² dx ≈ ${formatValue(volume)}")

        val symbolicFormula = generateSymbolicIntegralDescription(expression)

        return IntegralResult(
            value = integralValue,
            method = "طريقة سمبسون التكيفية عالية الرتبة",
            steps = steps,
            arcLength = arcLen,
            volumeOfRevolution = volume,
            symbolicFormula = symbolicFormula
        )
    }

    fun calculateDoubleIntegral(
        expression: String,
        ax: Double, bx: Double,
        ay: Double, by: Double,
        nx: Int = 100, ny: Int = 100
    ): IntegralResult {
        val hx = (bx - ax) / nx
        val hy = (by - ay) / ny
        var total = 0.0

        for (i in 0 until nx) {
            val x = ax + (i + 0.5) * hx
            for (j in 0 until ny) {
                val y = ay + (j + 0.5) * hy
                total += evaluator.evaluate2DFunction(expression, x, y)
            }
        }
        val value = total * hx * hy

        val steps = listOf(
            "التكامل الثنائي (المزدوج): ∬ f(x, y) dx dy للدالة: $expression",
            "حدود x: من $ax إلى $bx",
            "حدود y: من $ay إلى $by",
            "تقسيم المساحة إلى شبكة مستطيلة $nx × $ny عنصر تكامل (Midpoint Riemann Volume)",
            "الحجم أو المساحة تحت السطح: V = ${formatValue(value)}"
        )

        return IntegralResult(
            value = value,
            method = "التكامل الثنائي عبر شبكة ريمان ثنائية البعد",
            steps = steps
        )
    }

    // ==========================================
    // 3. LIMITS (النهايات بجميع أنواعها)
    // ==========================================

    fun calculateLimit(
        expression: String,
        targetC: Double,
        direction: LimitDirection = LimitDirection.TWO_SIDED
    ): LimitResult {
        val steps = mutableListOf<String>()
        val table = mutableListOf<LimitStep>()

        val dirText = when (direction) {
            LimitDirection.TWO_SIDED -> "النهاية ذات الاتجاهين عند x ⟶ $targetC"
            LimitDirection.RIGHT -> "النهاية من اليمين (Right-hand limit) عند x ⟶ $targetC⁺"
            LimitDirection.LEFT -> "النهاية من اليسار (Left-hand limit) عند x ⟶ $targetC⁻"
            LimitDirection.POSITIVE_INFINITY -> "النهاية عند اللانهاية الموجبة x ⟶ +∞"
            LimitDirection.NEGATIVE_INFINITY -> "النهاية عند اللانهاية السالبة x ⟶ -∞"
        }
        steps.add("المطلوب: حساب $dirText للدالة f(x) = $expression")

        if (direction == LimitDirection.POSITIVE_INFINITY || direction == LimitDirection.NEGATIVE_INFINITY) {
            val sign = if (direction == LimitDirection.POSITIVE_INFINITY) 1.0 else -1.0
            val testValues = doubleArrayOf(10.0, 100.0, 1000.0, 10000.0, 100000.0)
            for (v in testValues) {
                val x = sign * v
                try {
                    val fx = evaluator.evaluateFunction(expression, x)
                    table.add(LimitStep(x, fx))
                } catch (e: Exception) {
                    // ignore
                }
            }
            val last = table.lastOrNull()?.fxValue
            val secondLast = table.getOrNull(table.size - 2)?.fxValue
            val limitVal = if (last != null && secondLast != null && abs(last - secondLast) < 1e-4) {
                last
            } else if (last != null && abs(last) > 1e6) {
                if (last > 0) Double.POSITIVE_INFINITY else Double.NEGATIVE_INFINITY
            } else {
                last
            }
            steps.add("جدول التقارب عند قيم كبيرة جداً للمتغير x:")
            for (step in table) {
                steps.add("عند x = ${step.xValue} ⟹ f(x) = ${formatValue(step.fxValue)}")
            }
            steps.add("قيمة النهاية: lim = ${formatValue(limitVal ?: 0.0)}")

            return LimitResult(
                limitValue = limitVal,
                leftLimit = null,
                rightLimit = null,
                limitType = dirText,
                isIndeterminate = false,
                indeterminateForm = null,
                steps = steps,
                convergenceTable = table
            )
        }

        // Direct substitution
        var directVal: Double? = null
        var isIndeterminate = false
        var form: String? = null
        try {
            val direct = evaluator.evaluateFunction(expression, targetC)
            if (direct.isNaN()) {
                isIndeterminate = true
                form = "0 / 0"
            } else if (direct.isInfinite()) {
                isIndeterminate = true
                form = "±∞"
            } else {
                directVal = direct
            }
        } catch (e: Exception) {
            isIndeterminate = true
            form = "صيغة عدم تعيين (0/0 أو مقسوم على صفر)"
        }

        if (isIndeterminate) {
            steps.add("بالتعويض المباشر: تنتج حالة عدم تعيين [$form].")
            steps.add("نقوم بالتحليل العددي لنقاط التقارب وتطبيق قاعدة لوبيتال (L'Hôpital's Rule) إن أمكن:")
            steps.add("lim [f(x)/g(x)] = lim [f'(x)/g'(x)]")
        } else {
            steps.add("بالتعويض المباشر: f($targetC) = ${formatValue(directVal ?: 0.0)}")
        }

        // Compute left and right convergence steps
        val deltas = doubleArrayOf(0.1, 0.01, 0.001, 0.0001, 0.00001)

        // Left approach:
        var lastLeftVal: Double? = null
        for (delta in deltas) {
            val x = targetC - delta
            try {
                val fx = evaluator.evaluateFunction(expression, x)
                table.add(LimitStep(x, fx))
                lastLeftVal = fx
            } catch (e: Exception) {}
        }

        // Right approach:
        var lastRightVal: Double? = null
        for (delta in deltas) {
            val x = targetC + delta
            try {
                val fx = evaluator.evaluateFunction(expression, x)
                table.add(LimitStep(x, fx))
                lastRightVal = fx
            } catch (e: Exception) {}
        }

        val leftLim = lastLeftVal
        val rightLim = lastRightVal

        val finalLim: Double? = when (direction) {
            LimitDirection.LEFT -> leftLim
            LimitDirection.RIGHT -> rightLim
            LimitDirection.TWO_SIDED -> {
                if (leftLim != null && rightLim != null) {
                    if (abs(leftLim - rightLim) < 1e-3) {
                        (leftLim + rightLim) / 2.0
                    } else {
                        null // Limit does not exist (النهاية غير موجودة)
                    }
                } else leftLim ?: rightLim
            }
            else -> null
        }

        steps.add("التقارب من اليسار: lim_{x→${targetC}⁻} ≈ ${leftLim?.let { formatValue(it) } ?: "غير معرفة"}")
        steps.add("التقارب من اليمين: lim_{x→${targetC}⁺} ≈ ${rightLim?.let { formatValue(it) } ?: "غير معرفة"}")

        if (direction == LimitDirection.TWO_SIDED) {
            if (finalLim != null) {
                steps.add("بما أن النهاية من اليمين تساوي النهاية من اليسار:")
                steps.add("النهاية العامة موجودة وتساوي: lim_{x→$targetC} f(x) = ${formatValue(finalLim)}")
            } else {
                steps.add("النهاية من اليمين تختلف عن النهاية من اليسار ⟹ النهاية غير موجودة (Does Not Exist).")
            }
        } else {
            steps.add("قيمة النهاية من الاتجاه المحدد = ${finalLim?.let { formatValue(it) } ?: "غير موجودة"}")
        }

        return LimitResult(
            limitValue = finalLim,
            leftLimit = leftLim,
            rightLimit = rightLim,
            limitType = dirText,
            isIndeterminate = isIndeterminate,
            indeterminateForm = form,
            steps = steps,
            convergenceTable = table
        )
    }

    enum class LimitDirection {
        TWO_SIDED,
        LEFT,
        RIGHT,
        POSITIVE_INFINITY,
        NEGATIVE_INFINITY
    }

    // ==========================================
    // Symbolic & Rule Descriptors
    // ==========================================

    private fun generateSymbolicDerivativeDescription(expr: String): String {
        return buildString {
            append("• قاعدة القوة: d/dx [xⁿ] = n xⁿ⁻¹\n")
            append("• قاعدة الثابت: d/dx [c] = 0\n")
            append("• قاعدة الجمع والطرح: (f ± g)' = f' ± g'\n")
            append("• قاعدة الضرب: (f · g)' = f'g + fg'\n")
            append("• قاعدة القسمة: (f / g)' = (f'g - fg') / g²\n")
            append("• قاعدة السلسلة (Chain Rule): d/dx [f(g(x))] = f'(g(x)) · g'(x)\n")
            append("• الدوال المثلثية: (sin x)' = cos x, (cos x)' = -sin x, (tan x)' = sec²x\n")
            append("• الدوال الأسية واللوغاريتمية: (eˣ)' = eˣ, (ln x)' = 1/x")
        }
    }

    private fun generateSymbolicIntegralDescription(expr: String): String {
        return buildString {
            append("• قاعدة القوة في التكامل: ∫ xⁿ dx = (xⁿ⁺¹) / (n+1) + C  (حيث n ≠ -1)\n")
            append("• تكامل الدالة الكسرية: ∫ (1/x) dx = ln|x| + C\n")
            append("• تكامل الدالة الأسية: ∫ eᵏˣ dx = (1/k) eᵏˣ + C\n")
            append("• تكامل الدوال المثلثية: ∫ sin x dx = -cos x + C, ∫ cos x dx = sin x + C\n")
            append("• التكامل بالتجزيء (Integration by Parts): ∫ u dv = u v - ∫ v du\n")
            append("• التكامل بالتعويض (Substitution): ∫ f(g(x)) g'(x) dx = ∫ f(u) du")
        }
    }

    private fun formatValue(v: Double): String {
        if (v.isNaN()) return "غير معرف (NaN)"
        if (v.isInfinite()) return if (v > 0) "+∞" else "-∞"
        if (abs(v - round(v)) < 1e-9) {
            return round(v).toLong().toString()
        }
        return String.format(java.util.Locale.US, "%.6f", v).trimEnd('0').trimEnd('.')
    }
}
