package com.dq.fractioncalculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dq.fractioncalculator.math.Fraction
import com.dq.fractioncalculator.state.HistoryEntry
import com.dq.fractioncalculator.state.MixedInput
import com.dq.fractioncalculator.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(
    history: List<HistoryEntry>,
    onTap: (HistoryEntry) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            "History",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        HorizontalDivider()
        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No calculations yet", color = TextPrimary.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(history) { entry ->
                    HistoryItem(entry = entry, onTap = { onTap(entry) })
                    HorizontalDivider(color = Color(0xFFDDDDDD))
                }
            }
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
fun HistoryItem(entry: HistoryEntry, onTap: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HistoryMixed(entry.left)
        Text(" ${entry.op.symbol} ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        HistoryMixed(entry.right)
        Text(" = ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        HistoryResult(entry.result)
    }
}

@Composable
fun HistoryMixed(input: MixedInput) {
    val whole = input.whole.ifEmpty { "0" }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(whole, fontSize = 14.sp, color = TextPrimary)
        if (input.num.isNotEmpty() || input.den.isNotEmpty()) {
            Text(
                " ${input.num.ifEmpty { "0" }}/${input.den.ifEmpty { "1" }}",
                fontSize = 12.sp,
                color = TextPrimary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun HistoryResult(result: Fraction) {
    val (whole, num, den) = result.toMixed()
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (num == 0L) {
            Text(whole.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        } else {
            if (whole != 0L) Text(whole.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(" $num/$den", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}
