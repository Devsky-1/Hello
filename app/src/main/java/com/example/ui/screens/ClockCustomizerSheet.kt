package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClockAlignment
import com.example.model.ClockConfig
import com.example.model.ClockPosition
import com.example.model.ClockSize
import com.example.model.ClockStyle
import com.example.model.DateFormatOption
import com.example.ui.components.ClockDisplayView
import com.example.ui.theme.WalloraNeonCyan
import com.example.ui.theme.WalloraVividMagenta
import com.example.ui.viewmodel.WallpaperViewModel

val PRESET_CLOCK_COLORS = listOf(
    "#FFFFFF" to "White",
    "#00F0FF" to "Cyan",
    "#FF007F" to "Pink",
    "#FFD700" to "Gold",
    "#00F5A0" to "Emerald",
    "#A855F7" to "Purple",
    "#FF7043" to "Orange",
    "#38BDF8" to "Sky"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ClockCustomizerSheet(
    viewModel: WallpaperViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val config by viewModel.clockConfig.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Clock Customizer",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Real-time Mini Preview box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F111A))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                ClockDisplayView(config = config.copy(position = ClockPosition.CENTER))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Clock Style Selector
            Text("Clock Style", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ClockStyle.values().forEach { style ->
                    val isSelected = config.style == style
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateClockConfig(config.copy(style = style)) },
                        label = { Text(style.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WalloraNeonCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Position Preset
            Text("Screen Position", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ClockPosition.values().forEach { pos ->
                    val isSelected = config.position == pos
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateClockConfig(config.copy(position = pos)) },
                        label = { Text(pos.label, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WalloraVividMagenta,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Size Selector & Custom Slider
            Text("Clock Size", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ClockSize.values().forEach { size ->
                    val isSelected = config.size == size
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateClockConfig(config.copy(size = size)) },
                        label = { Text(size.label, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WalloraNeonCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            if (config.size == ClockSize.CUSTOM) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Scale: ${config.customSizeSp.toInt()} sp", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(12.dp))
                    Slider(
                        value = config.customSizeSp,
                        onValueChange = { viewModel.updateClockConfig(config.copy(customSizeSp = it)) },
                        valueRange = 24f..96f,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Color Palette
            Text("Clock Color", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PRESET_CLOCK_COLORS.forEach { (hex, name) ->
                    val isSelected = config.colorHex.equals(hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(hex)))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) WalloraNeonCyan else Color(0x33FFFFFF),
                                shape = CircleShape
                            )
                            .clickable {
                                viewModel.updateClockConfig(config.copy(colorHex = hex))
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Font Family
            Text("Font Family", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("SansSerif", "Monospace", "Serif", "Cursive").forEach { font ->
                    val isSelected = config.fontFamilyName == font
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateClockConfig(config.copy(fontFamilyName = font)) },
                        label = { Text(font, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Date Format
            Text("Date Format", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DateFormatOption.values().forEach { opt ->
                    val isSelected = config.dateFormat == opt
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateClockConfig(config.copy(dateFormat = opt)) },
                        label = { Text(opt.label, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Format Toggles
            Text("Format & Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("24-Hour Military Format", fontSize = 14.sp)
                Switch(
                    checked = config.is24Hour,
                    onCheckedChange = { viewModel.updateClockConfig(config.copy(is24Hour = it)) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show Seconds (:ss)", fontSize = 14.sp)
                Switch(
                    checked = config.showSeconds,
                    onCheckedChange = { viewModel.updateClockConfig(config.copy(showSeconds = it)) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show Date", fontSize = 14.sp)
                Switch(
                    checked = config.showDate,
                    onCheckedChange = { viewModel.updateClockConfig(config.copy(showDate = it)) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Neon Glow Effect", fontSize = 14.sp)
                Switch(
                    checked = config.hasGlow,
                    onCheckedChange = { viewModel.updateClockConfig(config.copy(hasGlow = it)) }
                )
            }

            // 8. Opacity Slider
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Opacity: ${(config.alpha * 100).toInt()}%", fontSize = 13.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Slider(
                    value = config.alpha,
                    onValueChange = { viewModel.updateClockConfig(config.copy(alpha = it)) },
                    valueRange = 0.2f..1.0f,
                    modifier = Modifier.weight(1f)
                )
            }

            // 9. Alignment
            Spacer(modifier = Modifier.height(12.dp))
            Text("Alignment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { viewModel.updateClockConfig(config.copy(alignment = ClockAlignment.LEFT)) },
                    modifier = Modifier
                        .weight(1f)
                        .background(if (config.alignment == ClockAlignment.LEFT) WalloraNeonCyan else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.AutoMirrored.Filled.FormatAlignLeft, contentDescription = "Left", tint = if (config.alignment == ClockAlignment.LEFT) Color.Black else Color.White)
                }
                IconButton(
                    onClick = { viewModel.updateClockConfig(config.copy(alignment = ClockAlignment.CENTER)) },
                    modifier = Modifier
                        .weight(1f)
                        .background(if (config.alignment == ClockAlignment.CENTER) WalloraNeonCyan else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.FormatAlignCenter, contentDescription = "Center", tint = if (config.alignment == ClockAlignment.CENTER) Color.Black else Color.White)
                }
                IconButton(
                    onClick = { viewModel.updateClockConfig(config.copy(alignment = ClockAlignment.RIGHT)) },
                    modifier = Modifier
                        .weight(1f)
                        .background(if (config.alignment == ClockAlignment.RIGHT) WalloraNeonCyan else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.AutoMirrored.Filled.FormatAlignRight, contentDescription = "Right", tint = if (config.alignment == ClockAlignment.RIGHT) Color.Black else Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
