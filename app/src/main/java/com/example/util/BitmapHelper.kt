package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.graphics.applyCanvas
import com.example.model.AspectRatioOption
import com.example.model.EditorState
import com.example.model.FilterMode
import com.example.model.GradientOverlayOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.max
import kotlin.math.min

object BitmapHelper {

    /**
     * Memory-safe downsampled bitmap loader from URL or local path
     */
    suspend fun loadBitmapSafe(context: Context, source: String, maxDimension: Int = 1920): Bitmap? =
        withContext(Dispatchers.IO) {
            try {
                if (source.startsWith("http://") || source.startsWith("https://")) {
                    val url = URL(source)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = 10000
                    connection.readTimeout = 15000
                    connection.doInput = true
                    connection.connect()
                    val input: InputStream = connection.inputStream

                    // Decode bounds first to avoid OOM
                    val tempFile = File(context.cacheDir, "temp_decode_${System.currentTimeMillis()}.tmp")
                    FileOutputStream(tempFile).use { out ->
                        input.copyTo(out)
                    }

                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeFile(tempFile.absolutePath, options)

                    var sampleSize = 1
                    if (options.outHeight > maxDimension || options.outWidth > maxDimension) {
                        val halfHeight = options.outHeight / 2
                        val halfWidth = options.outWidth / 2
                        while ((halfHeight / sampleSize) >= maxDimension && (halfWidth / sampleSize) >= maxDimension) {
                            sampleSize *= 2
                        }
                    }

                    val decodeOptions = BitmapFactory.Options().apply {
                        inSampleSize = sampleSize
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                    val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath, decodeOptions)
                    tempFile.delete()
                    bitmap
                } else if (source.startsWith("content://")) {
                    val uri = Uri.parse(source)
                    val input = context.contentResolver.openInputStream(uri) ?: return@withContext null
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 2
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                    BitmapFactory.decodeStream(input, null, options)
                } else {
                    val file = File(source)
                    if (!file.exists()) return@withContext null
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 1
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                    BitmapFactory.decodeFile(file.absolutePath, options)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    /**
     * Applies full editor transformations:
     * Crop, Rotation, Brightness, Contrast, Saturation, Blur, Vignette, Filters, Gradients, Text & Stamps
     */
    suspend fun applyEditorTransformations(
        baseBitmap: Bitmap,
        state: EditorState
    ): Bitmap = withContext(Dispatchers.Default) {
        var workingBitmap = baseBitmap

        // 1. Rotation & Crop
        if (state.rotationAngle != 0f) {
            val matrix = Matrix().apply { postRotate(state.rotationAngle) }
            workingBitmap = Bitmap.createBitmap(
                workingBitmap,
                0,
                0,
                workingBitmap.width,
                workingBitmap.height,
                matrix,
                true
            )
        }

        // Crop to aspect ratio if specified
        state.cropRatio.ratio?.let { targetRatio ->
            val currentRatio = workingBitmap.width.toFloat() / workingBitmap.height.toFloat()
            var cropW = workingBitmap.width
            var cropH = workingBitmap.height
            var startX = 0
            var startY = 0

            if (currentRatio > targetRatio) {
                cropW = (workingBitmap.height * targetRatio).toInt()
                startX = (workingBitmap.width - cropW) / 2
            } else if (currentRatio < targetRatio) {
                cropH = (workingBitmap.width / targetRatio).toInt()
                startY = (workingBitmap.height - cropH) / 2
            }
            if (cropW > 0 && cropH > 0 && (cropW != workingBitmap.width || cropH != workingBitmap.height)) {
                workingBitmap = Bitmap.createBitmap(workingBitmap, startX, startY, cropW, cropH)
            }
        }

        // 2. Color adjustments & Filter Matrix
        val finalBitmap = Bitmap.createBitmap(
            workingBitmap.width,
            workingBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(finalBitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // Combined Color Matrix
        val colorMatrix = ColorMatrix()

        // Filter Mode
        when (state.filterMode) {
            FilterMode.GRAYSCALE -> {
                colorMatrix.setSaturation(0f)
            }
            FilterMode.SEPIA -> {
                val sepiaMatrix = ColorMatrix().apply {
                    setScale(1f, 0.95f, 0.82f, 1.0f)
                }
                colorMatrix.postConcat(sepiaMatrix)
            }
            FilterMode.CYBERPUNK -> {
                val cyberMatrix = ColorMatrix(floatArrayOf(
                    1.2f, 0f, 0.5f, 0f, 20f,
                    0f, 0.9f, 0.8f, 0f, 10f,
                    0.4f, 0f, 1.4f, 0f, 40f,
                    0f, 0f, 0f, 1f, 0f
                ))
                colorMatrix.postConcat(cyberMatrix)
            }
            FilterMode.INVERT -> {
                val invertMatrix = ColorMatrix(floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                ))
                colorMatrix.postConcat(invertMatrix)
            }
            FilterMode.AMOLED_POP -> {
                val popMatrix = ColorMatrix().apply {
                    setSaturation(1.4f)
                }
                colorMatrix.postConcat(popMatrix)
            }
            FilterMode.COOL_BLUE -> {
                val coolMatrix = ColorMatrix().apply {
                    setScale(0.85f, 0.95f, 1.25f, 1f)
                }
                colorMatrix.postConcat(coolMatrix)
            }
            FilterMode.VINTAGE -> {
                val vintageMatrix = ColorMatrix().apply {
                    setScale(1.1f, 1.0f, 0.9f, 1f)
                    setSaturation(0.75f)
                }
                colorMatrix.postConcat(vintageMatrix)
            }
            FilterMode.NORMAL -> {
                // Normal base
            }
        }

        // Saturation adjustment
        if (state.saturation != 1f && state.filterMode != FilterMode.GRAYSCALE) {
            val satMatrix = ColorMatrix().apply { setSaturation(state.saturation) }
            colorMatrix.postConcat(satMatrix)
        }

        // Brightness & Contrast
        if (state.brightness != 0f || state.contrast != 1f) {
            val scale = state.contrast
            val translate = state.brightness * 255f + (1f - scale) * 128f
            val bcMatrix = ColorMatrix(floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            ))
            colorMatrix.postConcat(bcMatrix)
        }

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(workingBitmap, 0f, 0f, paint)

        // 3. Gradient Overlay
        if (state.gradientOverlay != GradientOverlayOption.NONE) {
            val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            val w = finalBitmap.width.toFloat()
            val h = finalBitmap.height.toFloat()
            val alphaInt = (state.gradientOpacity * 255).toInt().coerceIn(0, 255)

            when (state.gradientOverlay) {
                GradientOverlayOption.SUNSET -> {
                    overlayPaint.shader = LinearGradient(
                        0f, 0f, 0f, h,
                        Color.argb(alphaInt, 255, 94, 98),
                        Color.argb(alphaInt, 255, 153, 102),
                        Shader.TileMode.CLAMP
                    )
                }
                GradientOverlayOption.CYBER_NEON -> {
                    overlayPaint.shader = LinearGradient(
                        0f, 0f, w, h,
                        Color.argb(alphaInt, 0, 240, 255),
                        Color.argb(alphaInt, 255, 0, 128),
                        Shader.TileMode.CLAMP
                    )
                }
                GradientOverlayOption.MIDNIGHT_PURPLE -> {
                    overlayPaint.shader = LinearGradient(
                        0f, 0f, 0f, h,
                        Color.argb(alphaInt, 30, 10, 60),
                        Color.argb(alphaInt, 108, 92, 231),
                        Shader.TileMode.CLAMP
                    )
                }
                GradientOverlayOption.BOTTOM_SHADOW -> {
                    overlayPaint.shader = LinearGradient(
                        0f, h * 0.4f, 0f, h,
                        Color.TRANSPARENT,
                        Color.argb((alphaInt * 1.2).toInt().coerceAtMost(255), 0, 0, 0),
                        Shader.TileMode.CLAMP
                    )
                }
                GradientOverlayOption.VIGNETTE_DARK -> {
                    overlayPaint.shader = RadialGradient(
                        w / 2f, h / 2f, max(w, h) * 0.7f,
                        Color.TRANSPARENT,
                        Color.argb(alphaInt, 0, 0, 0),
                        Shader.TileMode.CLAMP
                    )
                }
                GradientOverlayOption.AMOLED_GRADIENT -> {
                    overlayPaint.shader = LinearGradient(
                        0f, 0f, 0f, h,
                        Color.argb(alphaInt, 0, 0, 0),
                        Color.TRANSPARENT,
                        Shader.TileMode.CLAMP
                    )
                }
                else -> Unit
            }
            canvas.drawRect(0f, 0f, w, h, overlayPaint)
        }

        // 4. Custom Text Overlay
        if (state.overlayText.isNotBlank()) {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = try {
                    Color.parseColor(state.overlayTextColorHex)
                } catch (e: Exception) {
                    Color.WHITE
                }
                textSize = state.overlayTextSizeSp * (finalBitmap.width / 400f).coerceAtLeast(1f)
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                setShadowLayer(8f, 2f, 4f, Color.argb(180, 0, 0, 0))
            }
            val yPos = finalBitmap.height * state.overlayTextPosY
            canvas.drawText(state.overlayText, finalBitmap.width / 2f, yPos, textPaint)
        }

        // 5. Clock / Date stamp overlay
        if (state.showClockStamp) {
            val stampPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 48f * (finalBitmap.width / 400f).coerceAtLeast(1f)
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                setShadowLayer(6f, 0f, 3f, Color.BLACK)
            }
            val now = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            canvas.drawText(now, finalBitmap.width / 2f, finalBitmap.height * 0.22f, stampPaint)
        }

        // 6. Decorative stickers / badges
        if (state.decorativeSticker.isNotBlank()) {
            val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(200, 15, 15, 25)
                style = Paint.Style.FILL
            }
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(220, 0, 240, 255)
                style = Paint.Style.STROKE
                strokeWidth = 3f
            }
            val textBadgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 28f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            val badgeText = state.decorativeSticker
            val rect = android.graphics.RectF(
                finalBitmap.width - 240f,
                finalBitmap.height - 100f,
                finalBitmap.width - 40f,
                finalBitmap.height - 40f
            )
            canvas.drawRoundRect(rect, 16f, 16f, badgePaint)
            canvas.drawRoundRect(rect, 16f, 16f, borderPaint)
            canvas.drawText(badgeText, rect.centerX(), rect.centerY() + 10f, textBadgePaint)
        }

        finalBitmap
    }

    /**
     * Saves bitmap to public MediaStore Pictures/Wallora collection and returns local URI
     */
    suspend fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        title: String = "Wallora_${System.currentTimeMillis()}"
    ): Uri? = withContext(Dispatchers.IO) {
        val fileName = "${title.replace("[^a-zA-Z0-9_]".toRegex(), "_")}.png"
        val mimeType = "image/png"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Wallora")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val uri = context.contentResolver.insert(collection, values) ?: return@withContext null

                context.contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
                uri
            } else {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val walloraDir = File(picturesDir, "Wallora").apply { if (!exists()) mkdirs() }
                val file = File(walloraDir, fileName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to internal app storage
            saveBitmapToInternalStorage(context, bitmap, fileName)
        }
    }

    /**
     * Saves bitmap to internal app storage
     */
    suspend fun saveBitmapToInternalStorage(
        context: Context,
        bitmap: Bitmap,
        fileName: String
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            val dir = File(context.filesDir, "wallpapers").apply { if (!exists()) mkdirs() }
            val file = File(dir, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
