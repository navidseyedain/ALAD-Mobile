package com.alad.app.core.service

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.alad.app.R
import com.alad.app.data.repository.DubbingSessionRepository
import com.alad.app.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class OverlayWidgetService : LifecycleService() {

    companion object {
        val isWidgetActive = MutableStateFlow(false)
    }

    private lateinit var windowManager: WindowManager
    private lateinit var controllerComposeView: ComposeView
    private lateinit var subtitleComposeView: ComposeView

    private var controllerParams: WindowManager.LayoutParams? = null
    private var subtitleParams: WindowManager.LayoutParams? = null
    private var isSubtitleViewAttached = false

    private val savedStateRegistryOwner by lazy { ServiceSavedStateRegistryOwner(this) }
    private val viewModelStoreOwner by lazy { ServiceViewModelStoreOwner() }

    override fun onCreate() {
        super.onCreate()
        isWidgetActive.value = true
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // 1. Controller Pill Window Params (Top-left draggable)
        controllerParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 24
            y = 220
        }

        // 2. Subtitle Window Params (Bottom-center draggable)
        subtitleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            x = 0
            y = 100
        }

        // Setup Controller ComposeView
        controllerComposeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ALADTheme {
                    FloatingControllerCapsule(
                        onDrag = { dx, dy ->
                            controllerParams?.let { p ->
                                p.x = (p.x + dx.toInt()).coerceAtLeast(0)
                                p.y = (p.y + dy.toInt()).coerceAtLeast(0)
                                windowManager.updateViewLayout(controllerComposeView, p)
                            }
                        },
                        onClose = { stopSelf() },
                        onToggle = { isCurrentlyRunning ->
                            if (isCurrentlyRunning) {
                                val intent = Intent(this@OverlayWidgetService, AudioDubbingForegroundService::class.java).apply {
                                    action = AudioDubbingForegroundService.ACTION_STOP
                                }
                                startService(intent)
                            } else {
                                val intent = Intent(this@OverlayWidgetService, com.alad.app.TransparentCaptureActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                startActivity(intent)
                            }
                        }
                    )
                }
            }
        }
        controllerComposeView.setViewTreeLifecycleOwner(this)
        controllerComposeView.setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
        controllerComposeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)

        // Setup Subtitle ComposeView
        subtitleComposeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ALADTheme {
                    FloatingCinemaSubtitles(
                        onDrag = { dx, dy ->
                            subtitleParams?.let { p ->
                                p.x = (p.x + dx.toInt())
                                p.y = (p.y - dy.toInt()).coerceAtLeast(20)
                                windowManager.updateViewLayout(subtitleComposeView, p)
                            }
                        },
                        onDismiss = {
                            DubbingSessionRepository.setSubtitleOverlayVisibility(false)
                        }
                    )
                }
            }
        }
        subtitleComposeView.setViewTreeLifecycleOwner(this)
        subtitleComposeView.setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
        subtitleComposeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)

        // Attach controller to WindowManager
        windowManager.addView(controllerComposeView, controllerParams)

        // Dynamically attach/detach subtitle window based on session state
        lifecycleScope.launch {
            combine(
                DubbingSessionRepository.isDubbingRunning,
                DubbingSessionRepository.isSubtitleOverlayVisible
            ) { isRunning, isVisible ->
                isRunning && isVisible
            }.collect { shouldShowSubtitles ->
                if (shouldShowSubtitles && !isSubtitleViewAttached) {
                    try {
                        windowManager.addView(subtitleComposeView, subtitleParams)
                        isSubtitleViewAttached = true
                    } catch (e: Exception) {
                        android.util.Log.e("OverlayWidgetService", "Error attaching subtitle view", e)
                    }
                } else if (!shouldShowSubtitles && isSubtitleViewAttached) {
                    try {
                        windowManager.removeView(subtitleComposeView)
                        isSubtitleViewAttached = false
                    } catch (e: Exception) {
                        android.util.Log.e("OverlayWidgetService", "Error detaching subtitle view", e)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isWidgetActive.value = false
        if (::controllerComposeView.isInitialized) {
            try { windowManager.removeView(controllerComposeView) } catch (_: Exception) {}
        }
        if (isSubtitleViewAttached && ::subtitleComposeView.isInitialized) {
            try { windowManager.removeView(subtitleComposeView) } catch (_: Exception) {}
            isSubtitleViewAttached = false
        }
    }
}

@Composable
fun FloatingControllerCapsule(
    onDrag: (Float, Float) -> Unit,
    onClose: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    val isRunning by DubbingSessionRepository.isDubbingRunning.collectAsState()
    val isSubtitleVisible by DubbingSessionRepository.isSubtitleOverlayVisible.collectAsState()
    val amplitude by DubbingSessionRepository.audioAmplitude.collectAsState()

    val haptic = LocalHapticFeedback.current

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRunning) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Row(
        modifier = Modifier
            .wrapContentSize()
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xEE090E1A),
                        Color(0xF504060C)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    if (isRunning) {
                        Brush.linearGradient(listOf(NeonRose, NeonCoral))
                    } else {
                        Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.6f), NeonBlue.copy(alpha = 0.3f)))
                    }
                ),
                shape = CircleShape
            )
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Play / Stop Button with Pulsing Halo
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(34.dp)
        ) {
            if (isRunning) {
                Box(
                    modifier = Modifier
                        .size((32 * pulseScale).dp)
                        .clip(CircleShape)
                        .background(NeonCoral.copy(alpha = 0.25f))
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            if (isRunning) listOf(NeonRose, NeonCoral) else listOf(NeonCyan, NeonBlue)
                        )
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggle(isRunning)
                    }
            ) {
                if (isRunning) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.White, shape = RoundedCornerShape(2.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Dubbing",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }

        // 2. If Running: Live Equalizer + Subtitle Toggle
        if (isRunning) {
            Spacer(modifier = Modifier.width(5.dp))

            // 3-Bar Dynamic Equalizer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.5.dp),
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                val ampFactor = (amplitude * 1.5f).coerceIn(0.2f, 1f)
                Box(
                    modifier = Modifier
                        .width(2.5.dp)
                        .height((6 + 10 * ampFactor * pulseScale).dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(NeonCyan)
                )
                Box(
                    modifier = Modifier
                        .width(2.5.dp)
                        .height((8 + 12 * ampFactor * (2f - pulseScale)).dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(NeonPurple)
                )
                Box(
                    modifier = Modifier
                        .width(2.5.dp)
                        .height((5 + 8 * ampFactor * pulseScale).dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(NeonCyan)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Subtitle Quick Toggle Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSubtitleVisible) NeonCyan.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.08f)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        DubbingSessionRepository.toggleSubtitleOverlayVisibility()
                    }
            ) {
                Icon(
                    imageVector = if (isSubtitleVisible) Icons.Default.Subtitles else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Subtitles",
                    tint = if (isSubtitleVisible) NeonCyan else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // 3. Compact Close 'X' Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClose()
                }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Widget",
                tint = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
fun FloatingCinemaSubtitles(
    onDrag: (Float, Float) -> Unit,
    onDismiss: () -> Unit
) {
    val liveSubtitleText by DubbingSessionRepository.liveSubtitleText.collectAsState()
    val connectionStatus by DubbingSessionRepository.connectionStatus.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "listening")
    val listeningAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "listeningAlpha"
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .widthIn(min = 240.dp, max = 350.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xF0080D1A),
                        Color(0xF7040710)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            NeonCyan.copy(alpha = 0.45f),
                            NeonPurple.copy(alpha = 0.25f)
                        )
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column {
            // Header Bar with Status Dot + Drag Handle + Close
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (connectionStatus == "Gemini Ready" || liveSubtitleText.isNotEmpty()) NeonCyan else NeonCoral)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ALAD SUBTITLES",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                // Drag indicator / handle
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(Color.White.copy(alpha = 0.20f))
                )

                // Minimize icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .clickable { onDismiss() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Hide Subtitles",
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Subtitle Content
            if (liveSubtitleText.isNotEmpty()) {
                Text(
                    text = liveSubtitleText,
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.85f),
                            offset = Offset(1f, 1f),
                            blurRadius = 3f
                        )
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = stringResource(R.string.subtitles_waiting),
                    color = TextSecondary.copy(alpha = listeningAlpha),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
