package com.example.hanaparalgroup.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.theme.*

// ── Gradient Background ───────────────────────────────────────────────────────
@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Ink50),
        content = content
    )
}

// ── Brand Top App Bar ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HanapAralTopBar(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = White,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = White
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Ink900,
            titleContentColor = White,
            actionIconContentColor = White,
            navigationIconContentColor = White
        )
    )
}

// ── Primary Button ────────────────────────────────────────────────────────────
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Ink900,
            contentColor   = White,
            disabledContainerColor = Ink200,
            disabledContentColor   = Ink400
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = White,
                strokeWidth = 2.dp
            )
        } else {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ── Action (Accent) Button ────────────────────────────────────────────────────
@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Accent,
            contentColor   = White,
            disabledContainerColor = Ink200,
            disabledContentColor   = Ink400
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = White,
                strokeWidth = 2.dp
            )
        } else {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ── Outlined (Secondary) Button ───────────────────────────────────────────────
@Composable
fun OutlinedSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    color: Color = Ink900
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(17.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
    }
}

// ── Branded Text Field ────────────────────────────────────────────────────────
@Composable
fun BrandedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    singleLine: Boolean = true,
    maxLines: Int = 1,
    placeholder: String = "",
    readOnly: Boolean = false,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder, color = Ink300, style = MaterialTheme.typography.bodyMedium) }
            } else null,
            leadingIcon = if (leadingIcon != null) {
                { Icon(leadingIcon, contentDescription = null, tint = if (isError) Danger else Ink400, modifier = Modifier.size(18.dp)) }
            } else null,
            trailingIcon = trailingIcon,
            isError = isError,
            singleLine = singleLine,
            maxLines = maxLines,
            readOnly = readOnly,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Ink900,
                unfocusedBorderColor = Ink200,
                errorBorderColor = Danger,
                focusedLabelColor = Ink900,
                unfocusedLabelColor = Ink400,
                cursorColor = Ink900,
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                disabledContainerColor = Ink50,
                disabledBorderColor = Ink200,
                disabledLabelColor = Ink300,
                disabledTextColor = Ink400
            )
        )
        AnimatedVisibility(visible = isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Danger,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 14.dp, top = 4.dp)
            )
        }
    }
}

// ── Study Group Card ──────────────────────────────────────────────────────────
@Composable
fun StudyGroupCard(
    groupName: String,
    subject: String,
    memberCount: Int,
    maxMembers: Int,
    adminName: String,
    isJoined: Boolean,
    onClick: () -> Unit,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fillRatio = memberCount.toFloat() / maxMembers.toFloat()
    val isFull = fillRatio >= 1f

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Ink200)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = groupName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink900,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subject,
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink400
                    )
                }
                Spacer(Modifier.width(12.dp))
                // Status pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        isJoined -> Ink900
                        isFull   -> Ink100
                        else     -> AccentSoft
                    }
                ) {
                    Text(
                        text = when {
                            isJoined -> "Joined"
                            isFull   -> "Full"
                            else     -> "Open"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            isJoined -> White
                            isFull   -> Ink400
                            else     -> Accent
                        },
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Admin info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Ink300,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = adminName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink400
                )
            }

            Spacer(Modifier.height(14.dp))

            // Member progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Members",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink400
                )
                Text(
                    text = "$memberCount / $maxMembers",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink900,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { fillRatio.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (isFull) Ink300 else Ink900,
                trackColor = Ink100
            )

            if (!isJoined && !isFull) {
                Spacer(Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onJoinClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Ink900),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Ink900)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Join Group", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Notification Item ─────────────────────────────────────────────────────────
@Composable
fun NotificationItem(
    title: String,
    body: String,
    timeAgo: String,
    type: NotificationType,
    isRead: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (iconVec, iconBg) = when (type) {
        NotificationType.NEW_MEMBER   -> Pair(Icons.Default.PersonAdd, Ink100)
        NotificationType.ANNOUNCEMENT -> Pair(Icons.Default.Campaign, Ink100)
        NotificationType.REMINDER     -> Pair(Icons.Default.Alarm, AccentSoft)
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRead) White else AccentSoft
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, if (isRead) Ink200 else Ink200)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon bubble
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconVec, contentDescription = null, tint = Ink700, modifier = Modifier.size(19.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Ink900,
                        fontWeight = if (isRead) FontWeight.Normal else FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    if (!isRead) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(Accent, CircleShape)
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink400,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink300
                )
            }
        }
    }
}

enum class NotificationType { NEW_MEMBER, ANNOUNCEMENT, REMINDER }

// ── Stats Card ────────────────────────────────────────────────────────────────
@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color = Ink900,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Ink200)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Ink100, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Ink700, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = Ink900,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Ink400
            )
        }
    }
}

// ── Section Header ────────────────────────────────────────────────────────────
@Composable
fun SectionHeader(
    title: String,
    action: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Ink900,
            fontWeight = FontWeight.Bold
        )
        if (action != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(action, color = Ink400, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(2.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Ink400, modifier = Modifier.size(14.dp))
            }
        }
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Ink100, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Ink400, modifier = Modifier.size(34.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = Ink900, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Ink400, textAlign = TextAlign.Center)
        if (action != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            PrimaryButton(text = action, onClick = onAction, modifier = Modifier.fillMaxWidth(0.6f))
        }
    }
}

// ── Pulsing Dot Indicator ─────────────────────────────────────────────────────
@Composable
fun PulsingDot(color: Color = Accent, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        modifier = modifier
            .size(8.dp)
            .background(color.copy(alpha = alpha), CircleShape)
    )
}

// ── Chip Tag ──────────────────────────────────────────────────────────────────
@Composable
fun TagChip(
    text: String,
    color: Color = Ink900,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Ink100
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Ink700,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

// ── Loading Overlay ───────────────────────────────────────────────────────────
@Composable
fun LoadingOverlay(message: String = "Loading...") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Ink900, strokeWidth = 2.5.dp, modifier = Modifier.size(28.dp))
                Spacer(Modifier.height(14.dp))
                Text(message, style = MaterialTheme.typography.bodySmall, color = Ink400)
            }
        }
    }
}

// ── Avatar Initials ───────────────────────────────────────────────────────────
@Composable
fun AvatarInitials(
    name: String,
    size: Dp = 44.dp,
    backgroundColor: Color = Ink900,
    textColor: Color = White
) {
    val initials = name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "?" }

    Box(
        modifier = Modifier
            .size(size)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Divider with label ────────────────────────────────────────────────────────
@Composable
fun LabeledDivider(label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Ink200)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Ink300,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Ink200)
    }
}

// ── Remote Config Toggle Row ──────────────────────────────────────────────────
@Composable
fun ConfigToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(label, style = MaterialTheme.typography.titleSmall, color = Ink900, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(description, style = MaterialTheme.typography.bodySmall, color = Ink400)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = Ink900,
                uncheckedThumbColor = Ink400,
                uncheckedTrackColor = Ink100,
                uncheckedBorderColor = Ink200
            )
        )
    }
}

// ── Member Avatar Row ─────────────────────────────────────────────────────────
@Composable
fun MemberAvatarStack(
    names: List<String>,
    maxVisible: Int = 4,
    modifier: Modifier = Modifier
) {
    val visible = names.take(maxVisible)
    val overflow = names.size - maxVisible
    val bgColors = listOf(Ink900, Ink600, Ink400, Ink700)

    Row(modifier = modifier) {
        visible.forEachIndexed { idx, name ->
            Box(modifier = Modifier.offset(x = (-(idx * 10)).dp)) {
                AvatarInitials(
                    name = name,
                    size = 32.dp,
                    backgroundColor = bgColors[idx % bgColors.size]
                )
            }
        }
        if (overflow > 0) {
            Box(modifier = Modifier.offset(x = (-(visible.size * 10)).dp)) {
                AvatarInitials(
                    name = "+$overflow",
                    size = 32.dp,
                    backgroundColor = Ink100,
                    textColor = Ink700
                )
            }
        }
    }
}