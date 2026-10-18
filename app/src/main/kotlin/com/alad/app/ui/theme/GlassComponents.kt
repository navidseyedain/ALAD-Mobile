package com.alad.app.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.alad.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Full-screen smooth obsidian liquid background container without any hard-edge circular discs.
 * Provides deep space aesthetic with pure smooth gradients.
 */
@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val bgBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF060911),
            Color(0xFF0A101D),
            Color(0xFF070B14)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        content()
    }
}

/**
 * High-end Liquid Glass container (matching Figma Liquid Glass UI / VisionOS style)
 * with frosted translucent surface, specular top highlight reflection, and glossy border.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    backgroundColor: Color = LiquidGlassSurface,
    borderColor: Color = Color.White,
    borderAlpha: Float = 0.28f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    // Multi-stop liquid glass surface reflection
    val glassBrush = Brush.verticalGradient(
        colors = listOf(
            backgroundColor.copy(alpha = 0.85f),
            backgroundColor.copy(alpha = 0.50f)
        )
    )

    // Specular liquid reflection border: bright specular reflection at top-left, fading gracefully
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            borderColor.copy(alpha = borderAlpha),
            NeonCyan.copy(alpha = borderAlpha * 0.45f),
            borderColor.copy(alpha = borderAlpha * 0.12f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(clickableModifier)
            .background(glassBrush)
            .border(BorderStroke(1.2.dp, borderBrush), shape = shape)
    ) {
        // Specular inner top sheen bar (giving the physical look of liquid glass)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.40f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

/**
 * Liquid Glass Pill container for interactive pill rows and capsules.
 */
@Composable
fun LiquidGlassPill(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val pillBg = if (isSelected) {
        Brush.horizontalGradient(
            listOf(
                NeonCyan.copy(alpha = 0.22f),
                NeonBlue.copy(alpha = 0.12f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                Color.White.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.03f)
            )
        )
    }

    val borderBrush = if (isSelected) {
        Brush.horizontalGradient(
            listOf(
                NeonCyan.copy(alpha = 0.8f),
                NeonBlue.copy(alpha = 0.4f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                Color.White.copy(alpha = 0.18f),
                Color.White.copy(alpha = 0.06f)
            )
        )
    }

    val clickableModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Box(
        modifier = modifier
            .clip(shape)
            .then(clickableModifier)
            .background(pillBg)
            .border(BorderStroke(1.dp, borderBrush), shape = shape)
    ) {
        content()
    }
}

/**
 * Glassmorphic circular icon button with frosted backdrop and neon tint.
 */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = TextPrimary,
    size: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.04f)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                ),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

/**
 * Modern Status Card with live pulsing glowing halo, state badge pill, and smooth typography.
 */
@Composable
fun StatusBadge(
    isConnected: Boolean,
    statusText: String,
    modifier: Modifier = Modifier
) {
    val glowColor = if (isConnected) NeonCyan else Color(0xFF64748B)

    val infiniteTransition = rememberInfiniteTransition(label = "status_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isConnected) 1.8f else 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isConnected) 900 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        backgroundColor = if (isConnected) Color(0x700B1A2F) else Color(0x550F172A),
        borderColor = if (isConnected) NeonCyan else Color.White,
        borderAlpha = if (isConnected) 0.40f else 0.12f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Pulsing Outer Halo
                    Box(
                        modifier = Modifier
                            .size((12 * pulseScale).dp)
                            .clip(CircleShape)
                            .background(glowColor.copy(alpha = if (isConnected) 0.35f else 0.15f))
                    )
                    // Core Dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(glowColor)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = stringResource(R.string.system_status),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isConnected) Color.White else TextSecondary,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            // Dynamic State Badge Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isConnected) NeonCyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f))
                    .border(
                        BorderStroke(
                            1.dp,
                            if (isConnected) NeonCyan.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.15f)
                        ),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isConnected) "LIVE" else "IDLE",
                    color = if (isConnected) NeonCyan else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
