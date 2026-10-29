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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePale
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SkyBlueUltraPale
import com.example.ui.theme.SplashLogoSurfaceEnd
import com.example.ui.theme.SplashLogoSurfaceStart
import com.example.ui.theme.SplashOrbitIndigo
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isDarkTheme: Boolean,
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var logoEntered by remember { mutableStateOf(false) }
    var textEntered by remember { mutableStateOf(false) }
    val syncProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoEntered = true
        delay(140L)
        textEntered = true
        syncProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1650, easing = FastOutSlowInEasing)
        )
        delay(100L)
        onSplashFinished()
    }

    val logoScale by animateFloatAsState(
        targetValue = if (logoEntered) 1f else 0.75f,
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
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "splash_orbit_degrees"
    )
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.06f,
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
                DarkSurfaceElevated.copy(alpha = 0.90f),
                DarkBg
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                SkyBlueUltraPale,
                SkyBluePale.copy(alpha = 0.55f),
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
        val isCompactHeight = maxHeight < 500.dp
        val logoCardSize = if (isCompactHeight) 96.dp else 116.dp
        val logoIconSize = if (isCompactHeight) 68.dp else 82.dp

        // Minimalist ambient glow & subtle orbital ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(x = size.width * 0.5f, y = size.height * 0.45f)
            val baseRadius = size.minDimension * 0.28f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SkyBlueLight.copy(alpha = if (isDarkTheme) 0.18f else 0.24f),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = baseRadius * 1.6f * haloPulse
                ),
                radius = baseRadius * 1.6f * haloPulse,
                center = centerOffset
            )

            drawCircle(
                color = SkyBluePrimary.copy(alpha = if (isDarkTheme) 0.18f else 0.12f),
                radius = baseRadius * 1.05f,
                center = centerOffset,
                style = Stroke(
                    width = 1.2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 16f), phase = orbitDegrees)
                )
            )

            drawCircle(
                color = SplashOrbitIndigo.copy(alpha = if (isDarkTheme) 0.18f else 0.10f),
                radius = baseRadius * 0.76f,
                center = centerOffset,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .widthIn(max = 480.dp)
                .align(Alignment.Center)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Minimalist top-end Skip action (no cluttered header badges)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val skipDesc = stringResource(R.string.splash_skip_content_desc)
                TextButton(
                    onClick = onSplashFinished,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .semantics { contentDescription = skipDesc }
                        .testTag("splash_skip_button"),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = stringResource(R.string.splash_skip),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Clean Hero Emblem + Brand Name + Short Tagline only
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.scale(logoScale)
                ) {
                    Box(
                        modifier = Modifier
                            .size(logoCardSize + 18.dp)
                            .scale(haloPulse)
                            .clip(RoundedCornerShape(32.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        BrandIndigo.copy(alpha = 0.14f),
                                        BrandEmerald.copy(alpha = 0.12f)
                                    )
                                )
                            )
                    )

                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 10.dp,
                        border = BorderStroke(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                listOf(
                                    BrandIndigo.copy(alpha = 0.45f),
                                    BrandCyan.copy(alpha = 0.60f)
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
                                            listOf(DarkSurfaceElevated, DarkBg)
                                        } else {
                                            listOf(SplashLogoSurfaceStart, SplashLogoSurfaceEnd)
                                        }
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = BrandCyan.copy(alpha = 0.18f),
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

                Spacer(modifier = Modifier.height(20.dp))

                AnimatedVisibility(
                    visible = textEntered,
                    enter = fadeIn(tween(380)) + slideInVertically(
                        animationSpec = tween(380, easing = FastOutSlowInEasing),
                        initialOffsetY = { it / 4 }
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = if (isCompactHeight) {
                                MaterialTheme.typography.headlineLarge
                            } else {
                                MaterialTheme.typography.displaySmall
                            },
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.testTag("splash_title")
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = stringResource(R.string.splash_tagline),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.testTag("splash_tagline")
                        )
                    }
                }
            }

            // Minimal slender loading indicator (no status sentence clutter)
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .width(120.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("splash_progress")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = syncProgress.value.coerceIn(0.08f, 1f))
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(BrandIndigo, SkyBlueLight, BrandEmerald)
                            )
                        )
                )
            }
        }
    }
}
