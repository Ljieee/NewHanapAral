package com.example.hanaparalgroup.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun BiometricGateScreen(
    onAuthSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var authState by remember { mutableStateOf(BiometricState.IDLE) }

    // Ring animation
    val infiniteTransition = rememberInfiniteTransition(label = "biometric")
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringAlpha"
    )

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "Superuser Access",
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                "Biometric Authentication",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Only authorized superusers can access remote configuration controls.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(60.dp))

            // Fingerprint visual
            Box(contentAlignment = Alignment.Center) {
                // Outer pulsing ring
                if (authState == BiometricState.IDLE) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(ringScale)
                            .alpha(ringAlpha)
                            .background(Brand.copy(alpha = 0.12f), CircleShape)
                    )
                }

                // Middle ring
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(
                            when (authState) {
                                BiometricState.SUCCESS -> Success.copy(alpha = 0.15f)
                                BiometricState.FAILED  -> Alert.copy(alpha = 0.15f)
                                else                   -> Brand.copy(alpha = 0.08f)
                            },
                            CircleShape
                        )
                )

                // Inner button
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            when (authState) {
                                BiometricState.SUCCESS -> Success
                                BiometricState.FAILED  -> Alert
                                BiometricState.SCANNING -> Action
                                else                   -> Brand
                            },
                            CircleShape
                        )
                        .clickable {
                            if (authState == BiometricState.IDLE || authState == BiometricState.FAILED) {
                                authState = BiometricState.SCANNING
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (authState) {
                            BiometricState.SUCCESS -> Icons.Default.CheckCircle
                            BiometricState.FAILED  -> Icons.Default.Cancel
                            else                   -> Icons.Default.Fingerprint
                        },
                        contentDescription = "Fingerprint",
                        tint = Surface,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Status label
            AnimatedContent(
                targetState = authState,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                },
                label = "status"
            ) { state ->
                when (state) {
                    BiometricState.IDLE -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Tap the fingerprint icon",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                        Text(
                            "to authenticate",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextHint
                        )
                    }
                    BiometricState.SCANNING -> {
                        LaunchedEffect(Unit) {
                            delay(1800)
                            // Galang: real BiometricPrompt logic here
                            authState = BiometricState.SUCCESS
                            delay(800)
                            onAuthSuccess()
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Action, strokeWidth = 3.dp, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("Scanning fingerprint…", style = MaterialTheme.typography.titleMedium, color = Action)
                        }
                    }
                    BiometricState.SUCCESS -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Authentication successful!", style = MaterialTheme.typography.titleMedium, color = Success, fontWeight = FontWeight.Bold)
                        Text("Redirecting to Superuser panel…", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    BiometricState.FAILED -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Authentication failed", style = MaterialTheme.typography.titleMedium, color = Alert, fontWeight = FontWeight.Bold)
                        Text("Tap to try again", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            // Use PIN alternative
            OutlinedSecondaryButton(
                text = "Use PIN Instead",
                onClick = { /* Galang: fallback auth */ },
                icon = Icons.Default.Pin,
                modifier = Modifier.fillMaxWidth(0.65f)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Protected by Android Biometric API",
                style = MaterialTheme.typography.labelSmall,
                color = TextHint
            )
        }
    }
}

private enum class BiometricState { IDLE, SCANNING, SUCCESS, FAILED }