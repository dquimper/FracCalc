package com.dq.fractioncalculator.math

import kotlin.math.abs

data class Fraction(val num: Long, val den: Long) {
    init {
        require(den > 0) { "denominator must be positive" }
    }

    operator fun plus(o: Fraction): Fraction {
        val lcm = lcm(den, o.den)
        return Fraction(num * (lcm / den) + o.num * (lcm / o.den), lcm).simplify()
    }

    operator fun minus(o: Fraction): Fraction = plus(Fraction(-o.num, o.den))

    operator fun times(o: Fraction): Fraction = Fraction(num * o.num, den * o.den).simplify()

    operator fun div(o: Fraction): Fraction {
        require(o.num != 0L) { "division by zero" }
        return if (o.num < 0)
            Fraction(-num * o.den, den * -o.num).simplify()
        else
            Fraction(num * o.den, den * o.num).simplify()
    }

    fun simplify(): Fraction {
        if (num == 0L) return Fraction(0, 1)
        val g = gcd(abs(num), den)
        return Fraction(num / g, den / g)
    }

    fun toMixed(): Triple<Long, Long, Long> {
        val whole = num / den
        val rem = abs(num % den)
        return if (rem == 0L) Triple(whole, 0L, den) else Triple(whole, rem, den)
    }

    fun toDouble(): Double = num.toDouble() / den.toDouble()

    val isNegative: Boolean get() = num < 0
    val isZero: Boolean get() = num == 0L
    val isWhole: Boolean get() = num % den == 0L

    companion object {
        fun ofMixed(whole: Long, num: Long, den: Long): Fraction {
            require(den > 0)
            val sign = if (whole < 0) -1L else 1L
            return Fraction(sign * (abs(whole) * den + num), den).simplify()
        }

        val ZERO = Fraction(0, 1)
    }
}

fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b
