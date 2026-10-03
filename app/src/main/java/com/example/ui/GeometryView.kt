package com.example.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun GeometryView(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val selectedShape by viewModel.selectedShape.collectAsState()
    val val1 by viewModel.geoVal1.collectAsState()
    val val2 by viewModel.geoVal2.collectAsState()
    val val3 by viewModel.geoVal3.collectAsState()
    val val4 by viewModel.geoVal4.collectAsState()

    val shapeResult by viewModel.geoShapeResult.collectAsState()
    val coordResult by viewModel.geoCoordResult.collectAsState()

    val chipScrollState = rememberScrollState()

    // Trigger initial calculation
    LaunchedEffect(selectedShape) {
        viewModel.calculateGeometry()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Horizontal Shape Picker
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(chipScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorViewModel.GeometryShape.values().forEach { shape ->
                    FilterChip(
                        selected = selectedShape == shape,
                        onClick = { viewModel.selectGeometryShape(shape) },
                        label = { Text(shape.label, fontWeight = if (selectedShape == shape) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MathCyanContainer,
                            selectedLabelColor = MathCyan
                        )
                    )
                }
            }
        }

        // Inputs Card
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
                        text = "بيانات ${selectedShape.label}:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MathCyan
                    )

                    when (selectedShape) {
                        CalculatorViewModel.GeometryShape.CIRCLE -> {
                            OutlinedTextField(
                                value = val1,
                                onValueChange = { viewModel.geoVal1.value = it; viewModel.calculateGeometry() },
                                label = { Text("نصف القطر (r)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        CalculatorViewModel.GeometryShape.TRIANGLE -> {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = val1, onValueChange = { viewModel.geoVal1.value = it; viewModel.calculateGeometry() }, label = { Text("الضلع a") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = val2, onValueChange = { viewModel.geoVal2.value = it; viewModel.calculateGeometry() }, label = { Text("الضلع b") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = val3, onValueChange = { viewModel.geoVal3.value = it; viewModel.calculateGeometry() }, label = { Text("الضلع c") }, modifier = Modifier.weight(1f))
                            }
                        }
                        CalculatorViewModel.GeometryShape.RECTANGLE -> {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = val1, onValueChange = { viewModel.geoVal1.value = it; viewModel.calculateGeometry() }, label = { Text("الطول (L)") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = val2, onValueChange = { viewModel.geoVal2.value = it; viewModel.calculateGeometry() }, label = { Text("العرض (W)") }, modifier = Modifier.weight(1f))
                            }
                        }
                        CalculatorViewModel.GeometryShape.TRAPEZOID -> {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = val1, onValueChange = { viewModel.geoVal1.value = it; viewModel.calculateGeometry() }, label = { Text("القاعدة a") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = val2, onValueChange = { viewModel.geoVal2.value = it; viewModel.calculateGeometry() }, label = { Text("القاعدة b") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = val3, onValueChange = { viewModel.geoVal3.value = it; viewModel.calculateGeometry() }, label = { Text("الارتفاع h") }, modifier = Modifier.weight(1f))
                            }
                        }
                        CalculatorViewModel.GeometryShape.ELLIPSE -> {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = val1, onValueChange = { viewModel.geoVal1.value = it; viewModel.calculateGeometry() }, label = { Text("المحور الأكبر a") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = val2, onValueChange = { viewModel.geoVal2.value = it; viewModel.calculateGeometry() }, label = { Text("المحور الأصغر b") }, modifier = Modifier.weight(1f))
                            }
                        }
                        CalculatorViewModel.GeometryShape.SPHERE -> {
                            OutlinedTextField(
                                value = val1,
                                onValueChange = { viewModel.geoVal1.value = it; viewModel.calculateGeometry() },
                                label = { Text("نصف القطر (r)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        CalculatorViewModel.GeometryShape.CYLINDER -> {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = val1, onValueChange = { viewModel.geoVal1.value = it; viewModel.calculateGeometry() }, label = { Text("نصف القطر (r)") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = val2, onValueChange = { viewModel.geoVal2.value = it; viewModel.calculateGeometry() }, label = { Text("الارتفاع (h)") }, modifier = Modifier.weight(1f))
                            }
                        }
                        CalculatorViewModel.GeometryShape.CONE -> {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = val1, onValueChange = { viewModel.geoVal1.value = it; viewModel.calculateGeometry() }, label = { Text("نصف القطر (r)") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = val2, onValueChange = { viewModel.geoVal2.value = it; viewModel.calculateGeometry() }, label = { Text("الارتفاع (h)") }, modifier = Modifier.weight(1f))
                            }
                        }
                        CalculatorViewModel.GeometryShape.CUBOID -> {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = val1, onValueChange = { viewModel.geoVal1.value = it; viewModel.calculateGeometry() }, label = { Text("الطول L") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = val2, onValueChange = { viewModel.geoVal2.value = it; viewModel.calculateGeometry() }, label = { Text("العرض W") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = val3, onValueChange = { viewModel.geoVal3.value = it; viewModel.calculateGeometry() }, label = { Text("الارتفاع H") }, modifier = Modifier.weight(1f))
                            }
                        }
                        CalculatorViewModel.GeometryShape.COORDINATES -> {
                            Text("النقطة الأولى A(x₁, y₁):", fontSize = 12.sp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = val1, onValueChange = { viewModel.geoVal1.value = it; viewModel.calculateGeometry() }, label = { Text("x₁") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = val2, onValueChange = { viewModel.geoVal2.value = it; viewModel.calculateGeometry() }, label = { Text("y₁") }, modifier = Modifier.weight(1f))
                            }
                            Text("النقطة الثانية B(x₂, y₂):", fontSize = 12.sp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = val3, onValueChange = { viewModel.geoVal3.value = it; viewModel.calculateGeometry() }, label = { Text("x₂") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = val4, onValueChange = { viewModel.geoVal4.value = it; viewModel.calculateGeometry() }, label = { Text("y₂") }, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.calculateGeometry() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("calculate_geometry_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MathOperatorKey)
                    ) {
                        Icon(Icons.Default.SquareFoot, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("تحديث الحسابات الهندسية", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Shape Result Card
        if (shapeResult != null) {
            item {
                val s = shapeResult!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MathSurfaceElevatedDark)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(s.shapeName, color = MathGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(s.primaryMetricName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = String.format(java.util.Locale.US, "%.4f", s.primaryMetricValue),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MathCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Column {
                                Text(s.secondaryMetricName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = String.format(java.util.Locale.US, "%.4f", s.secondaryMetricValue),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MathEmerald,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                        Text("الخواص والأبعاد التفصيلية:", fontWeight = FontWeight.Bold, color = MathGold)
                        s.details.forEach { (k, v) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(k, fontSize = 13.sp)
                                Text(v, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        Text("القوانين والصيغ الرياضية المطبقة:", fontWeight = FontWeight.Bold, color = MathGold)
                        s.formulas.forEach { f ->
                            Text("• $f", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Coordinate Result Card
        if (coordResult != null) {
            item {
                val c = coordResult!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MathSurfaceElevatedDark)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("نتائج الهندسة التحليلية:", color = MathGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("المسافة: d = ${String.format(java.util.Locale.US, "%.4f", c.distance)}", color = MathCyan, fontWeight = FontWeight.Bold)
                            Text("المنتصف: (${c.midpointX}, ${c.midpointY})", color = MathEmerald, fontWeight = FontWeight.Bold)
                        }

                        Text("الميل: ${c.slope?.let { String.format(java.util.Locale.US, "%.4f", it) } ?: "غير معرف"}")
                        Text("معادلة المستقيم: ${c.lineEquation}", color = MathPurple, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Text("الزاوية مع الأفق: ${String.format(java.util.Locale.US, "%.2f", c.angleDeg)}°")

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        c.steps.forEach { Text("• $it", fontSize = 13.sp) }
                    }
                }
            }
        }
    }
}
