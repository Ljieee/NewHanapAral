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
import androidx.compose.ui.geometry.Offset
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
    // Animation states
    var logoVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    var taglineVisible by remember { mutableStateOf(false) }
    var dotsVisible by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.3f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "logoAlpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        logoVisible = true
        delay(400)
        textVisible = true
        delay(300)
        taglineVisible = true
        delay(200)
        dotsVisible = true
        delay(1500)
        // In production: check auth state; for now go to login
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BrandDark, Brand, GradientEnd),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-80).dp)
                .background(Surface.copy(alpha = 0.05f), CircleShape)
                .align(Alignment.TopStart)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 60.dp, y = 40.dp)
                .background(Action.copy(alpha = 0.1f), CircleShape)
                .align(Alignment.BottomEnd)
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = (-20).dp, y = (-20).dp)
                .background(Surface.copy(alpha = 0.08f), CircleShape)
                .align(Alignment.BottomStart)
        )

        // Main content
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo container
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .background(Surface.copy(alpha = 0.15f), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Surface.copy(alpha = 0.9f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Brand,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // App name
            AnimatedVisibility(
                visible = textVisible,
                enter = fadeIn(tween(500)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(500)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "HanapAral",
                        style = MaterialTheme.typography.displayMedium,
                        color = Surface,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "GROUP",
                        style = MaterialTheme.typography.labelLarge,
                        color = ActionLight,
                        letterSpacing = 6.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tagline
            AnimatedVisibility(
                visible = taglineVisible,
                enter = fadeIn(tween(600))
            ) {
                Text(
                    text = "Connect. Study. Succeed.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Surface.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(60.dp))

            // Loading dots
            AnimatedVisibility(
                visible = dotsVisible,
                enter = fadeIn(tween(400))
            ) {
                LoadingDots()
            }
        }

        // Version at bottom
        Text(
            text = "v1.0.0 · University of Cabuyao",
            style = MaterialTheme.typography.labelSmall,
            color = Surface.copy(alpha = 0.4f),
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
            targetValue = -12f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, delayMillis = idx * 133, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot$idx"
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        offsets.forEach { offset ->
            Box(
                modifier = Modifier
                    .offset(y = offset.value.dp)
                    .size(8.dp)
                    .background(Surface.copy(alpha = 0.7f), CircleShape)
            )
        }
    }
}