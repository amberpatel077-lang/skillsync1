package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.BrandPink
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePale
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SkyBlueUltraPale
import com.example.ui.theme.SplashLogoSurfaceEnd
import com.example.ui.theme.SplashLogoSurfaceStart
import com.example.ui.theme.SplashOrbitIndigo
import com.example.ui.theme.SplashOrbitViolet
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isDarkTheme: Boolean,
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var logoEntered by remember { mutableStateOf(false) }
    var textEntered by remember { mutableStateOf(false) }
    var pillarsEntered by remember { mutableStateOf(false) }
    val syncProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoEntered = true
        delay(140L)
        textEntered = true
        delay(160L)
        pillarsEntered = true
        syncProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1850, easing = FastOutSlowInEasing)
        )
        delay(120L)
        onSplashFinished()
    }

    val logoScale by animateFloatAsState(
        targetValue = if (logoEntered) 1f else 0.72f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "splash_logo_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "splash_ambient_motion")
    val orbitDegrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "splash_orbit_degrees"
    )
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splash_halo_pulse"
    )

    val backgroundBrush = if (isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                DarkBg,
                DarkSurfaceElevated.copy(alpha = 0.92f),
                DarkBg
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                SkyBlueUltraPale,
                SkyBluePale.copy(alpha = 0.65f),
                MaterialTheme.colorScheme.background
            )
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .testTag("splash_screen")
    ) {
        val isCompactHeight = maxHeight < 540.dp
        val logoCardSize = if (isCompactHeight) 96.dp else 120.dp
        val logoIconSize = if (isCompactHeight) 68.dp else 86.dp

        // Atmospheric Orbital Skill-Sync Constellation Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(x = size.width * 0.5f, y = size.height * 0.42f)
            val baseRadius = size.minDimension * 0.30f

            // Layered ambient radial aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SkyBlueLight.copy(alpha = if (isDarkTheme) 0.20f else 0.28f),
                        BrandEmerald.copy(alpha = if (isDarkTheme) 0.08f else 0.10f),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = baseRadius * 1.65f * haloPulse
                ),
                radius = baseRadius * 1.65f * haloPulse,
                center = centerOffset
            )

            // Outer dashed orbital track
            drawCircle(
                color = SkyBluePrimary.copy(alpha = if (isDarkTheme) 0.24f else 0.18f),
                radius = baseRadius * 1.15f,
                center = centerOffset,
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f), phase = orbitDegrees)
                )
            )

            // Inner subtle continuous sync ring
            drawCircle(
                color = SplashOrbitIndigo.copy(alpha = if (isDarkTheme) 0.26f else 0.16f),
                radius = baseRadius * 0.82f,
                center = centerOffset,
                style = Stroke(width = 1.2.dp.toPx())
            )

            // Four orbiting synced skill nodes (Tech, Design, Synergy, Research)
            val nodeColors = listOf(
                SkyBluePrimary,
                BrandEmerald,
                SplashOrbitIndigo,
                SplashOrbitViolet
            )
            nodeColors.forEachIndexed { index, nodeColor ->
                val angleRad = Math.toRadians((orbitDegrees + index * 90f).toDouble())
                val ringRadius = if (index % 2 == 0) baseRadius * 1.15f else baseRadius * 0.82f
                val nodeCenter = Offset(
                    x = centerOffset.x + (ringRadius * cos(angleRad)).toFloat(),
                    y = centerOffset.y + (ringRadius * sin(angleRad)).toFloat()
                )
                drawCircle(
                    color = nodeColor.copy(alpha = 0.24f),
                    radius = 9.dp.toPx(),
                    center = nodeCenter
                )
                drawCircle(
                    color = nodeColor,
                    radius = 4.5.dp.toPx(),
                    center = nodeCenter
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .widthIn(max = 520.dp)
                .align(Alignment.Center)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top bar: Campus Hub badge & accessible Skip action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = if (isDarkTheme) 0.78f else 0.90f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = stringResource(R.string.splash_campus_badge),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                val skipDesc = stringResource(R.string.splash_skip_content_desc)
                TextButton(
                    onClick = onSplashFinished,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .semantics { contentDescription = skipDesc }
                        .testTag("splash_skip_button"),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.splash_skip),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Center Hero Identity: Emblem + Wordmark + Tagline + Capability Pillars
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Breathing halo + Spring-scaled Official Logo Card
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.scale(logoScale)
                ) {
                    Box(
                        modifier = Modifier
                            .size(logoCardSize + 20.dp)
                            .scale(haloPulse)
                            .clip(RoundedCornerShape(34.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        BrandIndigo.copy(alpha = 0.16f),
                                        BrandEmerald.copy(alpha = 0.14f)
                                    )
                                )
                            )
                    )

                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 12.dp,
                        border = BorderStroke(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                listOf(
                                    BrandIndigo.copy(alpha = 0.55f),
                                    BrandCyan.copy(alpha = 0.70f),
                                    BrandEmerald.copy(alpha = 0.50f)
                                )
                            )
                        ),
                        modifier = Modifier
                            .size(logoCardSize)
                            .testTag("splash_logo")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.linearGradient(
                                        if (isDarkTheme) {
                                            listOf(
                                                DarkSurfaceElevated,
                                                DarkBg
                                            )
                                        } else {
                                            listOf(
                                                SplashLogoSurfaceStart,
                                                SplashLogoSurfaceEnd
                                            )
                                        }
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = BrandCyan.copy(alpha = 0.22f),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_skillsync_logo),
                                contentDescription = stringResource(R.string.splash_logo_content_desc),
                                modifier = Modifier.size(logoIconSize),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(if (isCompactHeight) 14.dp else 24.dp))

                AnimatedVisibility(
                    visible = textEntered,
                    enter = fadeIn(tween(420)) + slideInVertically(
                        animationSpec = tween(420, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 3 }
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // CONNECT • COLLABORATE • GROW motto row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.splash_motto_connect),
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandPink,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.4.sp
                            )
                            MottoDot(color = BrandPink)
                            Text(
                                text = stringResource(R.string.splash_motto_collaborate),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.4.sp
                            )
                            MottoDot(color = BrandEmerald)
                            Text(
                                text = stringResource(R.string.splash_motto_grow),
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandEmerald,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.4.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.app_name),
                            style = if (isCompactHeight) {
                                MaterialTheme.typography.displaySmall
                            } else {
                                MaterialTheme.typography.displayMedium
                            },
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.testTag("splash_title")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(R.string.splash_tagline),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("splash_tagline")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.splash_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                if (!isCompactHeight) {
                    Spacer(modifier = Modifier.height(20.dp))

                    AnimatedVisibility(
                        visible = pillarsEntered,
                        enter = fadeIn(tween(450)) + slideInVertically(
                            animationSpec = tween(450, easing = FastOutSlowInEasing),
                            initialOffsetY = { it / 2 }
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SplashPillarChip(
                                icon = Icons.Filled.Code,
                                label = stringResource(R.string.splash_pillar_hackathons),
                                accentColor = BrandIndigo
                            )
                            SplashPillarChip(
                                icon = Icons.Filled.Groups,
                                label = stringResource(R.string.splash_pillar_smart_teams),
                                accentColor = BrandEmerald
                            )
                            SplashPillarChip(
                                icon = Icons.Filled.RocketLaunch,
                                label = stringResource(R.string.splash_pillar_opportunities),
                                accentColor = BrandCyan
                            )
                        }
                    }
                }
            }

            // Bottom Synchronization Progress Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("splash_progress")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = syncProgress.value.coerceIn(0.06f, 1f))
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        BrandIndigo,
                                        SkyBlueLight,
                                        BrandEmerald
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.splash_sync_status),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MottoDot(color: Color) {
    Box(
        modifier = Modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun SplashPillarChip(
    icon: ImageVector,
    label: String,
    accentColor: Color
) {
    Surface(
        shape = CircleShape,
        color = accentColor.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.36f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
