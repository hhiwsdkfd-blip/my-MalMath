package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun GraphingView(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val expr by viewModel.graphExpr.collectAsState()
    val xMinStr by viewModel.graphXMin.collectAsState()
    val xMaxStr by viewModel.graphXMax.collectAsState()

    var xMin by remember { mutableStateOf(-10.0) }
    var xMax by remember { mutableStateOf(10.0) }

    LaunchedEffect(xMinStr, xMaxStr) {
        xMin = xMinStr.toDoubleOrNull() ?: -10.0
        xMax = xMaxStr.toDoubleOrNull() ?: 10.0
        if (xMin >= xMax) xMax = xMin + 1.0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "رسم منحنى الدالة f(x) بيانيّاً:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MathCyan
                )

                OutlinedTextField(
                    value = expr,
                    onValueChange = { viewModel.graphExpr.value = it },
                    label = { Text("معادلة الدالة y = f(x)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
                )

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("sin(x)", "x^2 - 4", "cos(2*x)", "x^3 - 3*x", "exp(-x^2)", "ln(x)").forEach { preset ->
                        SuggestionChip(
                            onClick = { viewModel.graphExpr.value = preset },
                            label = { Text(preset, fontSize = 11.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = xMinStr,
                        onValueChange = { viewModel.graphXMin.value = it },
                        label = { Text("x الأدنى") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = xMaxStr,
                        onValueChange = { viewModel.graphXMax.value = it },
                        label = { Text("x الأقصى") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        }

        // --- GRAPH CANVAS ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MathNavyDark)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    if (w <= 0 || h <= 0) return@Canvas

                    // Sampling function points
                    val sampleCount = 300
                    val dx = (xMax - xMin) / sampleCount
                    val points = mutableListOf<Pair<Double, Double>>()
                    var yMinFound = Double.POSITIVE_INFINITY
                    var yMaxFound = Double.NEGATIVE_INFINITY

                    for (i in 0..sampleCount) {
                        val xVal = xMin + i * dx
                        try {
                            val yVal = viewModel.evaluator.evaluateFunction(expr, xVal)
                            if (!yVal.isNaN() && !yVal.isInfinite() && abs(yVal) < 1e5) {
                                points.add(xVal to yVal)
                                if (yVal < yMinFound) yMinFound = yVal
                                if (yVal > yMaxFound) yMaxFound = yVal
                            }
                        } catch (e: Exception) {
                            // ignore point
                        }
                    }

                    var yMin = if (yMinFound.isFinite()) yMinFound else -5.0
                    var yMax = if (yMaxFound.isFinite()) yMaxFound else 5.0

                    // Keep zero in range or give margin
                    if (yMin > 0) yMin = 0.0
                    if (yMax < 0) yMax = 0.0
                    if (yMax - yMin < 1e-4) {
                        yMin -= 2.0
                        yMax += 2.0
                    } else {
                        val margin = (yMax - yMin) * 0.1
                        yMin -= margin
                        yMax += margin
                    }

                    fun toScreenX(x: Double): Float = ((x - xMin) / (xMax - xMin) * w).toFloat()
                    fun toScreenY(y: Double): Float = (h - ((y - yMin) / (yMax - yMin) * h)).toFloat()

                    // Draw Grid
                    val gridColor = Color.White.copy(alpha = 0.06f)
                    val axisColor = Color.White.copy(alpha = 0.35f)

                    // Vertical grid lines
                    val xStep = max(1.0, ((xMax - xMin) / 10.0))
                    var currX = (xMin / xStep).toInt() * xStep
                    while (currX <= xMax) {
                        val sx = toScreenX(currX)
                        drawLine(gridColor, Offset(sx, 0f), Offset(sx, h), strokeWidth = 1f)
                        currX += xStep
                    }

                    // Horizontal grid lines
                    val yStep = max(1.0, ((yMax - yMin) / 8.0))
                    var currY = (yMin / yStep).toInt() * yStep
                    while (currY <= yMax) {
                        val sy = toScreenY(currY)
                        drawLine(gridColor, Offset(0f, sy), Offset(w, sy), strokeWidth = 1f)
                        currY += yStep
                    }

                    // X-Axis (y = 0)
                    if (yMin <= 0 && yMax >= 0) {
                        val zeroY = toScreenY(0.0)
                        drawLine(axisColor, Offset(0f, zeroY), Offset(w, zeroY), strokeWidth = 2f)
                    }

                    // Y-Axis (x = 0)
                    if (xMin <= 0 && xMax >= 0) {
                        val zeroX = toScreenX(0.0)
                        drawLine(axisColor, Offset(zeroX, 0f), Offset(zeroX, h), strokeWidth = 2f)
                    }

                    // Draw Curve Path
                    if (points.isNotEmpty()) {
                        val curvePath = Path()
                        var first = true

                        for (i in points.indices) {
                            val (xVal, yVal) = points[i]
                            val sx = toScreenX(xVal)
                            val sy = toScreenY(yVal)

                            if (first) {
                                curvePath.moveTo(sx, sy)
                                first = false
                            } else {
                                val prevY = points[i - 1].second
                                if (abs(yVal - prevY) > (yMax - yMin) * 0.8) {
                                    // Jump discontinuity (e.g. tan(x))
                                    curvePath.moveTo(sx, sy)
                                } else {
                                    curvePath.lineTo(sx, sy)
                                }
                            }
                        }

                        drawPath(
                            path = curvePath,
                            color = MathCyan,
                            style = Stroke(width = 3.5f)
                        )
                    }
                }
            }
        }
    }
}
