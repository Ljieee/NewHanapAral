package com.example.hanaparalgroup.ui.screens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*

@Composable
fun BiometricGateScreen(
    onAuthSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var authState by remember { mutableStateOf(BiometricAuthState.IDLE) }
    val context = LocalContext.current

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

    // Navigate on success
    LaunchedEffect(authState) {
        if (authState == BiometricAuthState.SUCCESS) {
            kotlinx.coroutines.delay(800)
            onAuthSuccess()
        }
    }

    fun launchBiometric() {
        val activity = context as? FragmentActivity ?: return
        authState = BiometricAuthState.SCANNING

        val executor = ContextCompat.getMainExecutor(context)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    authState = BiometricAuthState.SUCCESS
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    authState = BiometricAuthState.FAILED
                }
                override fun onAuthenticationFailed() {
                    authState = BiometricAuthState.FAILED
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Superuser Authentication")
            .setSubtitle("Verify your identity to access the Superuser panel")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            .build()

        prompt.authenticate(promptInfo)
    }

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

            Box(contentAlignment = Alignment.Center) {
                if (authState == BiometricAuthState.IDLE) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .scale(ringScale)
                            .alpha(ringAlpha)
                            .background(Ink900, CircleShape)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(118.dp)
                        .background(
                            when (authState) {
                                BiometricAuthState.SUCCESS -> PositiveLight
                                BiometricAuthState.FAILED  -> DangerLight
                                else                       -> Ink100
                            },
                            CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(
                            when (authState) {
                                BiometricAuthState.SUCCESS  -> Positive
                                BiometricAuthState.FAILED   -> Danger
                                BiometricAuthState.SCANNING -> Accent
                                else                        -> Ink900
                            },
                            CircleShape
                        )
                        .clickable {
                            if (authState == BiometricAuthState.IDLE || authState == BiometricAuthState.FAILED) {
                                launchBiometric()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (authState) {
                            BiometricAuthState.SUCCESS -> Icons.Default.CheckCircle
                            BiometricAuthState.FAILED  -> Icons.Default.Cancel
                            else                       -> Icons.Default.Fingerprint
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
                label = "status"
            ) { state ->
                when (state) {
                    BiometricAuthState.IDLE -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Tap the fingerprint icon to authenticate",
                            style = MaterialTheme.typography.labelMedium,
                            color = Ink400,
                            textAlign = TextAlign.Center
                        )
                    }
                    BiometricAuthState.SCANNING -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Accent, strokeWidth = 2.5.dp, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.height(10.dp))
                        Text("Scanning…", style = MaterialTheme.typography.labelMedium, color = Accent)
                    }
                    BiometricAuthState.SUCCESS -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Authenticated", style = MaterialTheme.typography.labelMedium, color = Positive, fontWeight = FontWeight.Bold)
                        Text("Redirecting to Superuser panel…", style = MaterialTheme.typography.labelSmall, color = Ink400)
                    }
                    BiometricAuthState.FAILED -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Authentication failed", style = MaterialTheme.typography.labelMedium, color = Danger, fontWeight = FontWeight.Bold)
                        Text("Tap to try again", style = MaterialTheme.typography.labelSmall, color = Ink400)
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            OutlinedSecondaryButton(
                text = "Cancel",
                onClick = onNavigateBack,
                icon = Icons.Default.Close,
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

private enum class BiometricAuthState { IDLE, SCANNING, SUCCESS, FAILED }