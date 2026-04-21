package com.dq.fractioncalculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dq.fractioncalculator.math.Fraction
import com.dq.fractioncalculator.math.Op
import com.dq.fractioncalculator.state.ActiveSide
import com.dq.fractioncalculator.state.MixedInput
import com.dq.fractioncalculator.ui.theme.DisplayBackground
import com.dq.fractioncalculator.ui.theme.TextPrimary
import java.util.Locale

internal enum class DisplaySize(
    val wholeSize: TextUnit,
    val fracSize: TextUnit,
    val opSize: TextUnit,
    val barHeight: Dp
) {
    SINGLE(72.sp, 32.sp, 56.sp, 1.5.dp),
    EXPR(44.sp, 20.sp, 34.sp, 1.dp),
    RESULT(22.sp, 11.sp, 20.sp, 1.dp),
}

@Composable
fun Display(
    left: MixedInput,
    right: MixedInput,
    op: Op?,
    result: Fraction?,
    activeSide: ActiveSide,
    onResultTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    // Auto-scroll to the right end whenever the expression changes
    LaunchedEffect(left, right, op, result) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(DisplayBackground),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            if (result != null) {
                Text(
                    text = "(${String.format(Locale.US, "%.5f", result.toDouble())})",
                    fontSize = 13.sp,
                    color = TextPrimary.copy(alpha = 0.65f),
                    fontStyle = FontStyle.Italic
                )
            }

            // Scrollable row: min width = screen width so Arrangement.End right-aligns narrow
            // content; wider content scrolls and LaunchedEffect keeps the right end visible.
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val minRowWidth = maxWidth
                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .widthIn(min = minRowWidth),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    when {
                        op == null && result == null -> {
                            MixedDisplay(left, DisplaySize.SINGLE, activeSide == ActiveSide.LEFT)
                        }
                        op == null && result != null -> {
                            MixedDisplay(left, DisplaySize.RESULT, false)
                            Spacer(Modifier.width(3.dp))
                            OpText("=", DisplaySize.RESULT.opSize, alpha = 0.55f)
                            Spacer(Modifier.width(4.dp))
                            Box(modifier = Modifier.clickable { onResultTap() }) {
                                ResultDisplay(result)
                            }
                        }
                        op != null && result == null -> {
                            MixedDisplay(left, DisplaySize.EXPR, activeSide == ActiveSide.LEFT)
                            Spacer(Modifier.width(6.dp))
                            OpText(op.symbol, DisplaySize.EXPR.opSize)
                            Spacer(Modifier.width(6.dp))
                            MixedDisplay(right, DisplaySize.EXPR, activeSide == ActiveSide.RIGHT)
                        }
                        op != null && result != null -> {
                            MixedDisplay(left, DisplaySize.RESULT, false)
                            Spacer(Modifier.width(3.dp))
                            OpText(op.symbol, DisplaySize.RESULT.opSize, alpha = 0.55f)
                            Spacer(Modifier.width(3.dp))
                            MixedDisplay(right, DisplaySize.RESULT, false)
                            Spacer(Modifier.width(3.dp))
                            OpText("=", DisplaySize.RESULT.opSize, alpha = 0.55f)
                            Spacer(Modifier.width(4.dp))
                            Box(modifier = Modifier.clickable { onResultTap() }) {
                                ResultDisplay(result)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OpText(symbol: String, size: TextUnit, alpha: Float = 1f) {
    Text(
        symbol,
        fontSize = size,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        color = TextPrimary.copy(alpha = alpha)
    )
}

@Composable
internal fun MixedDisplay(input: MixedInput, size: DisplaySize, highlight: Boolean) {
    val alpha = if (highlight) 1f else 0.72f
    val color = TextPrimary.copy(alpha = alpha)
    val showWhole = input.whole.isNotEmpty() || (input.num.isEmpty() && input.den.isEmpty())

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (showWhole) {
            Text(
                text = input.whole.ifEmpty { "0" },
                fontSize = size.wholeSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = if (input.whole.isEmpty()) color.copy(alpha = color.alpha * 0.4f) else color
            )
        }
        if (input.num.isNotEmpty() || input.den.isNotEmpty()) {
            if (showWhole) Spacer(Modifier.width(4.dp))
            // IntrinsicSize.Max makes the column as wide as its widest child,
            // so the fraction bar matches the text rather than filling the screen.
            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    input.num.ifEmpty { "0" },
                    fontSize = size.fracSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = color
                )
                Spacer(
                    modifier = Modifier
                        .height(size.barHeight)
                        .fillMaxWidth()
                        .background(color)
                )
                Text(
                    input.den.ifEmpty { "1" },
                    fontSize = size.fracSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = color
                )
            }
        }
    }
}

@Composable
fun ResultDisplay(result: Fraction) {
    val (whole, num, den) = result.toMixed()
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (whole != 0L || num == 0L) {
            Text(
                text = whole.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = TextPrimary
            )
        }
        if (num != 0L) {
            Spacer(Modifier.width(4.dp))
            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(num.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = TextPrimary)
                Spacer(modifier = Modifier.height(1.5.dp).fillMaxWidth().background(TextPrimary))
                Text(den.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = TextPrimary)
            }
        }
    }
}
