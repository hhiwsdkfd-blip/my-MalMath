package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.UnitConverter
import com.example.math.UnitItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterSheet(
    onDismiss: () -> Unit,
    onInsertResult: (String) -> Unit
) {
    val categories = UnitConverter.categories
    var selectedCatIndex by remember { mutableStateOf(0) }
    val currentCategory = categories[selectedCatIndex]

    var fromUnitIndex by remember { mutableStateOf(0) }
    var toUnitIndex by remember { mutableStateOf(1) }
    var inputValueStr by remember { mutableStateOf("1") }

    val fromUnit = currentCategory.units.getOrElse(fromUnitIndex) { currentCategory.units.first() }
    val toUnit = currentCategory.units.getOrElse(toUnitIndex) { currentCategory.units.last() }

    val inputValue = inputValueStr.toDoubleOrNull() ?: 1.0
    val convertedValue = remember(inputValue, fromUnit, toUnit) {
        UnitConverter.convert(inputValue, fromUnit, toUnit)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "محول الوحدات العلمي (CONV)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MathCyan
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedCatIndex,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MathCyan
            ) {
                categories.forEachIndexed { index, cat ->
                    Tab(
                        selected = selectedCatIndex == index,
                        onClick = {
                            selectedCatIndex = index
                            fromUnitIndex = 0
                            toUnitIndex = 1
                        },
                        text = { Text(cat.nameArabic, fontSize = 12.sp) }
                    )
                }
            }

            OutlinedTextField(
                value = inputValueStr,
                onValueChange = { inputValueStr = it },
                label = { Text("القيمة المدخلة") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            )

            // From / To selector row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("من وحدة:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    var expandedFrom by remember { mutableStateOf(false) }
                    OutlinedButton(
                        onClick = { expandedFrom = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("${fromUnit.nameArabic} (${fromUnit.symbol})", fontSize = 12.sp)
                    }
                    DropdownMenu(expanded = expandedFrom, onDismissRequest = { expandedFrom = false }) {
                        currentCategory.units.forEachIndexed { i, u ->
                            DropdownMenuItem(
                                text = { Text("${u.nameArabic} (${u.symbol})") },
                                onClick = {
                                    fromUnitIndex = i
                                    expandedFrom = false
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        val temp = fromUnitIndex
                        fromUnitIndex = toUnitIndex
                        toUnitIndex = temp
                    }
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = "تبديل", tint = MathGold)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("إلى وحدة:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    var expandedTo by remember { mutableStateOf(false) }
                    OutlinedButton(
                        onClick = { expandedTo = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("${toUnit.nameArabic} (${toUnit.symbol})", fontSize = 12.sp)
                    }
                    DropdownMenu(expanded = expandedTo, onDismissRequest = { expandedTo = false }) {
                        currentCategory.units.forEachIndexed { i, u ->
                            DropdownMenuItem(
                                text = { Text("${u.nameArabic} (${u.symbol})") },
                                onClick = {
                                    toUnitIndex = i
                                    expandedTo = false
                                }
                            )
                        }
                    }
                }
            }

            // Result Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MathSurfaceElevatedDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("النتيجة المحولة:", fontSize = 12.sp, color = MathGold)
                    Text(
                        text = "${String.format(java.util.Locale.US, "%.6f", convertedValue).trimEnd('0').trimEnd('.')} ${toUnit.symbol}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MathCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Button(
                onClick = {
                    onInsertResult(String.format(java.util.Locale.US, "%.6f", convertedValue).trimEnd('0').trimEnd('.'))
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MathOperatorKey)
            ) {
                Text("إدراج القيمة في الحاسبة", fontWeight = FontWeight.Bold)
            }
        }
    }
}
