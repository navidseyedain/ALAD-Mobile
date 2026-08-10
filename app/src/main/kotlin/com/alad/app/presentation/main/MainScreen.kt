package com.alad.app.presentation.main

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.alad.app.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alad.app.ui.theme.PrimaryBlue
import com.alad.app.ui.theme.SurfaceDark
import com.alad.app.ui.theme.TextSecondary
import kotlin.math.sin
import kotlin.math.exp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
    onConnectClicked: () -> Unit,
    onDisconnectClicked: () -> Unit
) {
    val isConnected by viewModel.isConnected.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val context = LocalContext.current
    var showHelpDialog by remember { mutableStateOf(false) }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text(stringResource(R.string.help_title)) },
            text = { Text(stringResource(R.string.help_content)) },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "ALAD Mobile",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ) 
                },
                actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Help",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.system_status),
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isConnected) PrimaryBlue else MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
            
            // Audio Waveform Visualizer
            AnimatedWaveform(
                isConnected = isConnected
            )

            // Language Selection
            val targetLang by viewModel.targetLang.collectAsState()
            
            var expanded by remember { mutableStateOf(false) }
            var searchQuery by remember { mutableStateOf("") }
            
            val displayLang = supportedLanguages.find { it.first == targetLang }?.second ?: targetLang
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.target_dubbing_language), color = TextSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = if (expanded) searchQuery else displayLang,
                            onValueChange = { searchQuery = it },
                            readOnly = !expanded,
                            label = { Text(stringResource(R.string.select_language)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { 
                                expanded = false 
                                searchQuery = ""
                            }
                        ) {
                            val filtered = supportedLanguages.filter { 
                                it.second.contains(searchQuery, ignoreCase = true) 
                            }
                            filtered.forEach { (code, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        viewModel.updateTargetLang(code)
                                        expanded = false
                                        searchQuery = ""
                                    }
                                )
                            }
                            if (filtered.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.no_results_found)) },
                                    onClick = {}
                                )
                            }
                        }
                    }
                }
            }
            
            // Floating Widget Permission / Activation
            val isWidgetActive by com.alad.app.core.service.OverlayWidgetService.isWidgetActive.collectAsState()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
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
                    },
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.floating_widget),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (Settings.canDrawOverlays(context)) stringResource(R.string.enable_overlay) else stringResource(R.string.grant_permission_widget),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = isWidgetActive,
                        onCheckedChange = null // handled by row click
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Start / Stop Dubbing Button
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
        }
    }
}

@Composable
fun AnimatedWaveform(
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val amplitude by com.alad.app.core.service.AudioDubbingForegroundService.audioAmplitude.collectAsState()
    val animatedAmplitude by animateFloatAsState(
        targetValue = if (isConnected) amplitude else 0f,
        animationSpec = tween(50, easing = LinearEasing),
        label = "amplitude"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val numBars = 45
    
    Canvas(modifier = modifier.fillMaxWidth().height(120.dp)) {
        val barWidth = size.width / (numBars * 1.5f)
        val space = (size.width - (numBars * barWidth)) / (numBars - 1)
        val centerY = size.height / 2
        
        for (i in 0 until numBars) {
            val normalizedPos = i.toFloat() / (numBars - 1)
            
            val distFromCenter = kotlin.math.abs(normalizedPos - 0.5f)
            val shapeFactor = kotlin.math.exp(-15f * distFromCenter * distFromCenter).toFloat()
            
            val baseHeight = shapeFactor * size.height * 0.9f
            
            val animFactor = if (isConnected) {
                val offsetPhase = phase + (i * 0.3f)
                val idleWobble = (sin(offsetPhase.toDouble()).toFloat() + 1f) / 2f * 0.15f + 0.05f
                idleWobble + (animatedAmplitude * 1.5f)
            } else {
                0.05f
            }
            
            val barHeight = baseHeight * animFactor.coerceIn(0f, 1.2f)
            val maxBarHeight = maxOf(4f, barHeight)
            
            val x = i * (barWidth + space)
            val y = centerY - (maxBarHeight / 2)
            
            drawRoundRect(
                color = if (isConnected) Color(0xFF00E5FF) else Color.Gray.copy(alpha = 0.4f),
                topLeft = Offset(x, y),
                size = Size(barWidth, maxBarHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
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
    
    val yOffset by animateDpAsState(if (isPressed) 6.dp else 0.dp, label = "press")
    
    val baseColor = if (isConnected) Color(0xFFFF3B30) else Color(0xFF00E5FF)
    val shadowColor = if (isConnected) Color(0xFFA30000) else Color(0xFF008299)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(shadowColor)
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = yOffset)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            baseColor.copy(alpha = 0.8f),
                            baseColor
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isConnected) stringResource(R.string.btn_stop) else stringResource(R.string.btn_start),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

