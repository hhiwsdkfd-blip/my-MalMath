package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * AI Math Master service powered by Gemini API.
 * Solves advanced math problems, differential equations, multivariable calculus,
 * limits proofs, geometry problems, with full step-by-step Arabic explanations.
 */
class GeminiMathService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    data class AiSolveResult(
        val isSuccess: Boolean,
        val solution: String,
        val finalAnswer: String? = null,
        val errorMessage: String? = null
    )

    suspend fun solveMathProblem(problemText: String): AiSolveResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext AiSolveResult(
                isSuccess = false,
                solution = "",
                errorMessage = "مفتاح API الخاص بـ Gemini غير مهيأ. يمكنك إدخال مفتاحك في لوحة Secrets في AI Studio لتفعيل المعلم الذكي مع كامل خطوات الذكاء الاصطناعي، أو استخدام المحرك الحسابي الداخلي المتطور في التبويبات الأخرى."
            )
        }

        try {
            val systemPrompt = """
                أنت خبير رياضيات عالمي فائق التخصص وأستاذ جامعي في الرياضيات المتقدمة.
                مهمتك هي حل المسألة الرياضية المقدمة من المستخدم بأعلى درجات الدقة والوضوح باللغة العربية.
                المجالات تشمل:
                - الجبر (المعادلات الخطية، التربيعية، التكعيبية، المصفوفات، الفضاءات الاتجاهية، المتتابعات والمتسلسلات)
                - الهندسة (الهندسة الإقليدية، المجسمات، الهندسة التحليلية والفراغية، حساب المثلثات)
                - التفاضل والاشتقاق بجميع أنواعه (المشتقات الأولى والعليا، المشتقات الجزئية، قاعدة السلسلة، الاشتقاق الضمني، تطبيقات التفاضل)
                - التكامل بجميع أنواعه (التكامل المحدود وغير المحدود، التكامل بالتجزيء وبالتعويض والكسور الجزئية، التكامل الثنائي والثلاثي، المساحات والحجوم)
                - النهايات بجميع أنواعها (النهايات من جهة واحدة، النهايات عند اللانهاية، حالات عدم التعيين، قاعدة لوبيتال، مبرهنة الحصر الساندويتش)
                
                هيكل إجابتك يجب أن يكون منظماً كالتالي:
                1. تحديد معطيات المسألة ونوعها
                2. القوانين والقواعد الرياضية المطبقة
                3. خطوات الحل التفصيلية خطوة بخطوة مع التعليل الرياضي
                4. النتيجة النهائية بشكل بارز ومباشر
                اجعل الرياضيات واضحة واستخدم الرموز الرياضية المعتادة.
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "المسألة الرياضية:\n$problemText")
                            })
                        })
                    })
                }
                put("contents", contents)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemPrompt)
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.2)
                    put("topP", 0.95)
                    put("maxOutputTokens", 4096)
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                return@withContext AiSolveResult(
                    isSuccess = false,
                    solution = "",
                    errorMessage = "حدث خطأ أثناء التواصل مع خادم الذكاء الاصطناعي (${response.code}). يرجى التحقق من اتصال الإنترنت أو مفتاح API."
                )
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val solutionText = parts?.optJSONObject(0)?.optString("text")

            if (solutionText.isNullOrBlank()) {
                return@withContext AiSolveResult(
                    isSuccess = false,
                    solution = "",
                    errorMessage = "لم يتم استلام نص الحل من النموذج."
                )
            }

            // Extract a summary/final answer line if detectable
            val lines = solutionText.lines()
            val finalLine = lines.findLast { it.contains("النتيجة النهائية") || it.contains("الجواب") || it.contains("إذن") }

            AiSolveResult(
                isSuccess = true,
                solution = solutionText,
                finalAnswer = finalLine?.trim()
            )
        } catch (e: Exception) {
            AiSolveResult(
                isSuccess = false,
                solution = "",
                errorMessage = "خطأ: ${e.localizedMessage ?: "فشل الاتصال بالخدمة"}"
            )
        }
    }
}
