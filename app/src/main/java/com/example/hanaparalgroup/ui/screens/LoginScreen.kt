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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        contentVisible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Background ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to BrandDark,
                            0.45f to Brand,
                            1.0f to Background
                        )
                    )
                )
        )

        // Decorative wave overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                color = Surface.copy(alpha = 0.06f),
                radius = w * 0.7f,
                center = Offset(w * 1.1f, h * 0.25f)
            )
            drawCircle(
                color = Action.copy(alpha = 0.08f),
                radius = w * 0.5f,
                center = Offset(-w * 0.15f, h * 0.35f)
            )
        }

        // ── Scrollable content ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero section
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(700)) + slideInVertically(
                    initialOffsetY = { -60 },
                    animationSpec = tween(700, easing = EaseOutCubic)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 72.dp, bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(Surface.copy(alpha = 0.15f), RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(66.dp)
                                .background(Surface, RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Brand,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "HanapAral",
                        style = MaterialTheme.typography.displaySmall,
                        color = Surface,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "GROUP",
                        style = MaterialTheme.typography.labelLarge,
                        color = ActionLight,
                        letterSpacing = 5.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Your cloud-based study companion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Surface.copy(alpha = 0.7f)
                    )
                }
            }

            // ── Card section ─────────────────────────────────────────────────
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(700, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(700, delayMillis = 200, easing = EaseOutCubic)
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome Back!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Sign in to access your study groups",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(36.dp))

                        // Google Sign-In Button (styled)
                        GoogleSignInButton(
                            onClick = {
                                isLoading = true
                                // Auth logic by Galang
                                // Simulated for UI only:
                                onLoginSuccess()
                            },
                            isLoading = isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))

                        LabeledDivider(label = "OR")

                        Spacer(Modifier.height(24.dp))

                        // Feature highlights
                        FeatureHighlight(
                            icon = Icons.Default.Groups,
                            text = "Join & create study groups instantly"
                        )
                        Spacer(Modifier.height(12.dp))
                        FeatureHighlight(
                            icon = Icons.Default.Notifications,
                            text = "Real-time notifications for your groups"
                        )
                        Spacer(Modifier.height(12.dp))
                        FeatureHighlight(
                            icon = Icons.Default.CloudSync,
                            text = "Everything synced across your devices"
                        )

                        Spacer(Modifier.height(32.dp))

                        Text(
                            text = "By signing in, you agree to our Terms of Service and Privacy Policy.",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextHint,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // Footer
            Text(
                text = "University of Cabuyao · CCS",
                style = MaterialTheme.typography.labelSmall,
                color = Surface.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(24.dp))
        }

        // Loading overlay
        if (isLoading) {
            LoadingOverlay(message = "Signing you in...")
        }
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
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, Divider),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Surface,
            contentColor = TextPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Brand,
                strokeWidth = 2.dp
            )
        } else {
            // Google "G" logo approximation with colored box
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        Brush.sweepGradient(
                            listOf(
                                Color(0xFF4285F4), Color(0xFF34A853),
                                Color(0xFFFBBC05), Color(0xFFEA4335), Color(0xFF4285F4)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "G",
                    style = MaterialTheme.typography.labelLarge,
                    color = Surface,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Continue with Google",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FeatureHighlight(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Action.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Action, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.weight(1f))
    }
}

private val EaseOutCubic = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)