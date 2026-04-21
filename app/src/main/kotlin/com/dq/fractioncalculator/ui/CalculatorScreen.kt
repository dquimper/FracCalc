package com.dq.fractioncalculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dq.fractioncalculator.state.CalculatorViewModel
import com.dq.fractioncalculator.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(vm: CalculatorViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(ChromeGradientStart, ChromeGradientEnd)))
            .statusBarsPadding()
    ) {
        TopBar(onHistoryClick = { vm.showHistory = true })

        Display(
            left = vm.left,
            right = vm.right,
            op = vm.op,
            result = vm.result,
            resultFormat = vm.resultFormat,
            activeSide = vm.activeSide,
            onResultTap = { vm.showSteps = true },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(4.dp))

        Keypad(
            onWholeDigit = vm::onWholeDigit,
            onNumDigit = vm::onNumDigit,
            onDenDigit = vm::onDenDigit,
            onWholeBackspace = vm::onWholeBackspace,
            onNumBackspace = vm::onNumBackspace,
            onDenBackspace = vm::onDenBackspace,
            onClear = vm::onClear,
            onSignToggle = vm::onSignToggle,
            onDecimalPoint = vm::onDecimalPoint,
            onOp = vm::onOp,
            onEquals = vm::onEquals,
            modifier = Modifier.weight(1f).navigationBarsPadding()
        )
    }

    if (vm.showSteps && vm.steps.isNotEmpty()) {
        StepsSheet(steps = vm.steps, onDismiss = { vm.showSteps = false })
    }

    if (vm.showHistory) {
        HistorySheet(
            history = vm.history,
            onTap = { entry ->
                vm.onHistoryTap(entry)
                vm.showHistory = false
            },
            onDismiss = { vm.showHistory = false }
        )
    }
}

@Composable
fun TopBar(onHistoryClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(TopBarBg)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onHistoryClick) {
            Icon(Icons.Default.History, contentDescription = "history", tint = TextPrimary)
        }
        Spacer(Modifier.weight(1f))
        val title = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Normal, fontSize = 20.sp)) { append("FRACTION") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)) { append("PLUS") }
        }
        Text(text = title, fontFamily = FontFamily.SansSerif, color = TextPrimary)
        Spacer(Modifier.width(48.dp))
    }
}
