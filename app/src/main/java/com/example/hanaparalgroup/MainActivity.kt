package com.example.hanaparalgroup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.hanaparalgroup.ui.navigation.HanapAralNavGraph
import com.example.hanaparalgroup.ui.theme.HanapAralGroupTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HanapAralGroupTheme {
                HanapAralNavGraph()
            }
        }
    }
}