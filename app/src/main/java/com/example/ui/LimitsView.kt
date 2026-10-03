package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
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
import com.example.math.CalculusEngine
import com.example.ui.theme.*

@Composable
fun LimitsView(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val limitExpr by viewModel.limitExpr.collectAsState()
    val limitC by viewModel.limitC.collectAsState()
    val direction by viewModel.limitDirection.collectAsState()
    val result by viewModel.limitResult.collectAsState()
    val error by viewModel.limitError.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        text = "حساب النهايات (النهايات العادية، الأحادية، وعند اللانهاية وحالات عدم التعيين)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MathCyan
                    )

                    OutlinedTextField(
                        value = limitExpr,
                        onValueChange = { viewModel.limitExpr.value = it },
                        label = { Text("الدالة f(x)") },
                        placeholder = { Text("مثال: (sin(x))/x أو (x^2 - 4)/(x - 2)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
                    )

                    // Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "(sin(x))/x" to "0",
                            "(x^2-1)/(x-1)" to "1",
                            "(1+1/x)^x" to "+∞",
                            "(exp(x)-1)/x" to "0"
                        ).forEach { (expr, cVal) ->
                            SuggestionChip(
                                onClick = {
                                    viewModel.limitExpr.value = expr
                                    if (cVal == "+∞") {
                                        viewModel.limitDirection.value = CalculusEngine.LimitDirection.POSITIVE_INFINITY
                                    } else {
                                        viewModel.limitC.value = cVal
                                        viewModel.limitDirection.value = CalculusEngine.LimitDirection.TWO_SIDED
                                    }
                                },
                                label = { Text(expr, fontSize = 10.sp) }
                            )
                        }
                    }

                    // Direction Selector Chips
                    Text("اتجاه النهاية (نوع النهاية):", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = direction == CalculusEngine.LimitDirection.TWO_SIDED,
                            onClick = { viewModel.limitDirection.value = CalculusEngine.LimitDirection.TWO_SIDED },
                            label = { Text("x ⟶ c") }
                        )
                        FilterChip(
                            selected = direction == CalculusEngine.LimitDirection.RIGHT,
                            onClick = { viewModel.limitDirection.value = CalculusEngine.LimitDirection.RIGHT },
                            label = { Text("x ⟶ c⁺") }
                        )
                        FilterChip(
                            selected = direction == CalculusEngine.LimitDirection.LEFT,
                            onClick = { viewModel.limitDirection.value = CalculusEngine.LimitDirection.LEFT },
                            label = { Text("x ⟶ c⁻") }
                        )
                        FilterChip(
                            selected = direction == CalculusEngine.LimitDirection.POSITIVE_INFINITY,
                            onClick = { viewModel.limitDirection.value = CalculusEngine.LimitDirection.POSITIVE_INFINITY },
                            label = { Text("x ⟶ +∞") }
                        )
                        FilterChip(
                            selected = direction == CalculusEngine.LimitDirection.NEGATIVE_INFINITY,
                            onClick = { viewModel.limitDirection.value = CalculusEngine.LimitDirection.NEGATIVE_INFINITY },
                            label = { Text("x ⟶ -∞") }
                        )
                    }

                    // Point C input (only if not infinity)
                    if (direction != CalculusEngine.LimitDirection.POSITIVE_INFINITY &&
                        direction != CalculusEngine.LimitDirection.NEGATIVE_INFINITY) {
                        OutlinedTextField(
                            value = limitC,
                            onValueChange = { viewModel.limitC.value = it },
                            label = { Text("النقطة c المراد الاقتراب منها") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = { viewModel.solveLimit() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("solve_limit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MathOperatorKey)
                    ) {
                        Icon(Icons.Default.Analytics, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("احسب النهاية وحلل سلوك الدالة", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Limit Result Display
        if (result != null) {
            item {
                val res = result!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MathSurfaceElevatedDark)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(res.limitType, color = MathGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                        if (res.isIndeterminate) {
                            Surface(
                                color = MathRose.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "حالة عدم تعيين: ${res.indeterminateForm ?: "0/0"}",
                                    color = MathRose,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Text(
                            text = if (res.limitValue != null) "lim = ${res.limitValue}" else "النهاية غير موجودة (DNE)",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (res.limitValue != null) MathCyan else MathRose,
                            fontFamily = FontFamily.Monospace
                        )

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                        Text("الخطوات والتعليل الرياضي:", fontWeight = FontWeight.Bold, color = MathGold)
                        res.steps.forEach { step ->
                            Text("• $step", fontSize = 13.sp, lineHeight = 18.sp)
                        }

                        // Convergence Table
                        if (res.convergenceTable.isNotEmpty()) {
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            Text("جدول التقارب العددي (Numerical Approach):", fontWeight = FontWeight.Bold, color = MathEmerald)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("قيمة x المقتربة", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MathCyan)
                                    Text("قيمة f(x)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MathGold)
                                }
                                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                                res.convergenceTable.take(8).forEach { row ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${row.xValue}", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                        Text("${row.fxValue}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

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
