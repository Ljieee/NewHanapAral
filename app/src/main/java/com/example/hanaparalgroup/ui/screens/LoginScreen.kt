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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { contentVisible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink50)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Top section — dark header ────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Ink900)
                    .padding(top = 72.dp, bottom = 52.dp),
                contentAlignment = Alignment.Center
            ) {
                this@Column.AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(
                        initialOffsetY = { -32 },
                        animationSpec = tween(600, easing = FastOutSlowInEasing)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Logo mark
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(White, RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Ink900,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = "HanapAral",
                            style = MaterialTheme.typography.headlineLarge,
                            color = White,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "GROUP",
                            style = MaterialTheme.typography.labelMedium,
                            color = White.copy(alpha = 0.4f),
                            letterSpacing = 5.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Your cloud-based study companion",
                            style = MaterialTheme.typography.bodySmall,
                            color = White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // ── Card section ─────────────────────────────────────────────────
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(600, delayMillis = 150)) + slideInVertically(
                    initialOffsetY = { 60 },
                    animationSpec = tween(600, delayMillis = 150, easing = FastOutSlowInEasing)
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp)
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, Ink200)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome back",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Ink900,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Sign in to access your study groups",
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink400,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(28.dp))

                        // Google Sign-In Button
                        GoogleSignInButton(
                            onClick = {
                                isLoading = true
                                onLoginSuccess()
                            },
                            isLoading = isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))
                        LabeledDivider(label = "OR")
                        Spacer(Modifier.height(24.dp))

                        // Feature highlights
                        FeatureHighlight(icon = Icons.Default.Groups, text = "Join & create study groups instantly")
                        Spacer(Modifier.height(10.dp))
                        FeatureHighlight(icon = Icons.Default.Notifications, text = "Real-time notifications for your groups")
                        Spacer(Modifier.height(10.dp))
                        FeatureHighlight(icon = Icons.Default.CloudSync, text = "Everything synced across your devices")

                        Spacer(Modifier.height(28.dp))

                        Text(
                            text = "By signing in, you agree to our Terms of Service and Privacy Policy.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink300,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "University of Cabuyao · CCS",
                style = MaterialTheme.typography.labelSmall,
                color = Ink300
            )
            Spacer(Modifier.height(32.dp))
        }

        if (isLoading) LoadingOverlay(message = "Signing you in...")
    }
}

@Composable
private fun GoogleSignInButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Ink200),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White,
            contentColor = Ink900
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Ink900,
                strokeWidth = 2.dp
            )
        } else {
            // Google G
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Ink100, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "G",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink900,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Continue with Google",
                style = MaterialTheme.typography.labelLarge,
                color = Ink900,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FeatureHighlight(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Ink100, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Ink700, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Ink600,
            modifier = Modifier.weight(1f)
        )
    }
}