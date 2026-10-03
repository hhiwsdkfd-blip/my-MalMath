package com.example.math

import kotlin.math.*

/**
 * High-precision rational fraction for exact arithmetic and S<=>D toggle.
 */
data class Fraction(val num: Long, val den: Long = 1L) {
    init {
        require(den != 0L) { "Denominator cannot be zero" }
    }

    val numerator: Long
    val denominator: Long

    init {
        val g = gcd(abs(num), abs(den))
        val sign = if (den < 0) -1 else 1
        numerator = (num / g) * sign
        denominator = abs(den) / g
    }

    fun toDouble(): Double = numerator.toDouble() / denominator.toDouble()

    fun toPrettyString(): String {
        return if (denominator == 1L) {
            "$numerator"
        } else {
            "$numerator/$denominator"
        }
    }

    operator fun plus(other: Fraction): Fraction {
        val n = numerator * other.denominator + other.numerator * denominator
        val d = denominator * other.denominator
        return Fraction(n, d)
    }

    operator fun minus(other: Fraction): Fraction {
        val n = numerator * other.denominator - other.numerator * denominator
        val d = denominator * other.denominator
        return Fraction(n, d)
    }

    operator fun times(other: Fraction): Fraction {
        return Fraction(numerator * other.numerator, denominator * other.denominator)
    }

    operator fun div(other: Fraction): Fraction {
        return Fraction(numerator * other.denominator, denominator * other.numerator)
    }

    companion object {
        fun fromDouble(value: Double, maxDenominator: Long = 10000L): Fraction {
            if (value.isNaN() || value.isInfinite()) return Fraction(0, 1)
            val sign = if (value < 0) -1L else 1L
            val x = abs(value)

            var h1 = 1L; var h2 = 0L
            var k1 = 0L; var k2 = 1L
            var b = x

            do {
                val a = b.toLong()
                var aux = h1
                h1 = a * h1 + h2
                h2 = aux
                aux = k1
                k1 = a * k1 + k2
                k2 = aux
                b = 1.0 / (b - a)
            } while (abs(x - h1.toDouble() / k1.toDouble()) > x * 1e-6 && k1 < maxDenominator && b.isFinite() && b < 1e7)

            return Fraction(sign * h1, k1)
        }

        private fun gcd(a: Long, b: Long): Long {
            var x = a
            var y = b
            while (y != 0L) {
                val t = y
                y = x % y
                x = t
            }
            return if (x == 0L) 1L else x
        }
    }
}
