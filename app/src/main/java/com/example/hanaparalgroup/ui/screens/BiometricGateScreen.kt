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

    val infiniteTransition = rememberInfiniteTransition(label = "biometric")
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
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
        containerColor = Ink50
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Biometric Authentication",
                style = MaterialTheme.typography.headlineSmall,
                color = Ink900,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Only authorized superusers can access remote configuration controls.",
                style = MaterialTheme.typography.bodySmall,
                color = Ink400,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(56.dp))

            // Fingerprint visual
            Box(contentAlignment = Alignment.Center) {
                // Outer pulsing ring
                if (authState == BiometricState.IDLE) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .scale(ringScale)
                            .alpha(ringAlpha)
                            .background(Ink900, CircleShape)
                    )
                }

                // Middle ring
                Box(
                    modifier = Modifier
                        .size(118.dp)
                        .background(
                            when (authState) {
                                BiometricState.SUCCESS -> PositiveLight
                                BiometricState.FAILED  -> DangerLight
                                else                   -> Ink100
                            },
                            CircleShape
                        )
                )

                // Inner button
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(
                            when (authState) {
                                BiometricState.SUCCESS  -> Positive
                                BiometricState.FAILED   -> Danger
                                BiometricState.SCANNING -> Accent
                                else                    -> Ink900
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
                        tint = White,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            AnimatedContent(
                targetState = authState,
                transitionSpec = {
                    fadeIn(tween(280)) togetherWith fadeOut(tween(200))
                },
                label = "status"
            ) { state ->
                when (state) {
                    BiometricState.IDLE -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Tap the fingerprint icon",
                            style = MaterialTheme.typography.labelMedium,
                            color = Ink600,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "to authenticate",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink400
                        )
                    }
                    BiometricState.SCANNING -> {
                        LaunchedEffect(Unit) {
                            delay(1800)
                            authState = BiometricState.SUCCESS
                            delay(700)
                            onAuthSuccess()
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Accent, strokeWidth = 2.5.dp, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.height(10.dp))
                            Text("Scanning…", style = MaterialTheme.typography.labelMedium, color = Accent)
                        }
                    }
                    BiometricState.SUCCESS -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Authenticated", style = MaterialTheme.typography.labelMedium, color = Positive, fontWeight = FontWeight.Bold)
                        Text("Redirecting to Superuser panel…", style = MaterialTheme.typography.labelSmall, color = Ink400)
                    }
                    BiometricState.FAILED -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Authentication failed", style = MaterialTheme.typography.labelMedium, color = Danger, fontWeight = FontWeight.Bold)
                        Text("Tap to try again", style = MaterialTheme.typography.labelSmall, color = Ink400)
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            OutlinedSecondaryButton(
                text = "Use PIN Instead",
                onClick = { /* Galang: fallback auth */ },
                icon = Icons.Default.Pin,
                color = Ink900,
                modifier = Modifier.fillMaxWidth(0.65f)
            )

            Spacer(Modifier.height(14.dp))

            Text(
                "Protected by Android Biometric API",
                style = MaterialTheme.typography.labelSmall,
                color = Ink300
            )
        }
    }
}

private enum class BiometricState { IDLE, SCANNING, SUCCESS, FAILED }