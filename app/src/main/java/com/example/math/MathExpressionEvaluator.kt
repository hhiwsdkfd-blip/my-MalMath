package com.example.math

import java.util.Locale
import kotlin.math.*

/**
 * High-precision on-device mathematical expression parser and evaluator.
 * Supports standard arithmetic, powers, roots, trigonometric, inverse trig,
 * hyperbolic, logarithmic, and factorial functions with Radian/Degree toggle.
 */
class MathExpressionEvaluator(var isDegreeMode: Boolean = false) {

    companion object {
        const val PI_VALUE = Math.PI
        const val E_VALUE = Math.E
        const val PHI_VALUE = 1.618033988749895 // Golden ratio
    }

    fun evaluate(expression: String, variables: Map<String, Double> = emptyMap()): Double {
        val sanitized = sanitize(expression)
        val tokens = tokenize(sanitized, variables)
        val rpn = toRpn(tokens)
        return evaluateRpn(rpn)
    }

    fun evaluateFunction(expression: String, x: Double): Double {
        return evaluate(expression, mapOf("x" to x))
    }

    fun evaluate2DFunction(expression: String, x: Double, y: Double): Double {
        return evaluate(expression, mapOf("x" to x, "y" to y))
    }

    private fun sanitize(expr: String): String {
        return expr.trim()
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "pi")
            .replace("φ", "phi")
            .replace(" ", "")
    }

    private sealed class Token {
        data class NumberToken(val value: Double) : Token()
        data class OperatorToken(val op: Char, val precedence: Int, val isRightAssociative: Boolean = false) : Token()
        data class FunctionToken(val name: String) : Token()
        object LeftParen : Token()
        object RightParen : Token()
        object Comma : Token()
    }

    private fun tokenize(expr: String, variables: Map<String, Double>): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        var expectUnary = true

        while (i < expr.length) {
            val c = expr[i]

            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.' || expr[i] == 'e' || expr[i] == 'E')) {
                        if ((expr[i] == 'e' || expr[i] == 'E') && i + 1 < expr.length && (expr[i + 1] == '+' || expr[i + 1] == '-')) {
                            sb.append(expr[i])
                            i++
                            sb.append(expr[i])
                            i++
                        } else {
                            sb.append(expr[i])
                            i++
                        }
                    }
                    val num = sb.toString().toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $sb")
                    tokens.add(Token.NumberToken(num))
                    expectUnary = false
                }
                c == '(' -> {
                    tokens.add(Token.LeftParen)
                    i++
                    expectUnary = true
                }
                c == ')' -> {
                    tokens.add(Token.RightParen)
                    i++
                    expectUnary = false
                }
                c == ',' -> {
                    tokens.add(Token.Comma)
                    i++
                    expectUnary = true
                }
                c == '+' || c == '-' -> {
                    if (expectUnary) {
                        // Unary plus or minus
                        if (c == '-') {
                            // Represent unary minus as 0 - token
                            tokens.add(Token.NumberToken(0.0))
                            tokens.add(Token.OperatorToken('-', 2))
                        }
                    } else {
                        tokens.add(Token.OperatorToken(c, 2))
                        expectUnary = true
                    }
                    i++
                }
                c == '*' || c == '/' || c == '%' -> {
                    tokens.add(Token.OperatorToken(c, 3))
                    i++
                    expectUnary = true
                }
                c == '^' -> {
                    tokens.add(Token.OperatorToken(c, 4, isRightAssociative = true))
                    i++
                    expectUnary = true
                }
                c == '!' -> {
                    tokens.add(Token.OperatorToken(c, 5))
                    i++
                    expectUnary = false
                }
                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < expr.length && expr[i].isLetter()) {
                        sb.append(expr[i])
                        i++
                    }
                    val word = sb.toString().lowercase(Locale.ROOT)
                    when {
                        word == "pi" -> {
                            tokens.add(Token.NumberToken(PI_VALUE))
                            expectUnary = false
                        }
                        word == "e" && !variables.containsKey("e") -> {
                            tokens.add(Token.NumberToken(E_VALUE))
                            expectUnary = false
                        }
                        word == "phi" -> {
                            tokens.add(Token.NumberToken(PHI_VALUE))
                            expectUnary = false
                        }
                        variables.containsKey(word) -> {
                            tokens.add(Token.NumberToken(variables[word]!!))
                            expectUnary = false
                        }
                        else -> {
                            tokens.add(Token.FunctionToken(word))
                            expectUnary = true
                        }
                    }
                }
                else -> {
                    throw IllegalArgumentException("Unknown character: $c")
                }
            }
        }
        return tokens
    }

    private fun toRpn(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val stack = ArrayDeque<Token>()

        for (token in tokens) {
            when (token) {
                is Token.NumberToken -> output.add(token)
                is Token.FunctionToken -> stack.addLast(token)
                is Token.OperatorToken -> {
                    while (stack.isNotEmpty()) {
                        val top = stack.last()
                        if (top is Token.OperatorToken) {
                            if ((!token.isRightAssociative && token.precedence <= top.precedence) ||
                                (token.isRightAssociative && token.precedence < top.precedence)
                            ) {
                                output.add(stack.removeLast())
                            } else {
                                break
                            }
                        } else {
                            break
                        }
                    }
                    stack.addLast(token)
                }
                Token.Comma -> {
                    while (stack.isNotEmpty() && stack.last() !is Token.LeftParen) {
                        output.add(stack.removeLast())
                    }
                }
                Token.LeftParen -> stack.addLast(token)
                Token.RightParen -> {
                    while (stack.isNotEmpty() && stack.last() !is Token.LeftParen) {
                        output.add(stack.removeLast())
                    }
                    if (stack.isNotEmpty() && stack.last() is Token.LeftParen) {
                        stack.removeLast()
                    }
                    if (stack.isNotEmpty() && stack.last() is Token.FunctionToken) {
                        output.add(stack.removeLast())
                    }
                }
            }
        }
        while (stack.isNotEmpty()) {
            output.add(stack.removeLast())
        }
        return output
    }

    private fun evaluateRpn(rpn: List<Token>): Double {
        val stack = ArrayDeque<Double>()

        for (token in rpn) {
            when (token) {
                is Token.NumberToken -> stack.addLast(token.value)
                is Token.OperatorToken -> {
                    if (token.op == '!') {
                        val a = stack.removeLast()
                        stack.addLast(factorial(a))
                    } else {
                        if (stack.size < 2) throw IllegalArgumentException("Malformed expression for operator ${token.op}")
                        val b = stack.removeLast()
                        val a = stack.removeLast()
                        val res = when (token.op) {
                            '+' -> a + b
                            '-' -> a - b
                            '*' -> a * b
                            '/' -> {
                                if (b == 0.0) throw ArithmeticException("Division by zero")
                                a / b
                            }
                            '%' -> a % b
                            '^' -> a.pow(b)
                            else -> throw UnsupportedOperationException("Unknown operator: ${token.op}")
                        }
                        stack.addLast(res)
                    }
                }
                is Token.FunctionToken -> {
                    val res = applyFunction(token.name, stack)
                    stack.addLast(res)
                }
                else -> {}
            }
        }

        if (stack.isEmpty()) return 0.0
        return stack.removeLast()
    }

    private fun toRad(angle: Double): Double = if (isDegreeMode) Math.toRadians(angle) else angle
    private fun fromRad(angle: Double): Double = if (isDegreeMode) Math.toDegrees(angle) else angle

    private fun applyFunction(name: String, stack: ArrayDeque<Double>): Double {
        when (name.lowercase(Locale.ROOT)) {
            "sin" -> {
                val a = stack.removeLast()
                return sin(toRad(a))
            }
            "cos" -> {
                val a = stack.removeLast()
                return cos(toRad(a))
            }
            "tan" -> {
                val a = stack.removeLast()
                return tan(toRad(a))
            }
            "cot" -> {
                val a = stack.removeLast()
                val t = tan(toRad(a))
                if (t == 0.0) throw ArithmeticException("cotangent undefined")
                return 1.0 / t
            }
            "sec" -> {
                val a = stack.removeLast()
                val c = cos(toRad(a))
                if (c == 0.0) throw ArithmeticException("secant undefined")
                return 1.0 / c
            }
            "csc" -> {
                val a = stack.removeLast()
                val s = sin(toRad(a))
                if (s == 0.0) throw ArithmeticException("cosecant undefined")
                return 1.0 / s
            }
            "asin", "arcsin" -> {
                val a = stack.removeLast()
                return fromRad(asin(a))
            }
            "acos", "arccos" -> {
                val a = stack.removeLast()
                return fromRad(acos(a))
            }
            "atan", "arctan" -> {
                val a = stack.removeLast()
                return fromRad(atan(a))
            }
            "sinh" -> {
                val a = stack.removeLast()
                return sinh(a)
            }
            "cosh" -> {
                val a = stack.removeLast()
                return cosh(a)
            }
            "tanh" -> {
                val a = stack.removeLast()
                return tanh(a)
            }
            "asinh" -> {
                val a = stack.removeLast()
                return ln(a + sqrt(a * a + 1.0))
            }
            "acosh" -> {
                val a = stack.removeLast()
                return ln(a + sqrt(a * a - 1.0))
            }
            "atanh" -> {
                val a = stack.removeLast()
                return 0.5 * ln((1.0 + a) / (1.0 - a))
            }
            "ln" -> {
                val a = stack.removeLast()
                if (a <= 0) throw ArithmeticException("ln requires positive argument")
                return ln(a)
            }
            "log", "log10" -> {
                val a = stack.removeLast()
                if (a <= 0) throw ArithmeticException("log requires positive argument")
                return log10(a)
            }
            "log2" -> {
                val a = stack.removeLast()
                if (a <= 0) throw ArithmeticException("log2 requires positive argument")
                return ln(a) / ln(2.0)
            }
            "exp" -> {
                val a = stack.removeLast()
                return exp(a)
            }
            "sqrt" -> {
                val a = stack.removeLast()
                if (a < 0) throw ArithmeticException("Square root of negative number")
                return sqrt(a)
            }
            "cbrt" -> {
                val a = stack.removeLast()
                return cbrt(a)
            }
            "abs" -> {
                val a = stack.removeLast()
                return abs(a)
            }
            "floor" -> {
                val a = stack.removeLast()
                return floor(a)
            }
            "ceil" -> {
                val a = stack.removeLast()
                return ceil(a)
            }
            "fact" -> {
                val a = stack.removeLast()
                return factorial(a)
            }
            "gamma" -> {
                val a = stack.removeLast()
                return gamma(a)
            }
            "pow" -> {
                val b = stack.removeLast()
                val a = stack.removeLast()
                return a.pow(b)
            }
            "root" -> {
                val n = stack.removeLast()
                val a = stack.removeLast()
                return a.pow(1.0 / n)
            }
            "gcd" -> {
                val b = stack.removeLast().toLong()
                val a = stack.removeLast().toLong()
                return gcd(a, b).toDouble()
            }
            "lcm" -> {
                val b = stack.removeLast().toLong()
                val a = stack.removeLast().toLong()
                return lcm(a, b).toDouble()
            }
            "npr", "perm" -> {
                val r = stack.removeLast().toLong()
                val n = stack.removeLast().toLong()
                return permutations(n, r).toDouble()
            }
            "ncr", "comb" -> {
                val r = stack.removeLast().toLong()
                val n = stack.removeLast().toLong()
                return combinations(n, r).toDouble()
            }
            else -> throw UnsupportedOperationException("Unknown function: $name")
        }
    }

    private fun factorial(n: Double): Double {
        if (n < 0) throw ArithmeticException("Factorial of negative number")
        val intN = n.toLong()
        if (n == intN.toDouble() && intN <= 20) {
            var res = 1.0
            for (i in 2..intN) res *= i
            return res
        }
        // Approximate using gamma function for non-integers or larger values
        return gamma(n + 1.0)
    }

    // Lanczos approximation for Gamma function
    private fun gamma(x: Double): Double {
        val p = doubleArrayOf(
            676.5203681218851, -1259.1392167224028,
            771.32342877765313, -176.61502916214059,
            12.507343278686905, -0.138571095836524,
            9.9843695780195716e-6, 1.5056327351493116e-7
        )
        var z = x
        if (z < 0.5) {
            return PI_VALUE / (sin(PI_VALUE * z) * gamma(1.0 - z))
        }
        z -= 1.0
        var a = 0.99999999999980993
        for (i in p.indices) {
            a += p[i] / (z + i + 1)
        }
        val t = z + p.size - 0.5
        return sqrt(2 * PI_VALUE) * t.pow(z + 0.5) * exp(-t) * a
    }

    private fun gcd(a: Long, b: Long): Long {
        var x = abs(a)
        var y = abs(b)
        while (y != 0L) {
            val t = y
            y = x % y
            x = t
        }
        return x
    }

    private fun lcm(a: Long, b: Long): Long {
        if (a == 0L || b == 0L) return 0L
        return abs(a * b) / gcd(a, b)
    }

    private fun permutations(n: Long, r: Long): Long {
        if (r < 0 || r > n) return 0L
        var res = 1L
        for (i in 0 until r) {
            res *= (n - i)
        }
        return res
    }

    private fun combinations(n: Long, r: Long): Long {
        if (r < 0 || r > n) return 0L
        val k = min(r, n - r)
        var res = 1L
        for (i in 1..k) {
            res = res * (n - k + i) / i
        }
        return res
    }
}
