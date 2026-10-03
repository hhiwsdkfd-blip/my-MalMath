package com.example.math

import kotlin.math.*

/**
 * Geometry Engine for:
 * - 2D Shapes (Triangle, Circle, Rectangle, Trapezoid, Ellipse, Regular Polygon)
 * - 3D Solids (Sphere, Cylinder, Cone, Pyramid, Cuboid)
 * - Coordinate & Analytic Geometry (Distance, Midpoint, Slope, Line equation)
 */
class GeometryEngine {

    data class ShapeResult(
        val shapeName: String,
        val primaryMetricName: String,
        val primaryMetricValue: Double,
        val secondaryMetricName: String,
        val secondaryMetricValue: Double,
        val details: Map<String, String>,
        val formulas: List<String>
    )

    data class CoordinateResult(
        val distance: Double,
        val midpointX: Double,
        val midpointY: Double,
        val slope: Double?,
        val lineEquation: String,
        val angleDeg: Double,
        val steps: List<String>
    )

    // ==========================================
    // 2D SHAPES (الأشكال ثنائية الأبعاد)
    // ==========================================

    fun calculateCircle(radius: Double): ShapeResult {
        val area = PI * radius * radius
        val circumference = 2.0 * PI * radius
        val diameter = 2.0 * radius

        val details = mapOf(
            "نصف القطر (r)" to format(radius),
            "القطر (d)" to format(diameter),
            "المساحة (A)" to format(area),
            "المحيط (C)" to format(circumference)
        )

        val formulas = listOf(
            "المساحة: A = π r² = π × ($radius)² = ${format(area)}",
            "المحيط: C = 2 π r = 2π × $radius = ${format(circumference)}",
            "القطر: d = 2r = ${format(diameter)}"
        )

        return ShapeResult(
            shapeName = "الدائرة",
            primaryMetricName = "المساحة",
            primaryMetricValue = area,
            secondaryMetricName = "المحيط",
            secondaryMetricValue = circumference,
            details = details,
            formulas = formulas
        )
    }

    fun calculateTriangleSSS(a: Double, b: Double, c: Double): ShapeResult {
        val s = (a + b + c) / 2.0 // Semi-perimeter
        val areaSq = s * (s - a) * (s - b) * (s - c)
        val area = if (areaSq > 0) sqrt(areaSq) else 0.0
        val perimeter = a + b + c

        // Angles using Law of Cosines
        val cosA = ((b * b + c * c - a * a) / (2.0 * b * c)).coerceIn(-1.0, 1.0)
        val cosB = ((a * a + c * c - b * b) / (2.0 * a * c)).coerceIn(-1.0, 1.0)
        val angleA = Math.toDegrees(acos(cosA))
        val angleB = Math.toDegrees(acos(cosB))
        val angleC = 180.0 - angleA - angleB

        val inradius = if (s > 0) area / s else 0.0
        val circumradius = if (area > 0) (a * b * c) / (4.0 * area) else 0.0

        val details = mapOf(
            "الأضلاع" to "a=$a, b=$b, c=$c",
            "المساحة (صيغة هيرون)" to format(area),
            "المحيط" to format(perimeter),
            "الزاوية α (المقابلة لـ a)" to "${format(angleA)}°",
            "الزاوية β (المقابلة لـ b)" to "${format(angleB)}°",
            "الزاوية γ (المقابلة لـ c)" to "${format(angleC)}°",
            "نصف قطر الدائرة الداخلية (r)" to format(inradius),
            "نصف قطر الدائرة المحيطة (R)" to format(circumradius)
        )

        val formulas = listOf(
            "نصف المحيط: s = (a + b + c)/2 = ${format(s)}",
            "صيغة هيرون للمساحة: A = √[s(s - a)(s - b)(s - c)] = ${format(area)}",
            "قانون جيب التمام للزوايا: cos(α) = (b² + c² - a²) / 2bc"
        )

        return ShapeResult(
            shapeName = "المثلث (بمعلومية 3 أضلاع)",
            primaryMetricName = "المساحة",
            primaryMetricValue = area,
            secondaryMetricName = "المحيط",
            secondaryMetricValue = perimeter,
            details = details,
            formulas = formulas
        )
    }

    fun calculateRectangle(length: Double, width: Double): ShapeResult {
        val area = length * width
        val perimeter = 2.0 * (length + width)
        val diagonal = sqrt(length * length + width * width)

        val details = mapOf(
            "الطول" to format(length),
            "العرض" to format(width),
            "المساحة" to format(area),
            "المحيط" to format(perimeter),
            "طول القطر" to format(diagonal)
        )

        val formulas = listOf(
            "المساحة: A = الطول × العرض = $length × $width = ${format(area)}",
            "المحيط: P = 2 × (الطول + العرض) = 2 × ($length + $width) = ${format(perimeter)}",
            "القطر (فيثاغورس): d = √(L² + W²) = √($length² + $width²) = ${format(diagonal)}"
        )

        return ShapeResult(
            shapeName = "المستطيل",
            primaryMetricName = "المساحة",
            primaryMetricValue = area,
            secondaryMetricName = "المحيط",
            secondaryMetricValue = perimeter,
            details = details,
            formulas = formulas
        )
    }

    fun calculateTrapezoid(baseA: Double, baseB: Double, height: Double): ShapeResult {
        val area = 0.5 * (baseA + baseB) * height
        val midSegment = 0.5 * (baseA + baseB)

        val details = mapOf(
            "القاعدة الكبرى" to format(max(baseA, baseB)),
            "القاعدة الصغرى" to format(min(baseA, baseB)),
            "الارتفاع" to format(height),
            "المساحة" to format(area),
            "طول القاعدة المتوسطة" to format(midSegment)
        )

        val formulas = listOf(
            "المساحة: A = ((a + b) / 2) × h = (($baseA + $baseB) / 2) × $height = ${format(area)}",
            "القاعدة المتوسطة: m = (a + b) / 2 = ${format(midSegment)}"
        )

        return ShapeResult(
            shapeName = "شبه المنحرف",
            primaryMetricName = "المساحة",
            primaryMetricValue = area,
            secondaryMetricName = "القاعدة المتوسطة",
            secondaryMetricValue = midSegment,
            details = details,
            formulas = formulas
        )
    }

    fun calculateEllipse(axisA: Double, axisB: Double): ShapeResult {
        val area = PI * axisA * axisB
        // Ramanujan perimeter approximation:
        val h = ((axisA - axisB).pow(2)) / ((axisA + axisB).pow(2))
        val perimeter = PI * (axisA + axisB) * (1.0 + (3.0 * h) / (10.0 + sqrt(4.0 - 3.0 * h)))

        val eccentricity = if (axisA >= axisB && axisA > 0) {
            sqrt(1.0 - (axisB * axisB) / (axisA * axisA))
        } else if (axisB > 0) {
            sqrt(1.0 - (axisA * axisA) / (axisB * axisB))
        } else 0.0

        val details = mapOf(
            "نصف المحور الأكبر (a)" to format(max(axisA, axisB)),
            "نصف المحور الأصغر (b)" to format(min(axisA, axisB)),
            "المساحة" to format(area),
            "المحيط التقريبي (رامانوجان)" to format(perimeter),
            "الاختلاف المركزي (e)" to format(eccentricity)
        )

        val formulas = listOf(
            "المساحة: A = π · a · b = π × $axisA × $axisB = ${format(area)}",
            "المحيط (صيغة رامانوجان الدقيقة): P ≈ π(a+b) [1 + 3h / (10 + √(4 - 3h))]",
            "الاختلاف المركزي: e = √(1 - b²/a²) = ${format(eccentricity)}"
        )

        return ShapeResult(
            shapeName = "القطع الناقص (الإهليلج)",
            primaryMetricName = "المساحة",
            primaryMetricValue = area,
            secondaryMetricName = "المحيط",
            secondaryMetricValue = perimeter,
            details = details,
            formulas = formulas
        )
    }

    // ==========================================
    // 3D SOLIDS (المجسمات ثلاثية الأبعاد)
    // ==========================================

    fun calculateSphere(radius: Double): ShapeResult {
        val volume = (4.0 / 3.0) * PI * radius.pow(3)
        val surfaceArea = 4.0 * PI * radius.pow(2)

        val details = mapOf(
            "نصف القطر (r)" to format(radius),
            "الحجم (V)" to format(volume),
            "مساحة السطح الإجمالية (A)" to format(surfaceArea)
        )

        val formulas = listOf(
            "الحجم: V = (4/3) π r³ = (4/3) π × ($radius)³ = ${format(volume)}",
            "مساحة السطح: A = 4 π r² = 4 π × ($radius)² = ${format(surfaceArea)}"
        )

        return ShapeResult(
            shapeName = "الكرة",
            primaryMetricName = "الحجم",
            primaryMetricValue = volume,
            secondaryMetricName = "مساحة السطح",
            secondaryMetricValue = surfaceArea,
            details = details,
            formulas = formulas
        )
    }

    fun calculateCylinder(radius: Double, height: Double): ShapeResult {
        val volume = PI * radius * radius * height
        val lateralArea = 2.0 * PI * radius * height
        val baseArea = PI * radius * radius
        val totalArea = lateralArea + 2.0 * baseArea

        val details = mapOf(
            "نصف قطر القاعدة (r)" to format(radius),
            "الارتفاع (h)" to format(height),
            "الحجم (V)" to format(volume),
            "المساحة الجانبية" to format(lateralArea),
            "المساحة الكلية" to format(totalArea)
        )

        val formulas = listOf(
            "الحجم: V = π r² h = π × ($radius)² × $height = ${format(volume)}",
            "المساحة الجانبية: A_L = 2 π r h = 2 π × $radius × $height = ${format(lateralArea)}",
            "المساحة الكلية: A_T = 2πrh + 2πr² = ${format(totalArea)}"
        )

        return ShapeResult(
            shapeName = "الأسطوانة",
            primaryMetricName = "الحجم",
            primaryMetricValue = volume,
            secondaryMetricName = "المساحة الكلية",
            secondaryMetricValue = totalArea,
            details = details,
            formulas = formulas
        )
    }

    fun calculateCone(radius: Double, height: Double): ShapeResult {
        val slantHeight = sqrt(radius * radius + height * height)
        val volume = (1.0 / 3.0) * PI * radius * radius * height
        val lateralArea = PI * radius * slantHeight
        val totalArea = lateralArea + PI * radius * radius

        val details = mapOf(
            "نصف القطر (r)" to format(radius),
            "الارتفاع (h)" to format(height),
            "طول الراسم (Slant Height)" to format(slantHeight),
            "الحجم (V)" to format(volume),
            "المساحة الجانبية" to format(lateralArea),
            "المساحة الكلية" to format(totalArea)
        )

        val formulas = listOf(
            "طول الراسم (فيثاغورس): s = √(r² + h²) = √($radius² + $height²) = ${format(slantHeight)}",
            "الحجم: V = (1/3) π r² h = (1/3) π × ($radius)² × $height = ${format(volume)}",
            "المساحة الجانبية: A_L = π r s = π × $radius × ${format(slantHeight)} = ${format(lateralArea)}",
            "المساحة الكلية: A_T = π r (s + r) = ${format(totalArea)}"
        )

        return ShapeResult(
            shapeName = "المخروط",
            primaryMetricName = "الحجم",
            primaryMetricValue = volume,
            secondaryMetricName = "المساحة الكلية",
            secondaryMetricValue = totalArea,
            details = details,
            formulas = formulas
        )
    }

    fun calculateCuboid(length: Double, width: Double, height: Double): ShapeResult {
        val volume = length * width * height
        val surfaceArea = 2.0 * (length * width + length * height + width * height)
        val spaceDiagonal = sqrt(length * length + width * width + height * height)

        val details = mapOf(
            "الطول" to format(length),
            "العرض" to format(width),
            "الارتفاع" to format(height),
            "الحجم" to format(volume),
            "المساحة الكلية للسطح" to format(surfaceArea),
            "القطر الداخلي للمجسم" to format(spaceDiagonal)
        )

        val formulas = listOf(
            "الحجم: V = L × W × H = $length × $width × $height = ${format(volume)}",
            "مساحة السطح: A = 2(LW + LH + WH) = ${format(surfaceArea)}",
            "القطر الداخلي: d = √(L² + W² + H²) = ${format(spaceDiagonal)}"
        )

        return ShapeResult(
            shapeName = "متوازي المستطيلات (الصندوق)",
            primaryMetricName = "الحجم",
            primaryMetricValue = volume,
            secondaryMetricName = "المساحة الكلية",
            secondaryMetricValue = surfaceArea,
            details = details,
            formulas = formulas
        )
    }

    // ==========================================
    // COORDINATE GEOMETRY (الهندسة التحليلية)
    // ==========================================

    fun calculateCoordinates(x1: Double, y1: Double, x2: Double, y2: Double): CoordinateResult {
        val dx = x2 - x1
        val dy = y2 - y1
        val distance = sqrt(dx * dx + dy * dy)
        val midX = (x1 + x2) / 2.0
        val midY = (y1 + y2) / 2.0

        val slope = if (abs(dx) > 1e-12) dy / dx else null
        val angleDeg = Math.toDegrees(atan2(dy, dx))

        val lineEq = if (slope != null) {
            // y - y1 = m (x - x1) -> y = mx + (y1 - m*x1)
            val c = y1 - slope * x1
            "y = ${format(slope)}x ${if (c >= 0) "+ " + format(c) else "- " + format(-c)}"
        } else {
            "x = ${format(x1)} (مستقيم رأسي ميله غير معرف)"
        }

        val steps = listOf(
            "النقطة الأولى: A($x1, $y1)، النقطة الثانية: B($x2, $y2)",
            "المسافة بين النقطتين: d = √[(x₂ - x₁)² + (y₂ - y₁)²] = √[(${dx})² + (${dy})²] = ${format(distance)}",
            "إحداثيات نقطة المنتصف: M = ((x₁+x₂)/2, (y₁+y₂)/2) = (${format(midX)}, ${format(midY)})",
            "ميل المستقيم (Slope): m = (y₂ - y₁) / (x₂ - x₁) = ${slope?.let { format(it) } ?: "غير معرف (عمودي)"}",
            "معادلة الخط المستقيم المار بالنقطتين: $lineEq",
            "زاوية ميل المستقيم مع محور السينات الموجب: θ = ${format(angleDeg)}°"
        )

        return CoordinateResult(
            distance = distance,
            midpointX = midX,
            midpointY = midY,
            slope = slope,
            lineEquation = lineEq,
            angleDeg = angleDeg,
            steps = steps
        )
    }

    private fun format(v: Double): String {
        if (abs(v - round(v)) < 1e-9) return round(v).toLong().toString()
        return String.format(java.util.Locale.US, "%.4f", v).trimEnd('0').trimEnd('.')
    }
}
