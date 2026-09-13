package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IconConfig
import com.example.model.IconPack
import com.example.model.IconShape

private val HexagonShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w * 0.5f, 0f)
    lineTo(w, h * 0.25f)
    lineTo(w, h * 0.75f)
    lineTo(w * 0.5f, h)
    lineTo(0f, h * 0.75f)
    lineTo(0f, h * 0.25f)
    close()
}

private val TeardropShape = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 24.dp,
    bottomStart = 24.dp,
    bottomEnd = 4.dp
)

private val SquircleShape = RoundedCornerShape(32)

@Composable
fun getComposeShape(iconShape: IconShape): Shape {
    return when (iconShape) {
        IconShape.CIRCLE -> CircleShape
        IconShape.SQUIRCLE -> SquircleShape
        IconShape.ROUNDED_RECT -> RoundedCornerShape(16.dp)
        IconShape.HEXAGON -> HexagonShape
        IconShape.TEARDROP -> TeardropShape
        IconShape.SQUARE -> RoundedCornerShape(4.dp)
    }
}

data class SampleApp(
    val name: String,
    val icon: ImageVector,
    val baseColor: Color
)

@Composable
fun IconPreviewRow(
    config: IconConfig,
    modifier: Modifier = Modifier,
    isDock: Boolean = false
) {
    val sampleApps = if (isDock) {
        listOf(
            SampleApp("Phone", Icons.Default.Phone, Color(0xFF10B981)),
            SampleApp("Messages", Icons.Default.Chat, Color(0xFF3B82F6)),
            SampleApp("Chrome", Icons.Default.Language, Color(0xFFF59E0B)),
            SampleApp("Camera", Icons.Default.CameraAlt, Color(0xFFEF4444))
        )
    } else {
        listOf(
            SampleApp("Music", Icons.Default.MusicNote, Color(0xFFEC4899)),
            SampleApp("Gallery", Icons.Default.PhotoLibrary, Color(0xFF8B5CF6)),
            SampleApp("Settings", Icons.Default.Settings, Color(0xFF6B7280)),
            SampleApp("Browser", Icons.Default.Language, Color(0xFF00D2FF))
        )
    }

    val shape = getComposeShape(config.shape)
    val iconSize = config.sizeDp.dp.coerceIn(40.dp, 64.dp)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        sampleApps.forEach { app ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon styling by IconPack
                val (bgBrush, iconColor, borderBrush) = when (config.pack) {
                    IconPack.NEON_GLOW -> Triple(
                        Brush.linearGradient(listOf(Color(0xFF0A0F1D), Color(0xFF141E33))),
                        Color(0xFF00F0FF),
                        Brush.linearGradient(listOf(Color(0xFF00F0FF), Color(0xFFFF007F)))
                    )
                    IconPack.MINIMAL_MONO -> Triple(
                        Brush.linearGradient(listOf(Color(0xFF18181B), Color(0xFF27272A))),
                        Color.White,
                        Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x33FFFFFF)))
                    )
                    IconPack.MATERIAL_YOU -> Triple(
                        Brush.linearGradient(listOf(app.baseColor.copy(alpha = 0.25f), app.baseColor.copy(alpha = 0.45f))),
                        app.baseColor,
                        Brush.linearGradient(listOf(app.baseColor.copy(alpha = 0.6f), app.baseColor.copy(alpha = 0.8f)))
                    )
                    IconPack.GLASSMORPHISM -> Triple(
                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.1f))),
                        Color.White,
                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.2f)))
                    )
                    IconPack.CYBERPUNK -> Triple(
                        Brush.linearGradient(listOf(Color(0xFF0D0221), Color(0xFF0F051D))),
                        Color(0xFFFFEE00),
                        Brush.linearGradient(listOf(Color(0xFFFFEE00), Color(0xFFFF007F)))
                    )
                    IconPack.AMOLED_DARK -> Triple(
                        Brush.linearGradient(listOf(Color.Black, Color.Black)),
                        Color(0xFF00F0FF),
                        Brush.linearGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                    )
                    IconPack.PASTEL_CREAM -> Triple(
                        Brush.linearGradient(listOf(Color(0xFFFDE68A), Color(0xFFFBCFE8))),
                        Color(0xFF831843),
                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.8f)))
                    )
                }

                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(shape)
                        .background(bgBrush)
                        .border(1.5.dp, borderBrush, shape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = app.icon,
                        contentDescription = app.name,
                        tint = iconColor,
                        modifier = Modifier.size(iconSize * 0.52f)
                    )
                }

                if (config.showLabels) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = app.name,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
