package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiMathService
import com.example.data.AppDatabase
import com.example.data.CalculationEntity
import com.example.math.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.abs

enum class CalculatorTab(val title: String) {
    SCIENTIFIC("علمية"),
    CALCULUS("تفاضل وتكامل"),
    LIMITS("النهايات"),
    ALGEBRA("الجبر"),
    GEOMETRY("الهندسة"),
    GRAPH("رسم بياني"),
    AI_SOLVER("المعلم الذكي")
}

enum class KeypadStyle {
    CASIO_NATURAL, // Calc Business & HiEdu style with SHIFT, ALPHA, secondary text
    MALMATH        // MalMath style with fast calculus buttons and direct variables
}

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val calculationDao = db.calculationDao()

    val evaluator = MathExpressionEvaluator()
    val calculusEngine = CalculusEngine(evaluator)
    val algebraEngine = AlgebraEngine(evaluator)
    val geometryEngine = GeometryEngine()
    val aiService = GeminiMathService()
    val stepSolver = OfflineStepSolver(evaluator)

    // History Flow
    val historyList: StateFlow<List<CalculationEntity>> = calculationDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current Tab
    private val _currentTab = MutableStateFlow(CalculatorTab.SCIENTIFIC)
    val currentTab: StateFlow<CalculatorTab> = _currentTab.asStateFlow()

    // Keypad Layout Style (Casio vs MalMath)
    private val _keypadStyle = MutableStateFlow(KeypadStyle.CASIO_NATURAL)
    val keypadStyle: StateFlow<KeypadStyle> = _keypadStyle.asStateFlow()

    // Dialogs / Sheets
    private val _showOfflineSolution = MutableStateFlow(false)
    val showOfflineSolution: StateFlow<Boolean> = _showOfflineSolution.asStateFlow()

    private val _currentOfflineSolution = MutableStateFlow<OfflineStepSolver.Solution?>(null)
    val currentOfflineSolution: StateFlow<OfflineStepSolver.Solution?> = _currentOfflineSolution.asStateFlow()

    private val _showConstantsSheet = MutableStateFlow(false)
    val showConstantsSheet: StateFlow<Boolean> = _showConstantsSheet.asStateFlow()

    private val _showConverterSheet = MutableStateFlow(false)
    val showConverterSheet: StateFlow<Boolean> = _showConverterSheet.asStateFlow()

    // ==========================================
    // 1. SCIENTIFIC CALCULATOR STATE
    // ==========================================
    private val _sciExpression = MutableStateFlow("")
    val sciExpression: StateFlow<String> = _sciExpression.asStateFlow()

    private val _cursorPosition = MutableStateFlow(0)
    val cursorPosition: StateFlow<Int> = _cursorPosition.asStateFlow()

    private val _sciResult = MutableStateFlow("0")
    val sciResult: StateFlow<String> = _sciResult.asStateFlow()

    private val _isFractionDisplay = MutableStateFlow(false)
    val isFractionDisplay: StateFlow<Boolean> = _isFractionDisplay.asStateFlow()

    private val _sciError = MutableStateFlow<String?>(null)
    val sciError: StateFlow<String?> = _sciError.asStateFlow()

    private val _isDegreeMode = MutableStateFlow(false)
    val isDegreeMode: StateFlow<Boolean> = _isDegreeMode.asStateFlow()

    private val _isShiftMode = MutableStateFlow(false)
    val isShiftMode: StateFlow<Boolean> = _isShiftMode.asStateFlow()

    private val _isAlphaMode = MutableStateFlow(false)
    val isAlphaMode: StateFlow<Boolean> = _isAlphaMode.asStateFlow()

    private var memoryValue = 0.0
    private var lastAns = 0.0
    private var lastDecimalResult = 0.0

    fun setTab(tab: CalculatorTab) {
        _currentTab.value = tab
    }

    fun toggleKeypadStyle() {
        _keypadStyle.value = if (_keypadStyle.value == KeypadStyle.CASIO_NATURAL) KeypadStyle.MALMATH else KeypadStyle.CASIO_NATURAL
    }

    fun toggleDegreeMode() {
        _isDegreeMode.value = !_isDegreeMode.value
        evaluator.isDegreeMode = _isDegreeMode.value
    }

    fun toggleShiftMode() {
        _isShiftMode.value = !_isShiftMode.value
        if (_isShiftMode.value) _isAlphaMode.value = false
    }

    fun toggleAlphaMode() {
        _isAlphaMode.value = !_isAlphaMode.value
        if (_isAlphaMode.value) _isShiftMode.value = false
    }

    fun setShowConstantsSheet(show: Boolean) {
        _showConstantsSheet.value = show
    }

    fun setShowConverterSheet(show: Boolean) {
        _showConverterSheet.value = show
    }

    fun closeOfflineSolution() {
        _showOfflineSolution.value = false
    }

    fun appendSci(text: String) {
        _sciError.value = null
        val insertText = if (text == "Ans") formatNumber(lastAns) else text
        val current = _sciExpression.value
        val pos = _cursorPosition.value.coerceIn(0, current.length)

        val newExpr = current.substring(0, pos) + insertText + current.substring(pos)
        _sciExpression.value = newExpr
        _cursorPosition.value = pos + insertText.length

        // Reset modifiers after one stroke if enabled
        if (_isShiftMode.value) _isShiftMode.value = false
        if (_isAlphaMode.value) _isAlphaMode.value = false
    }

    fun backspaceSci() {
        _sciError.value = null
        val current = _sciExpression.value
        val pos = _cursorPosition.value.coerceIn(0, current.length)
        if (pos > 0 && current.isNotEmpty()) {
            val newExpr = current.substring(0, pos - 1) + current.substring(pos)
            _sciExpression.value = newExpr
            _cursorPosition.value = pos - 1
        }
    }

    fun clearSci() {
        _sciExpression.value = ""
        _sciResult.value = "0"
        _cursorPosition.value = 0
        _sciError.value = null
    }

    fun moveCursorLeft() {
        if (_cursorPosition.value > 0) {
            _cursorPosition.value -= 1
        }
    }

    fun moveCursorRight() {
        if (_cursorPosition.value < _sciExpression.value.length) {
            _cursorPosition.value += 1
        }
    }

    fun calculateSci() {
        val expr = _sciExpression.value.trim()
        if (expr.isEmpty()) return
        try {
            evaluator.isDegreeMode = _isDegreeMode.value
            val res = evaluator.evaluate(expr)
            lastAns = res
            lastDecimalResult = res
            _isFractionDisplay.value = false
            val formatted = formatNumber(res)
            _sciResult.value = formatted
            _sciError.value = null

            // Save to history
            saveHistory("حساب علمي", expr, formatted, "")
        } catch (e: Exception) {
            _sciError.value = e.message ?: "تعبير غير صالح"
        }
    }

    // S <=> D (Toggle between Standard Fraction/Radical and Decimal representation)
    fun toggleFractionDecimal() {
        if (_sciResult.value == "0" && lastDecimalResult == 0.0) return
        _isFractionDisplay.value = !_isFractionDisplay.value

        if (_isFractionDisplay.value) {
            val frac = Fraction.fromDouble(lastDecimalResult)
            _sciResult.value = frac.toPrettyString()
        } else {
            _sciResult.value = formatNumber(lastDecimalResult)
        }
    }

    // MalMath SOLVE feature: 100% OFFLINE Step-by-Step Solver
    fun solveOfflineCurrentExpression() {
        val expr = _sciExpression.value.trim()
        if (expr.isEmpty()) return

        try {
            val solution = stepSolver.solveOffline(expr)
            _currentOfflineSolution.value = solution
            _showOfflineSolution.value = true

            // Also update result
            _sciResult.value = solution.finalAnswer

            saveHistory("حل خطوة بخطوة (بدون إنترنت)", expr, solution.finalAnswer, solution.steps.joinToString("\n") { "${it.title}: ${it.formula}" })
        } catch (e: Exception) {
            _sciError.value = e.message ?: "فشل الحل دون إنترنت"
        }
    }

    // MalMath GRAPH feature: Open Graphing tab with current expression
    fun openGraphWithCurrentExpr() {
        val expr = _sciExpression.value.trim()
        if (expr.isNotEmpty()) {
            graphExpr.value = expr
        }
        _currentTab.value = CalculatorTab.GRAPH
    }

    fun insertConstant(c: ScientificConstant) {
        appendSci(c.value.toString())
        _showConstantsSheet.value = false
    }

    fun memoryAdd() {
        val res = _sciResult.value.toDoubleOrNull() ?: lastDecimalResult
        memoryValue += res
    }

    fun memorySubtract() {
        val res = _sciResult.value.toDoubleOrNull() ?: lastDecimalResult
        memoryValue -= res
    }

    fun memoryRecall() {
        appendSci(formatNumber(memoryValue))
    }

    fun memoryClear() {
        memoryValue = 0.0
    }

    // ==========================================
    // 2. CALCULUS STATE (تفاضل وتكامل)
    // ==========================================
    enum class CalculusMode { DERIVATIVE, INTEGRAL, PARTIAL_DERIVATIVE, DOUBLE_INTEGRAL }

    private val _calculusMode = MutableStateFlow(CalculusMode.DERIVATIVE)
    val calculusMode: StateFlow<CalculusMode> = _calculusMode.asStateFlow()

    val derivExpr = MutableStateFlow("x^3 - 3*x + 2")
    val derivPoint = MutableStateFlow("2")
    val derivOrder = MutableStateFlow(1)
    private val _derivResult = MutableStateFlow<CalculusEngine.DerivativeResult?>(null)
    val derivResult: StateFlow<CalculusEngine.DerivativeResult?> = _derivResult.asStateFlow()

    val partialExpr = MutableStateFlow("x^2 * y + sin(x*y)")
    val partialX0 = MutableStateFlow("1")
    val partialY0 = MutableStateFlow("2")
    private val _partialResult = MutableStateFlow<CalculusEngine.PartialDerivativeResult?>(null)
    val partialResult: StateFlow<CalculusEngine.PartialDerivativeResult?> = _partialResult.asStateFlow()

    val integExpr = MutableStateFlow("sin(x) + x^2")
    val integA = MutableStateFlow("0")
    val integB = MutableStateFlow("pi")
    private val _integResult = MutableStateFlow<CalculusEngine.IntegralResult?>(null)
    val integResult: StateFlow<CalculusEngine.IntegralResult?> = _integResult.asStateFlow()

    val dIntegExpr = MutableStateFlow("x * y")
    val dIntegAx = MutableStateFlow("0")
    val dIntegBx = MutableStateFlow("2")
    val dIntegAy = MutableStateFlow("0")
    val dIntegBy = MutableStateFlow("3")
    private val _dIntegResult = MutableStateFlow<CalculusEngine.IntegralResult?>(null)
    val dIntegResult: StateFlow<CalculusEngine.IntegralResult?> = _dIntegResult.asStateFlow()

    val calculusError = MutableStateFlow<String?>(null)

    fun setCalculusMode(mode: CalculusMode) {
        _calculusMode.value = mode
        calculusError.value = null
    }

    fun solveDerivative() {
        calculusError.value = null
        try {
            val expr = derivExpr.value
            val x0 = derivPoint.value.toDoubleOrNull() ?: 0.0
            val order = derivOrder.value
            val res = calculusEngine.calculateDerivative(expr, x0, order)
            _derivResult.value = res
            saveHistory("تفاضل (مشتقة)", "d/dx [$expr] عند x=$x0", formatNumber(res.value), res.steps.joinToString("\n"))
        } catch (e: Exception) {
            calculusError.value = e.message ?: "خطأ في حساب الاشتقاق"
        }
    }

    fun solvePartialDerivative() {
        calculusError.value = null
        try {
            val expr = partialExpr.value
            val x0 = partialX0.value.toDoubleOrNull() ?: 0.0
            val y0 = partialY0.value.toDoubleOrNull() ?: 0.0
            val res = calculusEngine.calculatePartialDerivatives(expr, x0, y0)
            _partialResult.value = res
            saveHistory("مشتقات جزئية", "f(x,y)=$expr عند ($x0, $y0)", "∂f/∂x=${formatNumber(res.df_dx)}, ∂f/∂y=${formatNumber(res.df_dy)}", res.steps.joinToString("\n"))
        } catch (e: Exception) {
            calculusError.value = e.message ?: "خطأ في حساب المشتقات الجزئية"
        }
    }

    fun solveIntegral() {
        calculusError.value = null
        try {
            val expr = integExpr.value
            val a = evaluator.evaluate(integA.value)
            val b = evaluator.evaluate(integB.value)
            val res = calculusEngine.calculateDefiniteIntegral(expr, a, b)
            _integResult.value = res
            saveHistory("تكامل محدود", "∫ [$expr] من $a إلى $b", formatNumber(res.value), res.steps.joinToString("\n"))
        } catch (e: Exception) {
            calculusError.value = e.message ?: "خطأ في حساب التكامل"
        }
    }

    fun solveDoubleIntegral() {
        calculusError.value = null
        try {
            val expr = dIntegExpr.value
            val ax = evaluator.evaluate(dIntegAx.value)
            val bx = evaluator.evaluate(dIntegBx.value)
            val ay = evaluator.evaluate(dIntegAy.value)
            val by = evaluator.evaluate(dIntegBy.value)
            val res = calculusEngine.calculateDoubleIntegral(expr, ax, bx, ay, by)
            _dIntegResult.value = res
            saveHistory("تكامل ثنائي", "∬ [$expr] x:[$ax, $bx], y:[$ay, $by]", formatNumber(res.value), res.steps.joinToString("\n"))
        } catch (e: Exception) {
            calculusError.value = e.message ?: "خطأ في حساب التكامل الثنائي"
        }
    }

    // ==========================================
    // 3. LIMITS STATE (النهايات)
    // ==========================================
    val limitExpr = MutableStateFlow("(sin(x)) / x")
    val limitC = MutableStateFlow("0")
    val limitDirection = MutableStateFlow(CalculusEngine.LimitDirection.TWO_SIDED)
    private val _limitResult = MutableStateFlow<CalculusEngine.LimitResult?>(null)
    val limitResult: StateFlow<CalculusEngine.LimitResult?> = _limitResult.asStateFlow()
    val limitError = MutableStateFlow<String?>(null)

    fun solveLimit() {
        limitError.value = null
        try {
            val expr = limitExpr.value
            val c = if (limitDirection.value == CalculusEngine.LimitDirection.POSITIVE_INFINITY ||
                limitDirection.value == CalculusEngine.LimitDirection.NEGATIVE_INFINITY) {
                0.0
            } else {
                evaluator.evaluate(limitC.value)
            }
            val res = calculusEngine.calculateLimit(expr, c, limitDirection.value)
            _limitResult.value = res
            val resStr = res.limitValue?.let { formatNumber(it) } ?: "غير موجودة (DNE)"
            saveHistory("نهاية", "lim [$expr] عند ${res.limitType}", resStr, res.steps.joinToString("\n"))
        } catch (e: Exception) {
            limitError.value = e.message ?: "خطأ في حساب النهاية"
        }
    }

    // ==========================================
    // 4. ALGEBRA STATE (الجبر)
    // ==========================================
    enum class AlgebraMode { QUADRATIC, LINEAR_2X2, LINEAR_3X3, MATRIX_2X2, SEQUENCES }

    private val _algebraMode = MutableStateFlow(AlgebraMode.QUADRATIC)
    val algebraMode: StateFlow<AlgebraMode> = _algebraMode.asStateFlow()

    val quadA = MutableStateFlow("1")
    val quadB = MutableStateFlow("-5")
    val quadC = MutableStateFlow("6")
    private val _quadResult = MutableStateFlow<AlgebraEngine.QuadraticResult?>(null)
    val quadResult: StateFlow<AlgebraEngine.QuadraticResult?> = _quadResult.asStateFlow()

    val sysA1 = MutableStateFlow("2")
    val sysB1 = MutableStateFlow("3")
    val sysC1 = MutableStateFlow("8")
    val sysA2 = MutableStateFlow("1")
    val sysB2 = MutableStateFlow("-1")
    val sysC2 = MutableStateFlow("4")
    private val _sys2Result = MutableStateFlow<AlgebraEngine.LinearSystem2Result?>(null)
    val sys2Result: StateFlow<AlgebraEngine.LinearSystem2Result?> = _sys2Result.asStateFlow()

    val matA = MutableStateFlow("4")
    val matB = MutableStateFlow("7")
    val matC = MutableStateFlow("2")
    val matD = MutableStateFlow("6")
    private val _matrixResult = MutableStateFlow<AlgebraEngine.MatrixResult?>(null)
    val matrixResult: StateFlow<AlgebraEngine.MatrixResult?> = _matrixResult.asStateFlow()

    val seqA1 = MutableStateFlow("2")
    val seqDiffRatio = MutableStateFlow("3")
    val seqN = MutableStateFlow("10")
    val isGeometric = MutableStateFlow(false)
    private val _seqResult = MutableStateFlow<AlgebraEngine.SequenceResult?>(null)
    val seqResult: StateFlow<AlgebraEngine.SequenceResult?> = _seqResult.asStateFlow()

    val algebraError = MutableStateFlow<String?>(null)

    fun setAlgebraMode(mode: AlgebraMode) {
        _algebraMode.value = mode
        algebraError.value = null
    }

    fun solveQuadratic() {
        algebraError.value = null
        try {
            val a = quadA.value.toDoubleOrNull() ?: 1.0
            val b = quadB.value.toDoubleOrNull() ?: 0.0
            val c = quadC.value.toDoubleOrNull() ?: 0.0
            val res = algebraEngine.solveQuadratic(a, b, c)
            _quadResult.value = res
            saveHistory("معادلة تربيعية", "${a}x² + ${b}x + $c = 0", "الجذور: ${res.root1}, ${res.root2}", res.steps.joinToString("\n"))
        } catch (e: Exception) {
            algebraError.value = e.message ?: "خطأ في حل المعادلة"
        }
    }

    fun solve2x2System() {
        algebraError.value = null
        try {
            val a1 = sysA1.value.toDoubleOrNull() ?: 0.0
            val b1 = sysB1.value.toDoubleOrNull() ?: 0.0
            val c1 = sysC1.value.toDoubleOrNull() ?: 0.0
            val a2 = sysA2.value.toDoubleOrNull() ?: 0.0
            val b2 = sysB2.value.toDoubleOrNull() ?: 0.0
            val c2 = sysC2.value.toDoubleOrNull() ?: 0.0
            val res = algebraEngine.solve2x2(a1, b1, c1, a2, b2, c2)
            _sys2Result.value = res
            val resStr = if (res.x != null && res.y != null) "x=${formatNumber(res.x)}, y=${formatNumber(res.y)}" else res.statusMessage
            saveHistory("نظام معادلات 2x2", "${a1}x+${b1}y=$c1, ${a2}x+${b2}y=$c2", resStr, res.steps.joinToString("\n"))
        } catch (e: Exception) {
            algebraError.value = e.message ?: "خطأ في حل نظام المعادلات"
        }
    }

    fun solveMatrix2x2() {
        algebraError.value = null
        try {
            val a = matA.value.toDoubleOrNull() ?: 0.0
            val b = matB.value.toDoubleOrNull() ?: 0.0
            val c = matC.value.toDoubleOrNull() ?: 0.0
            val d = matD.value.toDoubleOrNull() ?: 0.0
            val res = algebraEngine.analyzeMatrix2x2(a, b, c, d)
            _matrixResult.value = res
            saveHistory("مصفوفة 2x2", "det=[[$a,$b],[$c,$d]]", "det=${formatNumber(res.determinant ?: 0.0)}", res.steps.joinToString("\n"))
        } catch (e: Exception) {
            algebraError.value = e.message ?: "خطأ في معالجة المصفوفة"
        }
    }

    fun solveSequence() {
        algebraError.value = null
        try {
            val a1 = seqA1.value.toDoubleOrNull() ?: 0.0
            val dOrR = seqDiffRatio.value.toDoubleOrNull() ?: 1.0
            val n = seqN.value.toIntOrNull() ?: 10
            val res = if (isGeometric.value) {
                algebraEngine.calculateGeometricProgression(a1, dOrR, n)
            } else {
                algebraEngine.calculateArithmeticProgression(a1, dOrR, n)
            }
            _seqResult.value = res
            saveHistory("متتابعات", "a1=$a1, اساس=$dOrR, n=$n", "a_n=${formatNumber(res.nthTerm)}, Sn=${formatNumber(res.sumN)}", res.steps.joinToString("\n"))
        } catch (e: Exception) {
            algebraError.value = e.message ?: "خطأ في حساب المتتابعة"
        }
    }

    // ==========================================
    // 5. GEOMETRY STATE (الهندسة)
    // ==========================================
    enum class GeometryShape(val label: String) {
        CIRCLE("الدائرة"),
        TRIANGLE("المثلث"),
        RECTANGLE("المستطيل"),
        TRAPEZOID("شبه المنحرف"),
        ELLIPSE("القطع الناقص"),
        SPHERE("الكرة"),
        CYLINDER("الأسطوانة"),
        CONE("المخروط"),
        CUBOID("متوازي المستطيلات"),
        COORDINATES("الهندسة التحليلية")
    }

    val selectedShape = MutableStateFlow(GeometryShape.CIRCLE)
    val geoVal1 = MutableStateFlow("5")
    val geoVal2 = MutableStateFlow("4")
    val geoVal3 = MutableStateFlow("3")
    val geoVal4 = MutableStateFlow("0")

    private val _geoShapeResult = MutableStateFlow<GeometryEngine.ShapeResult?>(null)
    val geoShapeResult: StateFlow<GeometryEngine.ShapeResult?> = _geoShapeResult.asStateFlow()

    private val _geoCoordResult = MutableStateFlow<GeometryEngine.CoordinateResult?>(null)
    val geoCoordResult: StateFlow<GeometryEngine.CoordinateResult?> = _geoCoordResult.asStateFlow()

    fun selectGeometryShape(shape: GeometryShape) {
        selectedShape.value = shape
        _geoShapeResult.value = null
        _geoCoordResult.value = null
        when (shape) {
            GeometryShape.CIRCLE -> { geoVal1.value = "5" }
            GeometryShape.TRIANGLE -> { geoVal1.value = "3"; geoVal2.value = "4"; geoVal3.value = "5" }
            GeometryShape.RECTANGLE -> { geoVal1.value = "8"; geoVal2.value = "5" }
            GeometryShape.TRAPEZOID -> { geoVal1.value = "10"; geoVal2.value = "6"; geoVal3.value = "4" }
            GeometryShape.ELLIPSE -> { geoVal1.value = "6"; geoVal2.value = "4" }
            GeometryShape.SPHERE -> { geoVal1.value = "5" }
            GeometryShape.CYLINDER -> { geoVal1.value = "4"; geoVal2.value = "10" }
            GeometryShape.CONE -> { geoVal1.value = "3"; geoVal2.value = "7" }
            GeometryShape.CUBOID -> { geoVal1.value = "5"; geoVal2.value = "4"; geoVal3.value = "3" }
            GeometryShape.COORDINATES -> { geoVal1.value = "1"; geoVal2.value = "2"; geoVal3.value = "4"; geoVal4.value = "6" }
        }
        calculateGeometry()
    }

    fun calculateGeometry() {
        val v1 = geoVal1.value.toDoubleOrNull() ?: 1.0
        val v2 = geoVal2.value.toDoubleOrNull() ?: 1.0
        val v3 = geoVal3.value.toDoubleOrNull() ?: 1.0
        val v4 = geoVal4.value.toDoubleOrNull() ?: 1.0

        try {
            when (selectedShape.value) {
                GeometryShape.CIRCLE -> _geoShapeResult.value = geometryEngine.calculateCircle(v1)
                GeometryShape.TRIANGLE -> _geoShapeResult.value = geometryEngine.calculateTriangleSSS(v1, v2, v3)
                GeometryShape.RECTANGLE -> _geoShapeResult.value = geometryEngine.calculateRectangle(v1, v2)
                GeometryShape.TRAPEZOID -> _geoShapeResult.value = geometryEngine.calculateTrapezoid(v1, v2, v3)
                GeometryShape.ELLIPSE -> _geoShapeResult.value = geometryEngine.calculateEllipse(v1, v2)
                GeometryShape.SPHERE -> _geoShapeResult.value = geometryEngine.calculateSphere(v1)
                GeometryShape.CYLINDER -> _geoShapeResult.value = geometryEngine.calculateCylinder(v1, v2)
                GeometryShape.CONE -> _geoShapeResult.value = geometryEngine.calculateCone(v1, v2)
                GeometryShape.CUBOID -> _geoShapeResult.value = geometryEngine.calculateCuboid(v1, v2, v3)
                GeometryShape.COORDINATES -> _geoCoordResult.value = geometryEngine.calculateCoordinates(v1, v2, v3, v4)
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    // ==========================================
    // 6. GRAPHING STATE (الرسم البياني)
    // ==========================================
    val graphExpr = MutableStateFlow("sin(x) * x")
    val graphXMin = MutableStateFlow("-10")
    val graphXMax = MutableStateFlow("10")

    // ==========================================
    // 7. AI SOLVER STATE (المعلم الرياضي الذكي)
    // ==========================================
    val aiPrompt = MutableStateFlow("")
    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiResult = MutableStateFlow<GeminiMathService.AiSolveResult?>(null)
    val aiResult: StateFlow<GeminiMathService.AiSolveResult?> = _aiResult.asStateFlow()

    fun solveWithAi(customPrompt: String? = null) {
        val prompt = customPrompt ?: aiPrompt.value.trim()
        if (prompt.isEmpty()) return
        aiPrompt.value = prompt

        viewModelScope.launch {
            _isAiLoading.value = true
            val result = aiService.solveMathProblem(prompt)
            _aiResult.value = result
            _isAiLoading.value = false

            if (result.isSuccess) {
                saveHistory("حل بالذكاء الاصطناعي", prompt, result.finalAnswer ?: "تم الحل بنجاح", result.solution)
            }
        }
    }

    // ==========================================
    // HISTORY ACTIONS
    // ==========================================
    private fun saveHistory(category: String, expression: String, result: String, steps: String) {
        viewModelScope.launch {
            try {
                calculationDao.insert(
                    CalculationEntity(
                        category = category,
                        expression = expression,
                        result = result,
                        stepDetails = steps
                    )
                )
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            calculationDao.clearHistory()
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            calculationDao.deleteById(id)
        }
    }

    fun loadFromHistory(item: CalculationEntity) {
        _sciExpression.value = item.expression
        _cursorPosition.value = item.expression.length
        _currentTab.value = CalculatorTab.SCIENTIFIC
    }

    private fun formatNumber(v: Double): String {
        if (v.isNaN()) return "NaN"
        if (v.isInfinite()) return if (v > 0) "∞" else "-∞"
        if (abs(v - Math.round(v)) < 1e-9) {
            return Math.round(v).toLong().toString()
        }
        return String.format(java.util.Locale.US, "%.6f", v).trimEnd('0').trimEnd('.')
    }
}
