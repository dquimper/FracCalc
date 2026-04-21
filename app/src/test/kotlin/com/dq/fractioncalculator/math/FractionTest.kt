package com.dq.fractioncalculator.math

import org.junit.Assert.assertEquals
import org.junit.Test

class FractionTest {
    @Test fun add() = assertEquals(Fraction(7, 6), Fraction(1, 2) + Fraction(2, 3))
    @Test fun sub() = assertEquals(Fraction(1, 4), Fraction(3, 4) - Fraction(1, 2))
    @Test fun mul() = assertEquals(Fraction(1, 3), Fraction(1, 2) * Fraction(2, 3))
    @Test fun div() = assertEquals(Fraction(2, 1), Fraction(1, 2) / Fraction(1, 4))
    @Test fun simplify() = assertEquals(Fraction(1, 3), Fraction(2, 6).simplify())
    @Test fun toMixed() = assertEquals(Triple(6L, 1L, 6L), Fraction(37, 6).toMixed())
    @Test fun toMixedNegative() {
        val (w, n, d) = Fraction(-5, 4).toMixed()
        assertEquals(-1L, w); assertEquals(1L, n); assertEquals(4L, d)
    }
    @Test fun ofMixed() = assertEquals(Fraction(5, 2), Fraction.ofMixed(2, 1, 2))
    @Test fun zero() = assertEquals(Fraction.ZERO, Fraction(1, 2) - Fraction(1, 2))
    @Test fun negativeResult() = assertEquals(Fraction(-1, 4), Fraction(1, 2) - Fraction(3, 4))
}
