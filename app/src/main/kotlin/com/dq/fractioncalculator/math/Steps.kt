package com.dq.fractioncalculator.math

import kotlin.math.abs

enum class Op(val symbol: String) { ADD("+"), SUB("−"), MUL("×"), DIV("÷") }

sealed class Step {
    data class ShowEquation(val left: MixedRep, val op: Op, val right: MixedRep) : Step()
    data class ConvertToImproper(val left: ImproperRep, val op: Op, val right: ImproperRep) : Step()
    data class SimplifiedImproper(val left: FracRep, val op: Op, val right: FracRep) : Step()
    data class CommonDenom(val left: ScaledRep, val op: Op, val right: ScaledRep, val lcm: Long) : Step()
    data class AfterScale(val left: FracRep, val op: Op, val right: FracRep) : Step()
    data class CombinedNumerator(val left: Long, val op: Op, val right: Long, val den: Long) : Step()
    data class SingleFraction(val num: Long, val den: Long) : Step()
    data class BackToMixed(val whole: Long, val rem: Long, val den: Long) : Step()
    data class FinalResult(val whole: Long, val num: Long, val den: Long) : Step()
}

data class MixedRep(val whole: Long, val num: Long, val den: Long)
data class ImproperRep(val wholeExpr: String, val num: Long, val den: Long)
data class FracRep(val num: Long, val den: Long)
data class ScaledRep(val num: Long, val scale: Long, val den: Long)

fun computeWithSteps(leftFrac: Fraction, op: Op, rightFrac: Fraction): Pair<Fraction, List<Step>> {
    val steps = mutableListOf<Step>()

    val (lw, ln, ld) = leftFrac.toMixed()
    val (rw, rn, rd) = rightFrac.toMixed()
    steps.add(Step.ShowEquation(MixedRep(lw, ln, ld), op, MixedRep(rw, rn, rd)))

    val lImproper = toImproper(lw, ln, ld)
    val rImproper = toImproper(rw, rn, rd)
    steps.add(Step.ConvertToImproper(lImproper, op, rImproper))

    val lFrac = Fraction(lImproper.num, lImproper.den).simplify()
    val rFrac = Fraction(rImproper.num, rImproper.den).simplify()

    if (lFrac.num != lImproper.num || lFrac.den != lImproper.den ||
        rFrac.num != rImproper.num || rFrac.den != rImproper.den) {
        steps.add(Step.SimplifiedImproper(FracRep(lFrac.num, lFrac.den), op, FracRep(rFrac.num, rFrac.den)))
    }

    val result: Fraction
    when (op) {
        Op.ADD, Op.SUB -> {
            val l = lFrac
            val r = if (op == Op.SUB) Fraction(-rFrac.num, rFrac.den) else rFrac
            val denom = lcm(l.den, r.den)
            val lScale = denom / l.den
            val rScale = denom / r.den
            steps.add(Step.CommonDenom(
                ScaledRep(l.num, lScale, denom), op, ScaledRep(r.num, rScale, denom), denom
            ))
            val lScaled = l.num * lScale
            val rScaled = r.num * rScale
            steps.add(Step.AfterScale(FracRep(lScaled, denom), op, FracRep(rScaled, denom)))
            steps.add(Step.CombinedNumerator(lScaled, op, rScaled, denom))
            val sumNum = lScaled + rScaled
            steps.add(Step.SingleFraction(sumNum, denom))
            result = Fraction(sumNum, denom).simplify()
        }
        Op.MUL -> {
            result = lFrac * rFrac
            steps.add(Step.SingleFraction(lFrac.num * rFrac.num, lFrac.den * rFrac.den))
            if (result.num != lFrac.num * rFrac.num || result.den != lFrac.den * rFrac.den) {
                steps.add(Step.SingleFraction(result.num, result.den))
            }
        }
        Op.DIV -> {
            val recipDen = abs(rFrac.num)
            val recipNum = rFrac.den * (if (rFrac.num < 0) -1L else 1L)
            steps.add(Step.SingleFraction(lFrac.num * recipNum, lFrac.den * recipDen))
            result = lFrac / rFrac
            if (result.num * (lFrac.den * recipDen) != lFrac.num * recipNum * result.den) {
                steps.add(Step.SingleFraction(result.num, result.den))
            }
        }
    }

    if (!result.isWhole) {
        val (rw2, rn2, rd2) = result.toMixed()
        if (rw2 != 0L && rn2 != 0L) {
            steps.add(Step.BackToMixed(rw2, rn2, rd2))
            steps.add(Step.FinalResult(rw2, rn2, rd2))
        } else {
            steps.add(Step.FinalResult(rw2, rn2, rd2))
        }
    } else {
        val whole = result.num / result.den
        steps.add(Step.FinalResult(whole, 0L, result.den))
    }

    return result to steps
}

private fun toImproper(whole: Long, num: Long, den: Long): ImproperRep {
    val absWhole = abs(whole)
    val sign = if (whole < 0) -1L else 1L
    val improperNum = sign * (absWhole * den + num)
    val expr = if (num == 0L) "$absWhole×$den" else "$absWhole×$den+$num"
    return ImproperRep(expr, improperNum, den)
}
