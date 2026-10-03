package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AiSolverView(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val prompt by viewModel.aiPrompt.collectAsState()
    val isLoading by viewModel.isAiLoading.collectAsState()
    val result by viewModel.aiResult.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    val quickQuestions = listOf(
        "احسب مشتقة الدالة f(x) = (x^2 + 1)/(x - 1) مع الخطوات",
        "احسب تكامل ∫ x * e^(2x) dx بالتجزيء خطوة بخطوة",
        "احسب النهاية lim (x->0) (sin(3x) - 3x) / x^3 بقاعدة لوبيتال",
        "حل المعادلة التفاضلية: dy/dx + 2y = 4x مع الشرط الابتدائي y(0) = 1",
        "أوجد القيم العظمى والصغرى ونقاط الانقلاب للدالة f(x) = x^3 - 6x^2 + 9x + 2",
        "احسب حجم المخروط الناقص إذا كان نصفا القطرين 4 و 6 والارتفاع 9"
    )

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MathGold)
                        Text(
                            text = "المعلم الرياضي الذكي (حل أي مسألة خطوة بخطوة)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MathCyan
                        )
                    }

                    Text(
                        text = "اكتب أي مسألة رياضية أو معادلة أو مسألة كلامية في الجبر، الهندسة، التفاضل، التكامل، أو النهايات، وسيقوم الذكاء الاصطناعي بحلها بالخطوات الكاملة.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { viewModel.aiPrompt.value = it },
                        label = { Text("اكتب مسألتك الرياضية هنا...") },
                        placeholder = { Text("مثال: احسب تكامل sin^2(x) cos(x) dx بالتفصيل...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        maxLines = 5
                    )

                    Button(
                        onClick = { viewModel.solveWithAi() },
                        enabled = !isLoading && prompt.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("ai_solve_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MathOperatorKey)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(10.dp))
                            Text("جاري التفكير والحل بالخطوات...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("حل المسألة بالذكاء الاصطناعي", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Quick Topic Suggestions
        item {
            Text("مسائل مقترحة للحل السريع:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MathGold)
        }

        quickQuestions.chunked(2).forEach { pair ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { q ->
                        OutlinedButton(
                            onClick = { viewModel.solveWithAi(q) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            Text(
                                text = q,
                                fontSize = 11.sp,
                                maxLines = 2,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Result Card
        if (result != null) {
            item {
                val r = result!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (r.isSuccess) MathSurfaceElevatedDark else MathRose.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (r.isSuccess) "الحل التفصيلي المعتمد:" else "تنبيه:",
                                color = if (r.isSuccess) MathGold else MathRose,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            if (r.isSuccess) {
                                IconButton(
                                    onClick = { clipboardManager.setText(AnnotatedString(r.solution)) }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ الحل", tint = MathCyan)
                                }
                            }
                        }

                        if (r.isSuccess) {
                            Text(
                                text = r.solution,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text(
                                text = r.errorMessage ?: "تعذر إتمام الحل.",
                                fontSize = 14.sp,
                                color = MathRose,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
