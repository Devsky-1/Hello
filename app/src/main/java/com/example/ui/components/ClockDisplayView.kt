package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClockAlignment
import com.example.model.ClockConfig
import com.example.model.ClockPosition
import com.example.model.ClockSize
import com.example.model.ClockStyle
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ClockDisplayView(
    config: ClockConfig,
    modifier: Modifier = Modifier,
    isInteractiveDrag: Boolean = false,
    onDragOffsetChange: ((Float, Float) -> Unit)? = null
) {
    // Keep live time updated every second
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000)
        }
    }

    // Format time components
    val timePattern = buildString {
        if (config.is24Hour) {
            append("HH:mm")
        } else {
            append("h:mm")
        }
        if (config.showSeconds) {
            append(":ss")
        }
        if (config.showAmPm && !config.is24Hour) {
            append(" a")
        }
    }

    val timeString = try {
        SimpleDateFormat(timePattern, Locale.getDefault()).format(currentTime)
    } catch (e: Exception) {
        "10:09"
    }

    val datePattern = config.dateFormat.pattern
    val dateString = try {
        SimpleDateFormat(datePattern, Locale.getDefault()).format(currentTime)
    } catch (e: Exception) {
        "Friday, Sep 12"
    }

    // Font Family
    val fontFamily = when (config.fontFamilyName) {
        "Monospace" -> FontFamily.Monospace
        "Serif" -> FontFamily.Serif
        "Cursive" -> FontFamily.Cursive
        else -> FontFamily.SansSerif
    }

    // Font Size
    val fontSizeSp = when (config.size) {
        ClockSize.SMALL -> 34.sp
        ClockSize.MEDIUM -> 54.sp
        ClockSize.LARGE -> 74.sp
        ClockSize.CUSTOM -> config.customSizeSp.sp
    }

    val clockColor = try {
        Color(android.graphics.Color.parseColor(config.colorHex))
    } catch (e: Exception) {
        Color.White
    }

    val shadowColor = try {
        Color(android.graphics.Color.parseColor(config.shadowColorHex))
    } catch (e: Exception) {
        Color.Black.copy(alpha = 0.6f)
    }

    val glowColor = try {
        Color(android.graphics.Color.parseColor(config.glowColorHex))
    } catch (e: Exception) {
        Color(0xFF00F0FF)
    }

    val textShadow = if (config.hasGlow) {
        Shadow(color = glowColor, offset = Offset(0f, 0f), blurRadius = 24f)
    } else if (config.hasShadow) {
        Shadow(color = shadowColor, offset = Offset(4f, 4f), blurRadius = 10f)
    } else {
        Shadow.None
    }

    val textAlign = when (config.alignment) {
        ClockAlignment.LEFT -> TextAlign.Start
        ClockAlignment.CENTER -> TextAlign.Center
        ClockAlignment.RIGHT -> TextAlign.End
    }

    val columnAlignment = when (config.alignment) {
        ClockAlignment.LEFT -> Alignment.Start
        ClockAlignment.CENTER -> Alignment.CenterHorizontally
        ClockAlignment.RIGHT -> Alignment.End
    }

    // Drag offset tracking
    var dragX by remember(config.customOffsetX) { mutableStateOf(config.customOffsetX) }
    var dragY by remember(config.customOffsetY) { mutableStateOf(config.customOffsetY) }

    val dragModifier = if (isInteractiveDrag) {
        Modifier.pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                dragX += dragAmount.x
                dragY += dragAmount.y
                onDragOffsetChange?.invoke(dragX, dragY)
            }
        }
    } else Modifier

    val positionOffsetModifier = when (config.position) {
        ClockPosition.TOP -> Modifier.offset { IntOffset(0, 40) }
        ClockPosition.CENTER -> Modifier.offset { IntOffset(0, 160) }
        ClockPosition.BOTTOM -> Modifier.offset { IntOffset(0, 360) }
        ClockPosition.CUSTOM -> Modifier.offset {
            IntOffset(dragX.roundToInt(), dragY.roundToInt())
        }
    }

    // Glass style container if GLASS
    val isGlassStyle = config.style == ClockStyle.GLASS

    Box(
        modifier = modifier
            .then(positionOffsetModifier)
            .then(dragModifier)
            .alpha(config.alpha)
            .padding(16.dp),
        contentAlignment = when (config.alignment) {
            ClockAlignment.LEFT -> Alignment.CenterStart
            ClockAlignment.CENTER -> Alignment.Center
            ClockAlignment.RIGHT -> Alignment.CenterEnd
        }
    ) {
        Column(
            horizontalAlignment = columnAlignment,
            modifier = if (isGlassStyle) {
                Modifier
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            } else Modifier
        ) {
            // Time Display
            Text(
                text = timeString,
                style = TextStyle(
                    fontSize = fontSizeSp,
                    fontWeight = FontWeight(config.fontWeightValue),
                    fontFamily = fontFamily,
                    color = clockColor,
                    shadow = textShadow,
                    letterSpacing = config.letterSpacingSp.sp,
                    textAlign = textAlign
                )
            )

            // Date Display
            if (config.showDate) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateString,
                    style = TextStyle(
                        fontSize = maxOf(fontSizeSp.value * 0.28f, 14f).sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = fontFamily,
                        color = clockColor.copy(alpha = 0.85f),
                        shadow = textShadow,
                        letterSpacing = 0.5.sp,
                        textAlign = textAlign
                    )
                )
            }
        }
    }
}
