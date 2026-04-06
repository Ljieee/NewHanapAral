package com.example.hanaparalgroup.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
//  HanapAral — Monochromatic Design System
//  Philosophy: Near-black on white. One accent. Surgical precision.
// ═══════════════════════════════════════════════════════════════

// ── Ink Scale (the full range of blacks & greys) ────────────────
val Ink900     = Color(0xFF0F0F0F)   // Near-black — primary text, headers
val Ink800     = Color(0xFF1A1A1A)   // Dark — top bars, deep elements
val Ink700     = Color(0xFF2D2D2D)   // Charcoal — secondary headers
val Ink600     = Color(0xFF404040)   // Dark grey — strong labels
val Ink400     = Color(0xFF737373)   // Mid grey — secondary text
val Ink300     = Color(0xFF9E9E9E)   // Light grey — placeholders, hints
val Ink200     = Color(0xFFD4D4D4)   // Subtle — dividers, borders
val Ink100     = Color(0xFFF0F0F0)   // Near-white — chips, alt surfaces
val Ink50      = Color(0xFFF8F8F8)   // Whisper — page background

// ── Pure White ──────────────────────────────────────────────────
val White      = Color(0xFFFFFFFF)   // Card surfaces, buttons

// ── Single Accent (deep blue-teal) ─────────────────────────────
// Used sparingly — unread dots, CTAs, active states only
val Accent     = Color(0xFF1A6BFF)   // Electric blue — primary CTA
val AccentSoft = Color(0xFFE8F0FF)   // Soft blue — accent backgrounds
val AccentDark = Color(0xFF1355D6)   // Pressed / darker variant

// ── Semantic ────────────────────────────────────────────────────
val Positive   = Color(0xFF16A34A)   // Green — success, joined
val PositiveLight = Color(0xFFDCFCE7)
val Danger     = Color(0xFFDC2626)   // Red — errors, destructive
val DangerLight = Color(0xFFFEE2E2)
val Warn       = Color(0xFFCA8A04)   // Amber — warning states
val WarnLight  = Color(0xFFFEF9C3)

// ── Legacy aliases (used by existing screens — mapped to new palette)
val Brand       = Ink800
val BrandDark   = Ink900
val BrandLight  = Ink700
val Action      = Accent
val ActionDark  = AccentDark
val ActionLight = Accent
val Alert       = Danger
val AlertLight  = DangerLight
val Background  = Ink50
val Surface     = White
val SurfaceAlt  = Ink100
val TextPrimary   = Ink900
val TextSecondary = Ink400
val TextHint      = Ink300
val Divider       = Ink200
val Overlay       = Color(0x1A0F0F0F)

// Gradient helpers (kept minimal)
val GradientStart = Ink900
val GradientMid   = Ink800
val GradientEnd   = Ink700

// Success / Warning (semantic)
val Success      = Positive
val SuccessLight = PositiveLight
val Warning      = Warn
val WarningLight = WarnLight

// Shimmer
val ShimmerBase = Ink100
val ShimmerHigh = Ink50