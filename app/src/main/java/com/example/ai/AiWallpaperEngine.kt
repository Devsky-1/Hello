package com.example.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.Typeface
import com.example.util.BitmapHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

data class AiGenerationConfig(
    val prompt: String,
    val style: String = "Cyberpunk",
    val aspectRatio: String = "9:16 (Phone)",
    val quality: String = "4K Cinematic"
)

data class AiWallpaperResult(
    val id: String,
    val prompt: String,
    val style: String,
    val aspectRatio: String,
    val quality: String,
    val bitmap: Bitmap,
    val localUri: String
)

interface AiImageProvider {
    val providerName: String
    fun isAvailable(): Boolean
    suspend fun generate(context: Context, config: AiGenerationConfig): Result<Bitmap>
}

object AiWallpaperEngine {

    val styles = listOf(
        "Cyberpunk",
        "Anime Aesthetic",
        "Photorealistic 8K",
        "Sci-Fi Void",
        "Minimal 3D",
        "Fantasy Celestial",
        "Oil Impressionism",
        "Vaporwave Synth",
        "Dark Fantasy",
        "AMOLED Pure Black"
    )

    val aspectRatios = listOf(
        "9:16 (Phone)",
        "9:20 (Tall Phone)",
        "1:1 (Square)",
        "16:9 (Landscape)"
    )

    val qualities = listOf(
        "Standard HD (1080p)",
        "2K Ultra HD",
        "4K Cinematic"
    )

    val samplePrompts = listOf(
        "Futuristic black supercar in a neon city at night, cinematic lighting, realistic, vertical phone wallpaper.",
        "Iridescent crystal galaxy floating in a deep void with glowing celestial rings, AMOLED pure black.",
        "Serene Japanese torii gate at midnight under vibrant violet sakura blossoms, glowing lantern reflections.",
        "Minimalist architectural geometric curves with soft golden hour shadows, warm beige modern aesthetics.",
        "Cyberpunk samurai overlooking a rain-drenched holographic metropolis from a rooftop spire."
    )

    suspend fun generateWallpaper(
        context: Context,
        config: AiGenerationConfig
    ): Result<AiWallpaperResult> = withContext(Dispatchers.Default) {
        try {
            // Simulated generation progress for smooth commercial feel
            delay(1200)

            val (width, height) = when (config.aspectRatio) {
                "1:1 (Square)" -> 1440 to 1440
                "16:9 (Landscape)" -> 1920 to 1080
                "9:20 (Tall Phone)" -> 1080 to 2400
                else -> 1080 to 1920 // 9:16 default
            }

            // High-fidelity procedural generative art synthesis matching prompt & style
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            renderGenerativeArt(bitmap, config)

            // Save to app internal directory
            val file = File(context.filesDir, "ai_wall_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
            }

            val result = AiWallpaperResult(
                id = "ai_${System.currentTimeMillis()}",
                prompt = config.prompt,
                style = config.style,
                aspectRatio = config.aspectRatio,
                quality = config.quality,
                bitmap = bitmap,
                localUri = file.absolutePath
            )
            Result.success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun renderGenerativeArt(bitmap: Bitmap, config: AiGenerationConfig) {
        val canvas = Canvas(bitmap)
        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()
        val seed = (config.prompt.hashCode() xor config.style.hashCode()).toLong()
        val rng = Random(seed)

        val promptLower = config.prompt.lowercase()

        // 1. Base gradient background based on style
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val (topColor, midColor, botColor) = when {
            config.style.contains("Cyberpunk", true) || promptLower.contains("neon") -> {
                Triple(Color.rgb(10, 5, 25), Color.rgb(25, 10, 60), Color.rgb(2, 2, 8))
            }
            config.style.contains("AMOLED", true) || promptLower.contains("black") -> {
                Triple(Color.BLACK, Color.rgb(8, 8, 12), Color.BLACK)
            }
            config.style.contains("Anime", true) || promptLower.contains("sakura") -> {
                Triple(Color.rgb(35, 15, 45), Color.rgb(75, 25, 70), Color.rgb(20, 10, 30))
            }
            config.style.contains("Sci-Fi", true) || promptLower.contains("space") -> {
                Triple(Color.rgb(3, 8, 20), Color.rgb(15, 25, 55), Color.rgb(2, 4, 10))
            }
            config.style.contains("Minimal", true) -> {
                Triple(Color.rgb(240, 235, 225), Color.rgb(220, 210, 200), Color.rgb(200, 190, 180))
            }
            else -> {
                Triple(Color.rgb(15, 15, 30), Color.rgb(40, 20, 60), Color.rgb(10, 10, 20))
            }
        }

        bgPaint.shader = LinearGradient(0f, 0f, 0f, h, intArrayOf(topColor, midColor, botColor), null, Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // 2. Celestial Orb / Sun / Core
        val orbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val orbRadius = w * 0.35f
        val orbCenterX = w * 0.5f
        val orbCenterY = h * 0.38f

        val orbStartColor = if (config.style.contains("AMOLED", true)) Color.rgb(0, 240, 255) else Color.rgb(255, 0, 128)
        val orbEndColor = if (config.style.contains("Anime", true)) Color.rgb(255, 120, 160) else Color.rgb(255, 215, 0)

        orbPaint.shader = RadialGradient(
            orbCenterX, orbCenterY, orbRadius,
            intArrayOf(orbEndColor, orbStartColor, Color.TRANSPARENT),
            floatArrayOf(0f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(orbCenterX, orbCenterY, orbRadius, orbPaint)

        // 3. Perspective Grid / Cyber Lines / Mountain Silhouettes
        val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            color = Color.argb(120, 0, 240, 255)
        }

        val horizonY = h * 0.58f

        // Draw geometric terrain/mountains in midground
        val mountainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = LinearGradient(0f, horizonY - 200f, 0f, horizonY + 100f,
                Color.rgb(30, 15, 55), Color.rgb(10, 5, 20), Shader.TileMode.CLAMP)
        }
        val mountainPath = Path().apply {
            moveTo(0f, horizonY)
            var currentX = 0f
            while (currentX <= w) {
                val peakH = rng.nextInt(120, 300).toFloat()
                val nextX = currentX + rng.nextInt(150, 350).toFloat()
                lineTo(currentX + (nextX - currentX) / 2f, horizonY - peakH)
                lineTo(nextX, horizonY)
                currentX = nextX
            }
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        canvas.drawPath(mountainPath, mountainPaint)

        // Perspective grid lines to horizon
        val gridOriginX = w * 0.5f
        val gridOriginY = horizonY
        for (i in -8..8) {
            val targetX = gridOriginX + (i * (w / 6f))
            canvas.drawLine(gridOriginX, gridOriginY, targetX, h, pathPaint)
        }
        var yStep = horizonY + 20f
        var stepDist = 15f
        while (yStep <= h) {
            canvas.drawLine(0f, yStep, w, yStep, pathPaint)
            stepDist *= 1.35f
            yStep += stepDist
        }

        // 4. Foreground Car / Cyber Silhouette if prompt includes "car" or "supercar"
        if (promptLower.contains("car") || promptLower.contains("supercar")) {
            val carPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(12, 14, 20)
                style = Paint.Style.FILL
            }
            val carGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(0, 240, 255)
                style = Paint.Style.STROKE
                strokeWidth = 5f
            }
            val carTailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(255, 30, 80)
                style = Paint.Style.STROKE
                strokeWidth = 6f
            }

            val carW = w * 0.7f
            val carH = carW * 0.32f
            val carX = (w - carW) / 2f
            val carY = h * 0.72f

            val carBody = Path().apply {
                moveTo(carX + carW * 0.1f, carY + carH)
                lineTo(carX + carW * 0.05f, carY + carH * 0.7f)
                lineTo(carX + carW * 0.22f, carY + carH * 0.45f)
                lineTo(carX + carW * 0.4f, carY + carH * 0.15f)
                lineTo(carX + carW * 0.65f, carY + carH * 0.15f)
                lineTo(carX + carW * 0.85f, carY + carH * 0.55f)
                lineTo(carX + carW * 0.95f, carY + carH * 0.75f)
                lineTo(carX + carW * 0.9f, carY + carH)
                close()
            }
            canvas.drawPath(carBody, carPaint)
            // Headlights & Tail accents
            canvas.drawLine(carX + carW * 0.08f, carY + carH * 0.65f, carX + carW * 0.2f, carY + carH * 0.5f, carGlowPaint)
            canvas.drawLine(carX + carW * 0.82f, carY + carH * 0.6f, carX + carW * 0.93f, carY + carH * 0.72f, carTailPaint)
        }

        // 5. Starfield / Cyber Particles
        val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(190, 255, 255, 255)
        }
        for (i in 0..120) {
            val sx = rng.nextFloat() * w
            val sy = rng.nextFloat() * horizonY
            val sr = rng.nextFloat() * 2.8f + 0.8f
            canvas.drawCircle(sx, sy, sr, starPaint)
        }

        // 6. Signature AI watermark text subtly at the bottom
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(160, 255, 255, 255)
            textSize = 26f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("WALLORA AI • ${config.style.uppercase()}", w / 2f, h - 50f, textPaint)
    }
}
