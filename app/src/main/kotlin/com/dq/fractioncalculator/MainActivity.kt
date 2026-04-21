package com.dq.fractioncalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.dq.fractioncalculator.ui.CalculatorScreen
import com.dq.fractioncalculator.ui.theme.FractionCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FractionCalculatorTheme {
                CalculatorScreen()
            }
        }
    }
}
