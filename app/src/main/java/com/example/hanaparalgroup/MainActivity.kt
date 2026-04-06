package com.example.hanaparalgroup

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.example.hanaparalgroup.ui.navigation.HanapAralNavGraph
import com.example.hanaparalgroup.ui.theme.HanapAralGroupTheme

class MainActivity : FragmentActivity() {
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