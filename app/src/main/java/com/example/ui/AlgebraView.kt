package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
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
fun AlgebraView(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val mode by viewModel.algebraMode.collectAsState()
    val error by viewModel.algebraError.collectAsState()

    val quadA by viewModel.quadA.collectAsState()
    val quadB by viewModel.quadB.collectAsState()
    val quadC by viewModel.quadC.collectAsState()
    val quadResult by viewModel.quadResult.collectAsState()

    val sysA1 by viewModel.sysA1.collectAsState()
    val sysB1 by viewModel.sysB1.collectAsState()
    val sysC1 by viewModel.sysC1.collectAsState()
    val sysA2 by viewModel.sysA2.collectAsState()
    val sysB2 by viewModel.sysB2.collectAsState()
    val sysC2 by viewModel.sysC2.collectAsState()
    val sys2Result by viewModel.sys2Result.collectAsState()

    val matA by viewModel.matA.collectAsState()
    val matB by viewModel.matB.collectAsState()
    val matC by viewModel.matC.collectAsState()
    val matD by viewModel.matD.collectAsState()
    val matrixResult by viewModel.matrixResult.collectAsState()

    val seqA1 by viewModel.seqA1.collectAsState()
    val seqDiffRatio by viewModel.seqDiffRatio.collectAsState()
    val seqN by viewModel.seqN.collectAsState()
    val isGeometric by viewModel.isGeometric.collectAsState()
    val seqResult by viewModel.seqResult.collectAsState()

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
                    selected = mode == CalculatorViewModel.AlgebraMode.QUADRATIC,
                    onClick = { viewModel.setAlgebraMode(CalculatorViewModel.AlgebraMode.QUADRATIC) },
                    text = { Text("معادلة تربيعية") }
                )
                Tab(
                    selected = mode == CalculatorViewModel.AlgebraMode.LINEAR_2X2,
                    onClick = { viewModel.setAlgebraMode(CalculatorViewModel.AlgebraMode.LINEAR_2X2) },
                    text = { Text("نظام معادلات 2x2") }
                )
                Tab(
                    selected = mode == CalculatorViewModel.AlgebraMode.MATRIX_2X2,
                    onClick = { viewModel.setAlgebraMode(CalculatorViewModel.AlgebraMode.MATRIX_2X2) },
                    text = { Text("المصفوفات") }
                )
                Tab(
                    selected = mode == CalculatorViewModel.AlgebraMode.SEQUENCES,
                    onClick = { viewModel.setAlgebraMode(CalculatorViewModel.AlgebraMode.SEQUENCES) },
                    text = { Text("المتتابعات") }
                )
            }
        }

        // --- SUBMODE 1: QUADRATIC EQUATION ---
        if (mode == CalculatorViewModel.AlgebraMode.QUADRATIC) {
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
                            text = "حل المعادلة التربيعية: ax² + bx + c = 0",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MathCyan
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = quadA,
                                onValueChange = { viewModel.quadA.value = it },
                                label = { Text("المعامل a") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = quadB,
                                onValueChange = { viewModel.quadB.value = it },
                                label = { Text("المعامل b") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = quadC,
                                onValueChange = { viewModel.quadC.value = it },
                                label = { Text("الحد c") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Button(
                            onClick = { viewModel.solveQuadratic() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("solve_quadratic_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MathOperatorKey)
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("حل المعادلة واستخراج الخصائص", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (quadResult != null) {
                item {
                    val r = quadResult!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MathSurfaceElevatedDark)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("نتائج حل المعادلة:", color = MathGold, fontWeight = FontWeight.Bold)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("الجذر الأول x₁ = ${r.root1}", color = MathCyan, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text("الجذر الثاني x₂ = ${r.root2}", color = MathEmerald, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }

                            Text("المميز (Δ = b² - 4ac) = ${r.discriminant}", color = MathGold, fontWeight = FontWeight.SemiBold)
                            Text("رأس القطع المكافئ (Vertex): (${r.vertexX}, ${r.vertexY})", color = MathPurple)
                            Text("معادلة محور التماثل: ${r.axisOfSymmetry}")

                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            Text("خطوات الحل بالقانون العام (المميز):", fontWeight = FontWeight.Bold, color = MathGold)
                            r.steps.forEach { step ->
                                Text("• $step", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- SUBMODE 2: 2X2 LINEAR SYSTEM ---
        if (mode == CalculatorViewModel.AlgebraMode.LINEAR_2X2) {
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
                            text = "حل نظام معادلتين بمتغيرين (بطريقة كرامر):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MathCyan
                        )

                        Text("المعادلة الأولى: a₁ x + b₁ y = c₁", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = sysA1, onValueChange = { viewModel.sysA1.value = it }, label = { Text("a₁") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = sysB1, onValueChange = { viewModel.sysB1.value = it }, label = { Text("b₁") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = sysC1, onValueChange = { viewModel.sysC1.value = it }, label = { Text("c₁") }, modifier = Modifier.weight(1f))
                        }

                        Text("المعادلة الثانية: a₂ x + b₂ y = c₂", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = sysA2, onValueChange = { viewModel.sysA2.value = it }, label = { Text("a₂") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = sysB2, onValueChange = { viewModel.sysB2.value = it }, label = { Text("b₂") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = sysC2, onValueChange = { viewModel.sysC2.value = it }, label = { Text("c₂") }, modifier = Modifier.weight(1f))
                        }

                        Button(
                            onClick = { viewModel.solve2x2System() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MathOperatorKey)
                        ) {
                            Text("حل النظام بطريقة كرامر", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (sys2Result != null) {
                item {
                    val s = sys2Result!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MathSurfaceElevatedDark)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("الحل:", color = MathGold, fontWeight = FontWeight.Bold)
                            if (s.x != null && s.y != null) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("x = ${s.x}", fontSize = 20.sp, color = MathCyan, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    Text("y = ${s.y}", fontSize = 20.sp, color = MathEmerald, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            } else {
                                Text(s.statusMessage, color = MathRose, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            s.steps.forEach { Text("• $it", fontSize = 13.sp) }
                        }
                    }
                }
            }
        }

        // --- SUBMODE 3: MATRICES ---
        if (mode == CalculatorViewModel.AlgebraMode.MATRIX_2X2) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "تحليل مصفوفة 2×2 (المحدد، المعكوس، الأثر، المنقول)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MathCyan
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = matA, onValueChange = { viewModel.matA.value = it }, label = { Text("الصف 1، عمود 1") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = matB, onValueChange = { viewModel.matB.value = it }, label = { Text("الصف 1، عمود 2") }, modifier = Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = matC, onValueChange = { viewModel.matC.value = it }, label = { Text("الصف 2، عمود 1") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = matD, onValueChange = { viewModel.matD.value = it }, label = { Text("الصف 2، عمود 2") }, modifier = Modifier.weight(1f))
                        }

                        Button(
                            onClick = { viewModel.solveMatrix2x2() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MathOperatorKey)
                        ) {
                            Text("حساب خصائص المصفوفة", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (matrixResult != null) {
                item {
                    val m = matrixResult!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MathSurfaceElevatedDark)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("خصائص المصفوفة:", color = MathGold, fontWeight = FontWeight.Bold)
                            Text("المحدد det(A) = ${m.determinant}", color = MathCyan, fontWeight = FontWeight.Bold)
                            Text("الأثر Trace(A) = ${m.trace}", color = MathEmerald)
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            m.steps.forEach { Text("• $it", fontSize = 13.sp) }
                        }
                    }
                }
            }
        }

        // --- SUBMODE 4: SEQUENCES ---
        if (mode == CalculatorViewModel.AlgebraMode.SEQUENCES) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("المتتابعات والمتسلسلات (حسابية وهندسية)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MathCyan)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = !isGeometric,
                                onClick = { viewModel.isGeometric.value = false },
                                label = { Text("متتابعة حسابية") }
                            )
                            FilterChip(
                                selected = isGeometric,
                                onClick = { viewModel.isGeometric.value = true },
                                label = { Text("متتابعة هندسية") }
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = seqA1, onValueChange = { viewModel.seqA1.value = it }, label = { Text("الحد الأول a₁") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(
                                value = seqDiffRatio,
                                onValueChange = { viewModel.seqDiffRatio.value = it },
                                label = { Text(if (isGeometric) "الأساس r" else "الأساس d") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(value = seqN, onValueChange = { viewModel.seqN.value = it }, label = { Text("رقم الحد n") }, modifier = Modifier.weight(1f))
                        }

                        Button(
                            onClick = { viewModel.solveSequence() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MathOperatorKey)
                        ) {
                            Text("احسب الحد النوني والمجموع", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (seqResult != null) {
                item {
                    val s = seqResult!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MathSurfaceElevatedDark)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(s.sequenceType, color = MathGold, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("الحد aₙ = ${s.nthTerm}", color = MathCyan, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text("مجموع Sₙ = ${s.sumN}", color = MathEmerald, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            if (s.infiniteSum != null) {
                                Text("المجموع إلى اللانهاية S_∞ = ${s.infiniteSum}", color = MathPurple, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            s.steps.forEach { Text("• $it", fontSize = 13.sp) }
                        }
                    }
                }
            }
        }

        if (error != null) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MathRose.copy(alpha = 0.2f))) {
                    Text(text = "خطأ: $error", color = MathRose, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
