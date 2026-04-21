package com.dq.fractioncalculator.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.dq.fractioncalculator.math.Fraction
import com.dq.fractioncalculator.math.Op
import com.dq.fractioncalculator.math.Step
import com.dq.fractioncalculator.math.computeWithSteps

data class MixedInput(
    val whole: String = "",
    val num: String = "",
    val den: String = ""
) {
    fun toFraction(): Fraction? {
        if (whole.contains('.')) {
            // Decimal input like "1.5" → convert to fraction using string math (avoids float imprecision)
            val sign = if (whole.startsWith('-')) -1L else 1L
            val abs = whole.trimStart('-')
            val parts = abs.split('.')
            val intPart = parts[0].toLongOrNull() ?: 0L
            val fracStr = if (parts.size > 1) parts[1].trimEnd('0').ifEmpty { "0" } else "0"
            if (fracStr == "0") return Fraction(sign * intPart, 1L)
            val denominator = Math.pow(10.0, fracStr.length.toDouble()).toLong()
            val numerator = sign * (intPart * denominator + fracStr.toLong())
            return Fraction(numerator, denominator).simplify()
        }
        val w = whole.toLongOrNull() ?: 0L
        val n = num.toLongOrNull() ?: 0L
        val d = when {
            den.isEmpty() -> 1L
            else -> den.toLongOrNull() ?: return null
        }
        if (d == 0L) return null
        return Fraction.ofMixed(w, n, d)
    }

    val isEmpty: Boolean get() = whole.isEmpty() && num.isEmpty() && den.isEmpty()

    fun displayWhole(): String = if (whole.isEmpty()) "0" else whole
    fun displayNum(): String = num.ifEmpty { "0" }
    fun displayDen(): String = den.ifEmpty { "1" }
}

data class HistoryEntry(
    val left: MixedInput,
    val op: Op,
    val right: MixedInput,
    val result: Fraction
)

enum class ActiveSide { LEFT, RIGHT }
enum class ResultFormat { MIXED, IMPROPER }

class CalculatorViewModel : ViewModel() {
    var left by mutableStateOf(MixedInput())
    var right by mutableStateOf(MixedInput())
    var op by mutableStateOf<Op?>(null)
    var result by mutableStateOf<Fraction?>(null)
    var resultFormat by mutableStateOf(ResultFormat.MIXED)
    var steps by mutableStateOf<List<Step>>(emptyList())
    var activeSide by mutableStateOf(ActiveSide.LEFT)
    var showSteps by mutableStateOf(false)
    var showHistory by mutableStateOf(false)
    val history = mutableStateListOf<HistoryEntry>()

    fun onWholeDigit(digit: Int) {
        val s = activeSide
        if (s == ActiveSide.LEFT) {
            left = left.copy(whole = appendDigit(left.whole, digit))
        } else {
            right = right.copy(whole = appendDigit(right.whole, digit))
        }
        clearResult()
    }

    fun onNumDigit(digit: Int) {
        val s = activeSide
        if (s == ActiveSide.LEFT) {
            left = left.copy(num = appendDigit(left.num, digit))
        } else {
            right = right.copy(num = appendDigit(right.num, digit))
        }
        clearResult()
    }

    fun onDenDigit(digit: Int) {
        val s = activeSide
        if (s == ActiveSide.LEFT) {
            left = left.copy(den = appendDigit(left.den, digit))
        } else {
            right = right.copy(den = appendDigit(right.den, digit))
        }
        clearResult()
    }

    fun onDecimalPoint() {
        val current = if (activeSide == ActiveSide.LEFT) left.whole else right.whole
        if (current.contains('.')) return
        val newWhole = if (current.isEmpty() || current == "-") "${current}0." else "$current."
        if (activeSide == ActiveSide.LEFT) left = left.copy(whole = newWhole)
        else right = right.copy(whole = newWhole)
        clearResult()
    }

    fun onOp(newOp: Op) {
        if (op != null && activeSide == ActiveSide.RIGHT && !right.isEmpty) {
            evaluate()
        }
        op = newOp
        activeSide = ActiveSide.RIGHT
        clearResult()
    }

    fun onEquals() {
        if (result != null) {
            resultFormat = if (resultFormat == ResultFormat.MIXED) ResultFormat.IMPROPER else ResultFormat.MIXED
            return
        }
        if (op == null) {
            val frac = left.toFraction() ?: return
            result = frac.simplify()
        } else {
            evaluate()
        }
    }

    fun onClear() {
        left = MixedInput()
        right = MixedInput()
        op = null
        result = null
        steps = emptyList()
        activeSide = ActiveSide.LEFT
        showSteps = false
    }

    fun onWholeBackspace() {
        if (activeSide == ActiveSide.LEFT) {
            left = left.copy(whole = left.whole.dropLast(1))
        } else {
            right = right.copy(whole = right.whole.dropLast(1))
        }
        clearResult()
    }

    fun onNumBackspace() {
        if (activeSide == ActiveSide.LEFT) {
            left = left.copy(num = left.num.dropLast(1))
        } else {
            right = right.copy(num = right.num.dropLast(1))
        }
        clearResult()
    }

    fun onDenBackspace() {
        if (activeSide == ActiveSide.LEFT) {
            left = left.copy(den = left.den.dropLast(1))
        } else {
            right = right.copy(den = right.den.dropLast(1))
        }
        clearResult()
    }

    fun onSignToggle() {
        if (activeSide == ActiveSide.LEFT) {
            left = left.copy(whole = toggleSign(left.whole))
        } else {
            right = right.copy(whole = toggleSign(right.whole))
        }
        clearResult()
    }

    fun onHistoryTap(entry: HistoryEntry) {
        left = entry.left
        right = entry.right
        op = entry.op
        result = entry.result
        activeSide = ActiveSide.RIGHT
    }

    private fun evaluate() {
        val currentOp = op ?: return
        val lFrac = left.toFraction() ?: return
        val rFrac = right.toFraction() ?: return
        val (res, stepList) = computeWithSteps(lFrac, currentOp, rFrac)
        result = res
        steps = stepList
        history.add(0, HistoryEntry(left, currentOp, right, res))
        if (history.size > 50) history.removeAt(history.lastIndex)
    }

    private fun clearResult() {
        result = null
        resultFormat = ResultFormat.MIXED
        steps = emptyList()
    }

    private fun appendDigit(current: String, digit: Int): String {
        if (current.isEmpty() && digit == 0) return current
        if (current.replace("-", "").length >= 6) return current
        return current + digit.toString()
    }

    private fun toggleSign(value: String): String {
        return when {
            value.startsWith("-") -> value.removePrefix("-")
            value.isEmpty() || value == "0" -> value
            else -> "-$value"
        }
    }
}
