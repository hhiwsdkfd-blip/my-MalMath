package com.example.math

import kotlin.math.*

/**
 * Complex Number support (CMPLX mode as in Calc Business & HiEdu).
 */
data class ComplexNumber(val real: Double, val imag: Double = 0.0) {

    fun magnitude(): Double = sqrt(real * real + imag * imag)
    fun angleRad(): Double = atan2(imag, real)
    fun angleDeg(): Double = Math.toDegrees(angleRad())

    operator fun plus(other: ComplexNumber) = ComplexNumber(real + other.real, imag + other.imag)
    operator fun minus(other: ComplexNumber) = ComplexNumber(real - other.real, imag - other.imag)
    operator fun times(other: ComplexNumber) = ComplexNumber(
        real * other.real - imag * other.imag,
        real * other.imag + imag * other.real
    )
    operator fun div(other: ComplexNumber): ComplexNumber {
        val d = other.real * other.real + other.imag * other.imag
        if (d == 0.0) throw ArithmeticException("Division by zero in complex division")
        return ComplexNumber(
            (real * other.real + imag * other.imag) / d,
            (imag * other.real - real * other.imag) / d
        )
    }

    fun conjugate() = ComplexNumber(real, -imag)

    fun toPrettyString(): String {
        val rStr = if (abs(real) < 1e-9) "0" else format(real)
        val iStr = if (abs(imag) < 1e-9) "0" else format(abs(imag))

        return when {
            abs(imag) < 1e-9 -> rStr
            abs(real) < 1e-9 -> if (imag < 0) "-${iStr}i" else "${iStr}i"
            imag < 0 -> "$rStr - ${iStr}i"
            else -> "$rStr + ${iStr}i"
        }
    }

    fun toPolarString(): String {
        val r = format(magnitude())
        val deg = format(angleDeg())
        return "$r ∠ ${deg}°"
    }

    companion object {
        private fun format(v: Double): String {
            if (abs(v - round(v)) < 1e-9) return round(v).toLong().toString()
            return String.format(java.util.Locale.US, "%.4f", v).trimEnd('0').trimEnd('.')
        }
    }
}
