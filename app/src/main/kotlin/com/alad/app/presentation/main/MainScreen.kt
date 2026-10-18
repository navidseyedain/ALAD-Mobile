package com.alad.app.presentation.main

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.alad.app.R
import com.alad.app.ui.theme.*
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
    onConnectClicked: () -> Unit,
    onDisconnectClicked: () -> Unit
) {
    val isConnected by viewModel.isConnected.collectAsState()
    val amplitude by viewModel.audioAmplitude.collectAsState()
    val liveSubtitleText by viewModel.liveSubtitleText.collectAsState()
    val liveSourceSubtitleText by viewModel.liveSourceSubtitleText.collectAsState()
    val showDualSubtitles by viewModel.showDualSubtitles.collectAsState()
    val selectedVoice by viewModel.voicePersona.collectAsState()
    val selectedMode by viewModel.dubbingMode.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()
    val targetDialect by viewModel.targetDialect.collectAsState()

    val dubbingVol by viewModel.dubbingVolume.collectAsState()
    val isDubbingMuted by viewModel.isDubbingMuted.collectAsState()
    val originalVol by viewModel.originalVolumeRatio.collectAsState()
    val isOriginalMuted by viewModel.isOriginalMuted.collectAsState()

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showHelpDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    if (showHelpDialog) {
        Dialog(onDismissRequest = { showHelpDialog = false }) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundColor = Color(0xFF10192C),
                borderColor = NeonCyan,
                borderAlpha = 0.35f
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.help_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.help_content),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            lineHeight = 22.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showHelpDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.close),
                            fontWeight = FontWeight.Bold,
                            color = DeepSpace
                        )
                    }
                }
            }
        }
    }

    AmbientBackground {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(
                                    BorderStroke(
                                        1.5.dp,
                                        Brush.linearGradient(
                                            listOf(
                                                NeonCyan.copy(alpha = 0.8f),
                                                NeonPurple.copy(alpha = 0.5f)
                                            )
                                        )
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.mipmap.ic_launcher),
                                contentDescription = "ALAD Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "ALAD",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 20.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "AI Live Audio Dubbing",
                                color = NeonCyan.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlassIconButton(
                            icon = Icons.Default.Info,
                            contentDescription = "Help",
                            onClick = { showHelpDialog = true },
                            tint = NeonCyan
                        )

                        GlassIconButton(
                            icon = Icons.Default.Settings,
                            contentDescription = "Settings",
                            onClick = onNavigateToSettings,
                            tint = Color.White
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 1. Status Indicator
                val statusText = if (isConnected) {
                    stringResource(R.string.status_connected)
                } else {
                    stringResource(R.string.status_disconnected)
                }

                StatusBadge(
                    isConnected = isConnected,
                    statusText = statusText,
                    modifier = Modifier.fillMaxWidth()
                )

                // 2. Animated Audio Waveform
                AnimatedWaveform(
                    isConnected = isConnected,
                    amplitude = amplitude,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // 3. Live Dual Subtitles Banner
                AnimatedVisibility(
                    visible = isConnected && (liveSubtitleText.isNotEmpty() || (showDualSubtitles && liveSourceSubtitleText.isNotEmpty())),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        borderColor = NeonCyan,
                        borderAlpha = 0.35f
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(NeonCyan)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.live_subtitles).uppercase(),
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Text(
                                    text = "$selectedVoice • $selectedMode",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (showDualSubtitles && liveSourceSubtitleText.isNotEmpty()) {
                                Text(
                                    text = liveSourceSubtitleText,
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            if (liveSubtitleText.isNotEmpty()) {
                                Text(
                                    text = liveSubtitleText,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // 4. Target Language & Dialect Selection Bento Card
                var langExpanded by remember { mutableStateOf(false) }
                var dialectExpanded by remember { mutableStateOf(false) }
                var searchQuery by remember { mutableStateOf("") }
                val displayLang = supportedLanguages.find { it.first == targetLang }?.second ?: targetLang

                val availableDialects = supportedDialects[targetLang.split("-")[0]] ?: emptyList()
                val displayDialect = availableDialects.find { it.code.equals(targetDialect, ignoreCase = true) }?.displayName
                    ?: stringResource(R.string.standard_dialect)

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.target_dubbing_language),
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonCyan.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = targetLang.uppercase(),
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Language Dropdown
                        ExposedDropdownMenuBox(
                            expanded = langExpanded,
                            onExpandedChange = { langExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = if (langExpanded) searchQuery else displayLang,
                                onValueChange = { searchQuery = it },
                                readOnly = !langExpanded,
                                label = { Text(stringResource(R.string.select_language), color = TextSecondary) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langExpanded) },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0x30FFFFFF),
                                    unfocusedContainerColor = Color(0x15FFFFFF),
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = langExpanded,
                                onDismissRequest = {
                                    langExpanded = false
                                    searchQuery = ""
                                },
                                modifier = Modifier
                                    .background(Color(0xFF131D31))
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            ) {
                                val filtered = supportedLanguages.filter {
                                    it.second.contains(searchQuery, ignoreCase = true)
                                }
                                filtered.forEach { (code, name) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                name,
                                                color = if (code == targetLang) NeonCyan else Color.White,
                                                fontWeight = if (code == targetLang) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            viewModel.updateTargetLang(code)
                                            langExpanded = false
                                            searchQuery = ""
                                        }
                                    )
                                }
                            }
                        }

                        // Dialect / Accent Selector (if dialects available for this language)
                        if (availableDialects.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = stringResource(R.string.target_dialect),
                                color = TextSecondary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            ExposedDropdownMenuBox(
                                expanded = dialectExpanded,
                                onExpandedChange = { dialectExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = displayDialect,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.select_dialect), color = TextSecondary) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dialectExpanded) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0x25FFFFFF),
                                        unfocusedContainerColor = Color(0x12FFFFFF),
                                        focusedBorderColor = NeonPurple,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = dialectExpanded,
                                    onDismissRequest = { dialectExpanded = false },
                                    modifier = Modifier
                                        .background(Color(0xFF131D31))
                                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                ) {
                                    availableDialects.forEach { dialect ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    dialect.displayName,
                                                    color = if (dialect.code == targetDialect) NeonPurple else Color.White,
                                                    fontWeight = if (dialect.code == targetDialect) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                viewModel.updateTargetDialect(dialect.code)
                                                dialectExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Dual Audio Mixer Bento Card (Issue #6)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    borderColor = NeonCyan,
                    borderAlpha = 0.20f
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.audio_mixer),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // A. Dubbed AI Voice Volume Slider + Mute
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.dubbed_voice_volume),
                                color = if (isDubbingMuted) TextMuted else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isDubbingMuted) "MUTED" else "${(dubbingVol * 100).toInt()}%",
                                    color = if (isDubbingMuted) NeonCoral else NeonCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isDubbingMuted) NeonCoral.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f))
                                        .clickable {
                                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                            viewModel.toggleDubbingMute()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isDubbingMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Mute Dubbing",
                                        tint = if (isDubbingMuted) NeonCoral else Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        Slider(
                            value = dubbingVol,
                            onValueChange = { viewModel.setDubbingVolume(it) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonCyan,
                                inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // B. Original Media Audio Volume Slider + Mute
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.original_media_volume),
                                color = if (isOriginalMuted) TextMuted else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isOriginalMuted) "MUTED" else "${(originalVol * 100).toInt()}%",
                                    color = if (isOriginalMuted) NeonCoral else NeonPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isOriginalMuted) NeonCoral.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f))
                                        .clickable {
                                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                            viewModel.toggleOriginalMute()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isOriginalMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Mute Original Audio",
                                        tint = if (isOriginalMuted) NeonCoral else Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        Slider(
                            value = originalVol,
                            onValueChange = { viewModel.setOriginalVolumeRatio(it) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonPurple,
                                activeTrackColor = NeonPurple,
                                inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 6. Floating Overlay Widget Switch Card
                val isWidgetActive by com.alad.app.core.service.OverlayWidgetService.isWidgetActive.collectAsState()
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    onClick = {
                        if (!Settings.canDrawOverlays(context)) {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        } else {
                            val intent = Intent(context, com.alad.app.core.service.OverlayWidgetService::class.java)
                            if (isWidgetActive) {
                                context.stopService(intent)
                            } else {
                                context.startService(intent)
                            }
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.floating_widget),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (Settings.canDrawOverlays(context)) {
                                    stringResource(R.string.enable_overlay)
                                } else {
                                    stringResource(R.string.grant_permission_widget)
                                },
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Switch(
                            checked = isWidgetActive,
                            onCheckedChange = null,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonCyan,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                            )
                        )
                    }
                }

                // 7. Start/Stop Dubbing Action Button
                StartStopDubbingButton(
                    isConnected = isConnected,
                    onClick = {
                        if (isConnected) {
                            onDisconnectClicked()
                        } else {
                            onConnectClicked()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AnimatedWaveform(
    isConnected: Boolean,
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    val animatedAmplitude by animateFloatAsState(
        targetValue = if (isConnected) amplitude.coerceIn(0.04f, 1f) else 0.03f,
        animationSpec = tween(70, easing = LinearEasing),
        label = "amplitude"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height * 0.50f

            // 1. Draw glowing wave fill & wave stroke (matching Figma dashboard wave curves in reference image)
            val wavePath = Path()
            val fillPath = Path()
            fillPath.moveTo(0f, height)

            val stepCount = 55
            val stepX = width / stepCount

            for (i in 0..stepCount) {
                val x = i * stepX
                val normalizedX = i.toFloat() / stepCount
                // Bell curve windowing so wave attenuates gently near edges
                val window = (sin(normalizedX * Math.PI).toFloat()).coerceIn(0.12f, 1f)

                val wave1 = sin(phase + (normalizedX * 4.2f * Math.PI.toFloat())) * 0.65f
                val wave2 = sin((phase * 1.4f) + (normalizedX * 6.5f * Math.PI.toFloat())) * 0.35f
                val combinedWave = (wave1 + wave2) * window

                val maxDisplacement = height * 0.36f * (0.15f + animatedAmplitude * 1.85f)
                val y = centerY - (combinedWave * maxDisplacement).coerceIn(-height * 0.42f, height * 0.42f)

                if (i == 0) {
                    wavePath.moveTo(x, y)
                    fillPath.lineTo(x, y)
                } else {
                    wavePath.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
            }

            fillPath.lineTo(width, height)
            fillPath.close()

            // Area Gradient Fill under wave
            val fillBrush = if (isConnected) {
                Brush.verticalGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.32f),
                        NeonBlue.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    startY = centerY - height * 0.4f,
                    endY = height
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    startY = centerY - height * 0.2f,
                    endY = height
                )
            }

            drawPath(path = fillPath, brush = fillBrush)

            // Glowing Wave Crest Stroke
            val strokeBrush = if (isConnected) {
                Brush.horizontalGradient(
                    colors = listOf(NeonCyan, NeonBlue, NeonPurple)
                )
            } else {
                Brush.horizontalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.12f))
                )
            }

            drawPath(
                path = wavePath,
                brush = strokeBrush,
                style = Stroke(width = if (isConnected) 3.5.dp.toPx() else 1.8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Specular glow dot on active peak
            if (isConnected) {
                val peakX = width * 0.5f
                val peakY = centerY - (height * 0.26f * (0.2f + animatedAmplitude * 1.5f))
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(peakX, peakY)
                )
                drawCircle(
                    color = NeonCyan.copy(alpha = 0.5f),
                    radius = 9.dp.toPx(),
                    center = Offset(peakX, peakY)
                )
            }
        }
    }
}

@Composable
fun StartStopDubbingButton(
    isConnected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "scale"
    )

    val gradientColors = if (isConnected) {
        listOf(NeonRose, NeonCoral, Color(0xFFE11D48))
    } else {
        listOf(NeonCyan, NeonBlue, NeonViolet)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .scale(scale)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.horizontalGradient(gradientColors)
            )
            .border(
                BorderStroke(
                    1.2.dp,
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.70f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Specular top highlight line inside pill
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(1.dp)
                .align(Alignment.TopCenter)
                .padding(top = 1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isConnected) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (isConnected) stringResource(R.string.btn_stop) else stringResource(R.string.btn_start),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                    fontSize = 17.5.sp
                )
            )
        }
    }
}
