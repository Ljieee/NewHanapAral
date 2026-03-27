package com.example.hanaparalgroup.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    var logoVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    var taglineVisible by remember { mutableStateOf(false) }
    var dotsVisible by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.6f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 280f),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "logoAlpha"
    )

    LaunchedEffect(Unit) {
        delay(180)
        logoVisible = true
        delay(380)
        textVisible = true
        delay(280)
        taglineVisible = true
        delay(180)
        dotsVisible = true
        delay(1600)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink900)
    ) {
        // Subtle dot-grid texture accent (top-right)
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 60.dp, y = (-60).dp)
                .alpha(0.06f)
                .background(White, RoundedCornerShape(40.dp))
                .align(Alignment.TopEnd)
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = (-40).dp, y = 40.dp)
                .alpha(0.04f)
                .background(White, CircleShape)
                .align(Alignment.BottomStart)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .background(White, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Ink900,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(Modifier.height(36.dp))

            AnimatedVisibility(
                visible = textVisible,
                enter = fadeIn(tween(500)) + slideInVertically(
                    initialOffsetY = { 24 },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "HanapAral",
                        style = MaterialTheme.typography.displaySmall,
                        color = White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "GROUP",
                        style = MaterialTheme.typography.labelLarge,
                        color = White.copy(alpha = 0.4f),
                        letterSpacing = 6.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            AnimatedVisibility(
                visible = taglineVisible,
                enter = fadeIn(tween(500))
            ) {
                Text(
                    text = "Connect. Study. Succeed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = White.copy(alpha = 0.45f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.3.sp
                )
            }

            Spacer(Modifier.height(56.dp))

            AnimatedVisibility(
                visible = dotsVisible,
                enter = fadeIn(tween(400))
            ) {
                LoadingDots()
            }
        }

        Text(
            text = "v1.0.0 · University of Cabuyao",
            style = MaterialTheme.typography.labelSmall,
            color = White.copy(alpha = 0.2f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val offsets = (0..2).map { idx ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -8f,
            animationSpec = infiniteRepeatable(
                animation = tween(380, delayMillis = idx * 126, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot$idx"
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        offsets.forEach { offset ->
            Box(
                modifier = Modifier
                    .offset(y = offset.value.dp)
                    .size(6.dp)
                    .background(White.copy(alpha = 0.35f), CircleShape)
            )
        }
    }
}