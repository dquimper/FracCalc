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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dq.fractioncalculator.math.Step
import com.dq.fractioncalculator.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsSheet(
    steps: List<Step>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)) {
            GridPaperBackground(modifier = Modifier.matchParentSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                steps.forEach { step ->
                    StepRow(step)
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF555555))
                ) {
                    Text("Close", color = Color.White)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

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

@Composable
fun StepRow(step: Step) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (step) {
            is Step.ShowEquation -> {
                Text("Equation", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f))
                InlineMixed(step.left.whole, step.left.num, step.left.den)
                Text(" ${step.op.symbol} ", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                InlineMixed(step.right.whole, step.right.num, step.right.den)
            }
            is Step.ConvertToImproper -> {
                Spacer(Modifier.weight(1f))
                InlineFracExpr(step.left.wholeExpr, step.left.den)
                Text(" ${step.op.symbol} ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                InlineFracExpr(step.right.wholeExpr, step.right.den)
            }
            is Step.SimplifiedImproper -> {
                Spacer(Modifier.weight(1f))
                InlineFrac(step.left.num, step.left.den)
                Text(" ${step.op.symbol} ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                InlineFrac(step.right.num, step.right.den)
            }
            is Step.CommonDenom -> {
                Spacer(Modifier.weight(1f))
                val l = step.left
                val r = step.right
                InlineFracExpr("${l.num}×${l.scale}", l.den)
                Text(" ${step.op.symbol} ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                InlineFracExpr("${r.num}×${r.scale}", r.den)
            }
            is Step.AfterScale -> {
                Spacer(Modifier.weight(1f))
                InlineFrac(step.left.num, step.left.den)
                Text(" ${step.op.symbol} ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                InlineFrac(step.right.num, step.right.den)
            }
            is Step.CombinedNumerator -> {
                Spacer(Modifier.weight(1f))
                val opSym = if (step.right < 0) "−" else step.op.symbol
                val rAbs = kotlin.math.abs(step.right)
                InlineFracExpr("${step.left}${if (step.right < 0) "−$rAbs" else "+${step.right}"}", step.den)
            }
            is Step.SingleFraction -> {
                Spacer(Modifier.weight(1f))
                InlineFrac(step.num, step.den)
            }
            is Step.BackToMixed -> {
                Spacer(Modifier.weight(1f))
                InlineFracExpr("${step.whole}×${step.den}+${step.rem}", step.den)
            }
            is Step.FinalResult -> {
                Text("Steps", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f))
                val (w, n, d) = Triple(step.whole, step.num, step.den)
                if (n != 0L) {
                    Text(w.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.width(4.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(n.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        HorizontalDivider(thickness = 1.5.dp, color = TextPrimary, modifier = Modifier.width(24.dp))
                        Text(d.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                } else {
                    Text(w.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun InlineMixed(whole: Long, num: Long, den: Long) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (whole != 0L || num == 0L) {
            Text(whole.toString(), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        if (num != 0L) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(num.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                HorizontalDivider(thickness = 1.dp, color = TextPrimary, modifier = Modifier.width(20.dp))
                Text(den.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}

@Composable
fun InlineFrac(num: Long, den: Long) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(num.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        HorizontalDivider(thickness = 1.5.dp, color = TextPrimary, modifier = Modifier.width(32.dp))
        Text(den.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
fun InlineFracExpr(expr: String, den: Long) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(expr, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontStyle = FontStyle.Normal)
        HorizontalDivider(thickness = 1.5.dp, color = TextPrimary, modifier = Modifier.widthIn(min = 24.dp))
        Text(den.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
