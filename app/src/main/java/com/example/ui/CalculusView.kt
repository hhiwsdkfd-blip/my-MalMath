package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CalculusView(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val mode by viewModel.calculusMode.collectAsState()
    val error by viewModel.calculusError.collectAsState()

    val derivExpr by viewModel.derivExpr.collectAsState()
    val derivPoint by viewModel.derivPoint.collectAsState()
    val derivOrder by viewModel.derivOrder.collectAsState()
    val derivResult by viewModel.derivResult.collectAsState()

    val partialExpr by viewModel.partialExpr.collectAsState()
    val partialX0 by viewModel.partialX0.collectAsState()
    val partialY0 by viewModel.partialY0.collectAsState()
    val partialResult by viewModel.partialResult.collectAsState()

    val integExpr by viewModel.integExpr.collectAsState()
    val integA by viewModel.integA.collectAsState()
    val integB by viewModel.integB.collectAsState()
    val integResult by viewModel.integResult.collectAsState()

    val dIntegExpr by viewModel.dIntegExpr.collectAsState()
    val dIntegAx by viewModel.dIntegAx.collectAsState()
    val dIntegBx by viewModel.dIntegBx.collectAsState()
    val dIntegAy by viewModel.dIntegAy.collectAsState()
    val dIntegBy by viewModel.dIntegBy.collectAsState()
    val dIntegResult by viewModel.dIntegResult.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = mode.ordinal,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MathCyan,
                modifier = Modifier.clip(RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = mode == CalculatorViewModel.CalculusMode.DERIVATIVE,
                    onClick = { viewModel.setCalculusMode(CalculatorViewModel.CalculusMode.DERIVATIVE) },
                    text = { Text("المشتقة العادية") }
                )
                Tab(
                    selected = mode == CalculatorViewModel.CalculusMode.PARTIAL_DERIVATIVE,
                    onClick = { viewModel.setCalculusMode(CalculatorViewModel.CalculusMode.PARTIAL_DERIVATIVE) },
                    text = { Text("المشتقة الجزئية") }
                )
                Tab(
                    selected = mode == CalculatorViewModel.CalculusMode.INTEGRAL,
                    onClick = { viewModel.setCalculusMode(CalculatorViewModel.CalculusMode.INTEGRAL) },
                    text = { Text("التكامل المحدود") }
                )
                Tab(
                    selected = mode == CalculatorViewModel.CalculusMode.DOUBLE_INTEGRAL,
                    onClick = { viewModel.setCalculusMode(CalculatorViewModel.CalculusMode.DOUBLE_INTEGRAL) },
                    text = { Text("التكامل الثنائي") }
                )
            }
        }

        // --- SUBMODE 1: DERIVATIVE ---
        if (mode == CalculatorViewModel.CalculusMode.DERIVATIVE) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "حساب الاشتقاق (المشتقة الأولى، الثانية، الثالثة)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MathCyan
                        )

                        OutlinedTextField(
                            value = derivExpr,
                            onValueChange = { viewModel.derivExpr.value = it },
                            label = { Text("الدالة f(x)") },
                            placeholder = { Text("مثال: x^3 - 3*x + 2 أو sin(x)*exp(x)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
                        )

                        // Quick symbol chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("x", "x^2", "sin(x)", "cos(x)", "ln(x)", "exp(x)", "sqrt(x)").forEach { sym ->
                                SuggestionChip(
                                    onClick = { viewModel.derivExpr.value += sym },
                                    label = { Text(sym, fontSize = 11.sp) }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = derivPoint,
                                onValueChange = { viewModel.derivPoint.value = it },
                                label = { Text("النقطة x₀") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            // Order Selector
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(1 to "f'", 2 to "f''", 3 to "f'''").forEach { (ord, label) ->
                                    FilterChip(
                                        selected = derivOrder == ord,
                                        onClick = { viewModel.derivOrder.value = ord },
                                        label = { Text(label, fontWeight = FontWeight.Bold) }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.solveDerivative() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("solve_derivative_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MathOperatorKey)
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("احسب المشتقة وخطوات الحل", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Derivative Results Card
            if (derivResult != null) {
                item {
                    val res = derivResult!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MathSurfaceElevatedDark)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("النتيجة الرياضية:", color = MathGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                            Text(
                                text = "f${"'" .repeat(derivOrder)}(${derivPoint}) = ${res.value}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MathCyan,
                                fontFamily = FontFamily.Monospace
                            )

                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                            Text("معادلة مماس المنحنى (Tangent Line):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(res.tangentLine, color = MathEmerald, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)

                            Text("معادلة الناظم العمودي (Normal Line):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(res.normalLine, color = MathPurple, fontFamily = FontFamily.Monospace)

                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                            Text("خطوات الحل والتعليل الرياضي:", fontWeight = FontWeight.Bold, color = MathGold)
                            res.steps.forEach { step ->
                                Text("• $step", fontSize = 13.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- SUBMODE 2: PARTIAL DERIVATIVE ---
        if (mode == CalculatorViewModel.CalculusMode.PARTIAL_DERIVATIVE) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "المشتقات الجزئية والانحدار f(x, y)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MathCyan
                        )

                        OutlinedTextField(
                            value = partialExpr,
                            onValueChange = { viewModel.partialExpr.value = it },
                            label = { Text("الدالة f(x, y)") },
                            placeholder = { Text("مثال: x^2 * y + sin(x*y)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = partialX0,
                                onValueChange = { viewModel.partialX0.value = it },
                                label = { Text("قيمة x₀") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = partialY0,
                                onValueChange = { viewModel.partialY0.value = it },
                                label = { Text("قيمة y₀") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Button(
                            onClick = { viewModel.solvePartialDerivative() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MathOperatorKey)
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("احسب المشتقات الجزئية والتدرج", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (partialResult != null) {
                item {
                    val p = partialResult!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MathSurfaceElevatedDark)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("المشتقات الجزئية المحسوبة:", color = MathGold, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("∂f/∂x = ${p.df_dx}", color = MathCyan, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text("∂f/∂y = ${p.df_dy}", color = MathEmerald, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Text("مقدار متجه الانحدار |∇f| = ${p.gradientMagnitude}", color = MathGold, fontWeight = FontWeight.Bold)

                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            Text("خطوات الحل والتعليل:", fontWeight = FontWeight.Bold, color = MathGold)
                            p.steps.forEach { step ->
                                Text("• $step", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- SUBMODE 3: INTEGRAL ---
        if (mode == CalculatorViewModel.CalculusMode.INTEGRAL) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "حساب التكامل المحدود وطول القوس والحجم الدوراني",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MathCyan
                        )

                        OutlinedTextField(
                            value = integExpr,
                            onValueChange = { viewModel.integExpr.value = it },
                            label = { Text("دالة التكامل f(x)") },
                            placeholder = { Text("مثال: sin(x) + x^2 أو 1/(1+x^2)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = integA,
                                onValueChange = { viewModel.integA.value = it },
                                label = { Text("الحد الأدنى (a)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = integB,
                                onValueChange = { viewModel.integB.value = it },
                                label = { Text("الحد الأعلى (b)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Button(
                            onClick = { viewModel.solveIntegral() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("solve_integral_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MathEqualKey)
                        ) {
                            Icon(Icons.Default.Functions, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("احسب التكامل والخواص الهندسية", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (integResult != null) {
                item {
                    val res = integResult!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MathSurfaceElevatedDark)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("نتيجة التكامل المحدود:", color = MathGold, fontWeight = FontWeight.Bold)
                            Text(
                                text = "∫ f(x) dx = ${res.value}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MathCyan,
                                fontFamily = FontFamily.Monospace
                            )

                            if (res.arcLength != null) {
                                Text("طول قوس المنحنى (Arc Length): ${res.arcLength}", color = MathEmerald, fontWeight = FontWeight.SemiBold)
                            }
                            if (res.volumeOfRevolution != null) {
                                Text("حجم المجسم الدوراني حول محور السينات: ${res.volumeOfRevolution}", color = MathPurple, fontWeight = FontWeight.SemiBold)
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            Text("خطوات وطرق الحساب:", fontWeight = FontWeight.Bold, color = MathGold)
                            res.steps.forEach { step ->
                                Text("• $step", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- SUBMODE 4: DOUBLE INTEGRAL ---
        if (mode == CalculatorViewModel.CalculusMode.DOUBLE_INTEGRAL) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "التكامل الثنائي (المزدوج) ∬ f(x, y) dx dy",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MathCyan
                        )

                        OutlinedTextField(
                            value = dIntegExpr,
                            onValueChange = { viewModel.dIntegExpr.value = it },
                            label = { Text("الدالة f(x, y)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = dIntegAx,
                                onValueChange = { viewModel.dIntegAx.value = it },
                                label = { Text("x أدنى") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = dIntegBx,
                                onValueChange = { viewModel.dIntegBx.value = it },
                                label = { Text("x أعلى") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = dIntegAy,
                                onValueChange = { viewModel.dIntegAy.value = it },
                                label = { Text("y أدنى") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = dIntegBy,
                                onValueChange = { viewModel.dIntegBy.value = it },
                                label = { Text("y أعلى") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Button(
                            onClick = { viewModel.solveDoubleIntegral() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MathEqualKey)
                        ) {
                            Text("احسب التكامل الثنائي", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (dIntegResult != null) {
                item {
                    val res = dIntegResult!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MathSurfaceElevatedDark)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("الحجم والمساحة تحت السطح ∬:", color = MathGold, fontWeight = FontWeight.Bold)
                            Text("V = ${res.value}", fontSize = 24.sp, color = MathCyan, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            res.steps.forEach { Text("• $it", fontSize = 13.sp) }
                        }
                    }
                }
            }
        }

        // Error Banner
        if (error != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MathRose.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "خطأ: $error",
                        color = MathRose,
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
