package com.dq.fractioncalculator.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dq.fractioncalculator.math.Step
import com.dq.fractioncalculator.ui.theme.TextPrimary
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsSheet(
    steps: List<Step>,
    onDismiss: () -> Unit
) {
    val equationStep = steps.filterIsInstance<Step.ShowEquation>().firstOrNull()
    val middleSteps = steps.filter { it !is Step.ShowEquation && it !is Step.FinalResult }
    val finalStep = steps.filterIsInstance<Step.FinalResult>().firstOrNull()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)) {
            GridPaperBackground(modifier = Modifier.matchParentSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // ── EQUATION ────────────────────────────────────────────
                SheetSection("Equation") {
                    equationStep?.let { s ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 18.dp, top = 10.dp, bottom = 16.dp)
                        ) {
                            SheetMixed(s.left.whole, s.left.num, s.left.den)
                            Text(
                                "  ${s.op.symbol}  ",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = TextPrimary
                            )
                            SheetMixed(s.right.whole, s.right.num, s.right.den)
                        }
                    }
                }

                SectionDivider()

                // ── STEPS ────────────────────────────────────────────────
                SheetSection("Steps") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 18.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        middleSteps.forEach { step ->
                            Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SheetStepContent(step)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

                SectionDivider()

                // ── SOLUTION ─────────────────────────────────────────────
                SheetSection("Solution") {
                    finalStep?.let { s ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 18.dp, top = 10.dp, bottom = 16.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "=",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = TextPrimary
                                )
                                Spacer(Modifier.width(8.dp))
                                SheetSolution(s.whole, s.num, s.den)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "(${formatDecimal(s.decimal)})",
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic,
                                color = TextPrimary.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, bottom = 16.dp, top = 6.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF555555))
                    ) {
                        Text("Close", color = Color.White)
                    }
                }
            }
        }
    }
}

// ── Section helpers ──────────────────────────────────────────────────────────

@Composable
private fun SheetSection(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 6.dp),
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = TextPrimary.copy(alpha = 0.5f)
        )
        HorizontalDivider(thickness = 0.5.dp, color = TextPrimary.copy(alpha = 0.2f))
        content()
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(thickness = 1.dp, color = TextPrimary.copy(alpha = 0.4f))
}

// ── Equation display ─────────────────────────────────────────────────────────

@Composable
private fun SheetMixed(whole: Long, num: Long, den: Long) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (whole != 0L || num == 0L) {
            Text(
                whole.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = TextPrimary
            )
        }
        if (num != 0L) {
            if (whole != 0L) Spacer(Modifier.width(3.dp))
            FracColumn(num.toString(), den.toString(), textSize = 17)
        }
    }
}

// ── Step content ─────────────────────────────────────────────────────────────

@Composable
private fun SheetStepContent(step: Step) {
    when (step) {
        is Step.ConvertToImproper -> {
            SheetFracExpr(step.left.wholeExpr, step.left.den)
            StepOpText(step.op.symbol)
            SheetFracExpr(step.right.wholeExpr, step.right.den)
        }
        is Step.SimplifiedImproper -> {
            SheetFrac(step.left.num, step.left.den)
            StepOpText(step.op.symbol)
            SheetFrac(step.right.num, step.right.den)
        }
        is Step.CommonDenom -> {
            SheetFracExpr("${step.left.num}×${step.left.scale}", step.left.den)
            StepOpText(step.op.symbol)
            SheetFracExpr("${step.right.num}×${step.right.scale}", step.right.den)
        }
        is Step.AfterScale -> {
            SheetFrac(step.left.num, step.left.den)
            StepOpText(step.op.symbol)
            SheetFrac(step.right.num, step.right.den)
        }
        is Step.CombinedNumerator -> {
            val rAbs = abs(step.right)
            val expr = "${step.left}${if (step.right < 0) "−$rAbs" else "+${step.right}"}"
            SheetFracExpr(expr, step.den)
        }
        is Step.FracExprStep -> FracColumn(step.numExpr, step.denExpr, textSize = 16)
        is Step.SingleFraction -> SheetFrac(step.num, step.den)
        is Step.BackToMixed -> SheetFracExpr("${step.whole}×${step.den}+${step.rem}", step.den)
        else -> Unit
    }
}

@Composable
private fun StepOpText(symbol: String) {
    Text(
        "  $symbol  ",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        color = TextPrimary
    )
}

@Composable
private fun SheetFrac(num: Long, den: Long) = FracColumn(num.toString(), den.toString(), textSize = 20)

@Composable
private fun SheetFracExpr(expr: String, den: Long) = FracColumn(expr, den.toString(), textSize = 16)

@Composable
private fun FracColumn(top: String, bottom: String, textSize: Int) {
    Column(
        modifier = Modifier.width(IntrinsicSize.Max),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(top, fontSize = textSize.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = TextPrimary)
        Spacer(modifier = Modifier.height(1.5.dp).fillMaxWidth().background(TextPrimary))
        Text(bottom, fontSize = textSize.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = TextPrimary)
    }
}

// ── Solution display ─────────────────────────────────────────────────────────

@Composable
private fun SheetSolution(whole: Long, num: Long, den: Long) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (whole != 0L || num == 0L) {
            Text(
                whole.toString(),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = TextPrimary
            )
        }
        if (num != 0L) {
            Spacer(Modifier.width(5.dp))
            FracColumn(num.toString(), den.toString(), textSize = 24)
        }
    }
}

private fun formatDecimal(d: Double): String =
    String.format(Locale.US, "%.5f", d).trimEnd('0').trimEnd('.')

// ── Background ───────────────────────────────────────────────────────────────

@Composable
fun GridPaperBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(Color(0xFFF4F4F0))) {
        val gridSize = 18.dp.toPx()
        val lineColor = Color(0xFFDDDDCC)
        var x = 0f
        while (x <= size.width) {
            drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 0.7f)
            x += gridSize
        }
        var y = 0f
        while (y <= size.height) {
            drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 0.7f)
            y += gridSize
        }
    }
}
