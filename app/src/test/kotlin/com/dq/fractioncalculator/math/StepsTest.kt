package com.dq.fractioncalculator.math

import org.junit.Assert.assertEquals
import org.junit.Test

class StepsTest {
    @Test fun referenceCase() {
        val left = Fraction.ofMixed(2, 1, 2)
        val right = Fraction.ofMixed(3, 2, 3)
        val (result, steps) = computeWithSteps(left, Op.ADD, right)
        assertEquals(Fraction(37, 6), result)
        val equation = steps.filterIsInstance<Step.ShowEquation>().first()
        assertEquals(2L, equation.left.whole)
        assertEquals(1L, equation.left.num)
        assertEquals(2L, equation.left.den)
        assertEquals(3L, equation.right.whole)
        assertEquals(2L, equation.right.num)
        assertEquals(3L, equation.right.den)
        val final = steps.filterIsInstance<Step.FinalResult>().first()
        assertEquals(6L, final.whole)
        assertEquals(1L, final.num)
        assertEquals(6L, final.den)
    }

    @Test fun multiplication() {
        val (result, _) = computeWithSteps(Fraction(1, 2), Op.MUL, Fraction(2, 3))
        assertEquals(Fraction(1, 3), result)
    }

    @Test fun division() {
        val (result, _) = computeWithSteps(Fraction(1, 2), Op.DIV, Fraction(1, 4))
        assertEquals(Fraction(2, 1), result)
    }
}
