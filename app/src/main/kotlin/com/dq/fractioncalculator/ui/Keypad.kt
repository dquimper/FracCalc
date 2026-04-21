package com.dq.fractioncalculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dq.fractioncalculator.math.Op
import com.dq.fractioncalculator.ui.theme.*

@Composable
fun CalcButton(
    label: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bgColor: Color = ButtonDigit,
    fontSize: TextUnit = 26.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    content: @Composable (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = modifier
            .shadow(2.dp, shape)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        bgColor.copy(red = (bgColor.red * 1.08f).coerceAtMost(1f), green = (bgColor.green * 1.08f).coerceAtMost(1f), blue = (bgColor.blue * 1.08f).coerceAtMost(1f)),
                        bgColor.copy(red = bgColor.red * 0.9f, green = bgColor.green * 0.9f, blue = bgColor.blue * 0.9f)
                    )
                )
            )
            .border(0.5.dp, Color.White.copy(alpha = 0.4f), shape)
            .clickable(onClick = onClick)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (content != null) content()
        else Text(label, fontSize = fontSize, fontWeight = fontWeight, color = TextPrimary)
    }
}

@Composable
fun BackspaceButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    CalcButton(onClick = onClick, modifier = modifier, bgColor = ButtonDigit) {
        Icon(
            Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "backspace",
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun NumPad3x4(
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9)).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                row.forEach { d ->
                    CalcButton(
                        label = d.toString(),
                        onClick = { onDigit(d) },
                        fontSize = fontSize,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            CalcButton(
                label = "0",
                onClick = { onDigit(0) },
                fontSize = fontSize,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            BackspaceButton(
                onClick = onBackspace,
                modifier = Modifier.weight(2f).fillMaxHeight()
            )
        }
    }
}

@Composable
fun Keypad(
    onWholeDigit: (Int) -> Unit,
    onNumDigit: (Int) -> Unit,
    onDenDigit: (Int) -> Unit,
    onWholeBackspace: () -> Unit,
    onNumBackspace: () -> Unit,
    onDenBackspace: () -> Unit,
    onClear: () -> Unit,
    onSignToggle: () -> Unit,
    onDecimalPoint: () -> Unit,
    onOp: (Op) -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(brush = Brush.verticalGradient(colors = listOf(ChromeGradientStart, ChromeGradientEnd)))
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            LeftColumn(
                onWholeDigit = onWholeDigit,
                onBackspace = onWholeBackspace,
                onClear = onClear,
                onSignToggle = onSignToggle,
                onDecimalPoint = onDecimalPoint,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )

            Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(DividerColor))

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(2f).fillMaxHeight()
            ) {
                NumPad3x4(
                    onDigit = onNumDigit,
                    onBackspace = onNumBackspace,
                    modifier = Modifier.weight(1f),
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(DividerColor))
                Spacer(modifier = Modifier.height(6.dp))
                NumPad3x4(
                    onDigit = onDenDigit,
                    onBackspace = onDenBackspace,
                    modifier = Modifier.weight(1f),
                    fontSize = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OperatorRow(onOp = onOp, onEquals = onEquals)
    }
}

@Composable
fun LeftColumn(
    onWholeDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onSignToggle: () -> Unit,
    onDecimalPoint: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            CalcButton(
                label = "C",
                onClick = onClear,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                bgColor = ButtonClear,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            CalcButton(
                label = "+/−",
                onClick = onSignToggle,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                bgColor = ButtonSign,
                fontSize = 18.sp
            )
        }

        for (row in listOf(listOf(1, 2), listOf(3, 4), listOf(5, 6), listOf(7, 8))) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                row.forEach { d ->
                    CalcButton(
                        label = d.toString(),
                        onClick = { onWholeDigit(d) },
                        fontSize = 36.sp,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            CalcButton(label = "9", onClick = { onWholeDigit(9) }, fontSize = 36.sp, modifier = Modifier.weight(1f).fillMaxHeight())
            CalcButton(label = "0", onClick = { onWholeDigit(0) }, fontSize = 36.sp, modifier = Modifier.weight(1f).fillMaxHeight())
        }

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            CalcButton(label = ".", onClick = onDecimalPoint, fontSize = 36.sp, modifier = Modifier.weight(1f).fillMaxHeight())
            BackspaceButton(onClick = onBackspace, modifier = Modifier.weight(1f).fillMaxHeight())
        }
    }
}

@Composable
fun OperatorRow(
    onOp: (Op) -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        listOf(Op.ADD to "+", Op.SUB to "−", Op.MUL to "×", Op.DIV to "÷").forEach { (op, sym) ->
            CalcButton(
                label = sym,
                onClick = { onOp(op) },
                modifier = Modifier.weight(1f).height(56.dp),
                bgColor = ButtonOperator,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
        CalcButton(
            label = "=",
            onClick = onEquals,
            modifier = Modifier.weight(1f).height(56.dp),
            bgColor = ButtonEquals,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
