package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ScientificCalculatorView(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val expression by viewModel.sciExpression.collectAsState()
    val cursorPosition by viewModel.cursorPosition.collectAsState()
    val result by viewModel.sciResult.collectAsState()
    val error by viewModel.sciError.collectAsState()
    val isDegree by viewModel.isDegreeMode.collectAsState()
    val isShift by viewModel.isShiftMode.collectAsState()
    val isAlpha by viewModel.isAlphaMode.collectAsState()
    val keypadStyle by viewModel.keypadStyle.collectAsState()

    val exprScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // ==========================================
        // 1. TOP CASIO/HIEdu MODIFIER & STATUS ROW
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // SHIFT Key (Golden Amber)
                Button(
                    onClick = { viewModel.toggleShiftMode() },
                    modifier = Modifier.height(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isShift) MathGold else MathGold.copy(alpha = 0.2f),
                        contentColor = if (isShift) Color.Black else MathGold
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("SHIFT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // ALPHA Key (Coral Red)
                Button(
                    onClick = { viewModel.toggleAlphaMode() },
                    modifier = Modifier.height(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAlpha) MathRose else MathRose.copy(alpha = 0.2f),
                        contentColor = if (isAlpha) Color.White else MathRose
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("ALPHA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // DEG / RAD Toggle
                FilledTonalButton(
                    onClick = { viewModel.toggleDegreeMode() },
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(if (isDegree) "DEG" else "RAD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // CONST (Scientific Constants)
                FilledTonalButton(
                    onClick = { viewModel.setShowConstantsSheet(true) },
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("CONST", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MathGold)
                }

                // CONV (Unit Converter)
                FilledTonalButton(
                    onClick = { viewModel.setShowConverterSheet(true) },
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("CONV", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MathCyan)
                }

                // S <=> D (Fraction to Decimal Toggle)
                Button(
                    onClick = { viewModel.toggleFractionDecimal() },
                    modifier = Modifier.height(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MathEmerald.copy(alpha = 0.25f),
                        contentColor = MathEmerald
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Text("S⇔D", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ==========================================
        // 2. NATURAL DISPLAY (Screen)
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MathSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Status Badges line
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (isShift) Text("S", fontWeight = FontWeight.Bold, color = MathGold, fontSize = 11.sp)
                        if (isAlpha) Text("A", fontWeight = FontWeight.Bold, color = MathRose, fontSize = 11.sp)
                        Text(if (isDegree) "DEG" else "RAD", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("MATH", fontSize = 11.sp, color = MathCyan)
                    }

                    // Memory Recall indicators
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { viewModel.memoryAdd() }, contentPadding = PaddingValues(2.dp)) {
                            Text("M+", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = { viewModel.memoryRecall() }, contentPadding = PaddingValues(2.dp)) {
                            Text("MR", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = { viewModel.memoryClear() }, contentPadding = PaddingValues(2.dp)) {
                            Text("MC", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Expression with Cursor
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(exprScrollState, reverseScrolling = true),
                    horizontalArrangement = Arrangement.End
                ) {
                    val displayExpr = if (expression.isEmpty()) "0" else expression
                    Text(
                        text = displayExpr,
                        fontSize = 26.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }

                // Result Line
                if (error != null) {
                    Text(
                        text = error!!,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "= $result",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MathCyan,
                            textAlign = TextAlign.End,
                            modifier = Modifier.testTag("calculator_result_text"),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // ==========================================
        // 3. MALMATH FAST CALCULUS & VARIABLES BAR
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            QuickMathChip("∫") { viewModel.appendSci("int(") }
            QuickMathChip("∫_a^b") { viewModel.setTab(CalculatorTab.CALCULUS) }
            QuickMathChip("d/dx") { viewModel.appendSci("d/dx(") }
            QuickMathChip("lim") { viewModel.appendSci("lim(") }
            QuickMathChip("∞") { viewModel.appendSci("inf") }
            QuickMathChip("x") { viewModel.appendSci("x") }
            QuickMathChip("y") { viewModel.appendSci("y") }
            QuickMathChip("z") { viewModel.appendSci("z") }
            QuickMathChip("π") { viewModel.appendSci("pi") }
            QuickMathChip("θ") { viewModel.appendSci("theta") }
            QuickMathChip("e") { viewModel.appendSci("e") }
            QuickMathChip("i") { viewModel.appendSci("i") }
        }

        // ==========================================
        // 4. SCIENTIFIC KEYPAD (CASIO & CALC BUSINESS DUAL-LEGEND)
        // ==========================================
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Row 1: Fractions, Roots, Powers, Logs
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                CasioKey(
                    label = "■/■",
                    shiftLabel = "a b/c",
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.appendSci(if (isShift) "+1/(" else "/(") }
                )
                CasioKey(
                    label = "√■",
                    shiftLabel = "³√",
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.appendSci(if (isShift) "cbrt(" else "sqrt(") }
                )
                CasioKey(
                    label = "x²",
                    shiftLabel = "x³",
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.appendSci(if (isShift) "^3" else "^2") }
                )
                CasioKey(
                    label = "x■",
                    shiftLabel = "■√",
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.appendSci("^") }
                )
                CasioKey(
                    label = "log",
                    shiftLabel = "10ˣ",
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.appendSci(if (isShift) "10^(" else "log(") }
                )
                CasioKey(
                    label = "ln",
                    shiftLabel = "eˣ",
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.appendSci(if (isShift) "exp(" else "ln(") }
                )
            }

            // Row 2: Trig, Hyp, Constants
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                CasioKey(
                    label = "sin",
                    shiftLabel = "sin⁻¹",
                    alphaLabel = "D",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.appendSci(if (isAlpha) "D" else if (isShift) "asin(" else "sin(") }
                )
                CasioKey(
                    label = "cos",
                    shiftLabel = "cos⁻¹",
                    alphaLabel = "E",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.appendSci(if (isAlpha) "E" else if (isShift) "acos(" else "cos(") }
                )
                CasioKey(
                    label = "tan",
                    shiftLabel = "tan⁻¹",
                    alphaLabel = "F",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.appendSci(if (isAlpha) "F" else if (isShift) "atan(" else "tan(") }
                )
                CasioKey(
                    label = "hyp",
                    shiftLabel = "sinh",
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.appendSci(if (isShift) "sinh(" else "tanh(") }
                )
                CasioKey(
                    label = "(",
                    shiftLabel = "%",
                    alphaLabel = "X",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.appendSci(if (isAlpha) "x" else if (isShift) "%" else "(") }
                )
                CasioKey(
                    label = ")",
                    shiftLabel = ",",
                    alphaLabel = "Y",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.appendSci(if (isAlpha) "y" else if (isShift) "," else ")") }
                )
            }

            // Row 3: Numeric 7, 8, 9, DEL, AC
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                CasioKey(label = "7", shiftLabel = "CONST", alphaLabel = null, modifier = Modifier.weight(1f), onClick = { if (isShift) viewModel.setShowConstantsSheet(true) else viewModel.appendSci("7") })
                CasioKey(label = "8", shiftLabel = "CONV", alphaLabel = null, modifier = Modifier.weight(1f), onClick = { if (isShift) viewModel.setShowConverterSheet(true) else viewModel.appendSci("8") })
                CasioKey(label = "9", shiftLabel = "nPr", alphaLabel = null, modifier = Modifier.weight(1f), onClick = { if (isShift) viewModel.appendSci("perm(") else viewModel.appendSci("9") })
                CasioKey(
                    label = "DEL",
                    shiftLabel = null,
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    keyColor = MathActionKey.copy(alpha = 0.85f),
                    textColor = Color.White,
                    onClick = { viewModel.backspaceSci() }
                )
                CasioKey(
                    label = "AC",
                    shiftLabel = null,
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    keyColor = MathActionKey,
                    textColor = Color.White,
                    onClick = { viewModel.clearSci() }
                )
            }

            // Row 4: Numeric 4, 5, 6, Multiply, Divide
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                CasioKey(label = "4", shiftLabel = "nCr", alphaLabel = null, modifier = Modifier.weight(1f), onClick = { if (isShift) viewModel.appendSci("comb(") else viewModel.appendSci("4") })
                CasioKey(label = "5", shiftLabel = "GCD", alphaLabel = null, modifier = Modifier.weight(1f), onClick = { if (isShift) viewModel.appendSci("gcd(") else viewModel.appendSci("5") })
                CasioKey(label = "6", shiftLabel = "LCM", alphaLabel = null, modifier = Modifier.weight(1f), onClick = { if (isShift) viewModel.appendSci("lcm(") else viewModel.appendSci("6") })
                CasioKey(
                    label = "×",
                    shiftLabel = "x!",
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    keyColor = MathOperatorKey,
                    textColor = Color.White,
                    onClick = { if (isShift) viewModel.appendSci("!") else viewModel.appendSci("*") }
                )
                CasioKey(
                    label = "÷",
                    shiftLabel = "mod",
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    keyColor = MathOperatorKey,
                    textColor = Color.White,
                    onClick = { if (isShift) viewModel.appendSci("%") else viewModel.appendSci("/") }
                )
            }

            // Row 5: Numeric 1, 2, 3, Add, Subtract
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                CasioKey(label = "1", shiftLabel = "Pol", alphaLabel = null, modifier = Modifier.weight(1f), onClick = { viewModel.appendSci("1") })
                CasioKey(label = "2", shiftLabel = "Rec", alphaLabel = null, modifier = Modifier.weight(1f), onClick = { viewModel.appendSci("2") })
                CasioKey(label = "3", shiftLabel = "Abs", alphaLabel = null, modifier = Modifier.weight(1f), onClick = { if (isShift) viewModel.appendSci("abs(") else viewModel.appendSci("3") })
                CasioKey(
                    label = "+",
                    shiftLabel = "Σ",
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    keyColor = MathOperatorKey,
                    textColor = Color.White,
                    onClick = { viewModel.appendSci("+") }
                )
                CasioKey(
                    label = "−",
                    shiftLabel = "Π",
                    alphaLabel = null,
                    modifier = Modifier.weight(1f),
                    keyColor = MathOperatorKey,
                    textColor = Color.White,
                    onClick = { viewModel.appendSci("-") }
                )
            }

            // Row 6: Numeric 0, Dot, Exp, Ans, Equals
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                CasioKey(label = "0", shiftLabel = "Ran#", alphaLabel = null, modifier = Modifier.weight(1f), onClick = { viewModel.appendSci("0") })
                CasioKey(label = ".", shiftLabel = "RanInt", alphaLabel = null, modifier = Modifier.weight(1f), onClick = { viewModel.appendSci(".") })
                CasioKey(label = "Exp", shiftLabel = "π", alphaLabel = "e", modifier = Modifier.weight(1f), onClick = { if (isShift) viewModel.appendSci("pi") else if (isAlpha) viewModel.appendSci("e") else viewModel.appendSci("e") })
                CasioKey(label = "Ans", shiftLabel = "PreAns", alphaLabel = null, modifier = Modifier.weight(1f), textColor = MathGold, onClick = { viewModel.appendSci("Ans") })
                CasioKey(
                    label = "=",
                    shiftLabel = "SOLVE",
                    alphaLabel = null,
                    modifier = Modifier.weight(1.5f),
                    keyColor = MathEqualKey,
                    textColor = Color.White,
                    onClick = { viewModel.calculateSci() }
                )
            }

            // ==========================================
            // 5. MALMATH BOTTOM CONTROLS (GRAPH, <, >, SOLVE)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // MalMath GRAPH Button
                Button(
                    onClick = { viewModel.openGraphWithCurrentExpr() },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MathCyanContainer,
                        contentColor = MathCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("GRAPH", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Cursor Left Arrow
                IconButton(
                    onClick = { viewModel.moveCursorLeft() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "يسار", tint = Color.White)
                }

                // Cursor Right Arrow
                IconButton(
                    onClick = { viewModel.moveCursorRight() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "يمين", tint = Color.White)
                }

                // MalMath 100% OFFLINE STEP-BY-STEP SOLVE Button
                Button(
                    onClick = { viewModel.solveOfflineCurrentExpression() },
                    modifier = Modifier
                        .weight(1.8f)
                        .height(44.dp)
                        .testTag("malmath_solve_offline_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MathOperatorKey,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("SOLVE (حل بالخطوات)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Quick Math Chip for MalMath Calculus Bar
@Composable
fun QuickMathChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MathCyanContainer.copy(alpha = 0.6f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(30.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = MathCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// Dual-legend Casio Key (Calc Business & HiEdu style with yellow SHIFT and red ALPHA markings above key)
@Composable
fun CasioKey(
    label: String,
    shiftLabel: String?,
    alphaLabel: String?,
    modifier: Modifier = Modifier,
    keyColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dual Sub-legends (Shift & Alpha)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = shiftLabel ?: "",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = MathGold,
                maxLines = 1
            )
            Text(
                text = alphaLabel ?: "",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = MathRose,
                maxLines = 1
            )
        }

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = keyColor,
                contentColor = textColor
            ),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
        ) {
            Text(
                text = label,
                fontSize = if (label.length > 3) 12.sp else 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
