package com.example.model

enum class WallpaperResolution(val label: String, val badge: String) {
    HD("HD 720p", "HD"),
    FHD("Full HD 1080p", "FHD"),
    QHD("2K Quad HD", "2K"),
    UHD_4K("4K Ultra HD", "4K"),
    AMOLED("AMOLED Pure Black", "AMOLED")
}

enum class WallpaperCategory(val id: String, val title: String, val iconName: String) {
    ALL("all", "All", "Explore"),
    GAMING("gaming", "Gaming", "SportsEsports"),
    CARS("cars", "Cars", "DirectionsCar"),
    NATURE("nature", "Nature", "Landscape"),
    SPACE("space", "Space", "RocketLaunch"),
    TECHNOLOGY("tech", "Technology", "Memory"),
    ANIME("anime", "Anime", "Brush"),
    MOVIES("movies", "Movies", "Movie"),
    ANIMALS("animals", "Animals", "Pets"),
    ABSTRACT("abstract", "Abstract", "Grain"),
    MINIMAL("minimal", "Minimal", "FilterVintage"),
    DARK("dark", "Dark", "DarkMode"),
    CITY("city", "City", "LocationCity"),
    LUXURY("luxury", "Luxury", "Diamond"),
    K4("4k", "4K Ultra", "HighQuality"),
    AMOLED("amoled", "AMOLED", "BrightnessLow")
}

data class Wallpaper(
    val id: String,
    val title: String,
    val author: String = "Wallora Studios",
    val category: String,
    val resolution: WallpaperResolution = WallpaperResolution.UHD_4K,
    val thumbnailUrl: String,
    val fullUrl: String,
    val tags: List<String> = emptyList(),
    val dominantColors: List<Long> = listOf(0xFF1E1E2E, 0xFF6C5CE7, 0xFF00D2FF),
    val viewsCount: Int = 1240,
    val downloadsCount: Int = 450,
    val likesCount: Int = 320,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isLatest: Boolean = false,
    val isAiGenerated: Boolean = false,
    val isAmoled: Boolean = false,
    val description: String = "Ultra high-definition wallpaper optimized for modern AMOLED and OLED smartphone displays."
)

// Clock Customizer Models
enum class ClockPosition(val label: String) {
    TOP("Top"),
    CENTER("Center"),
    BOTTOM("Bottom"),
    CUSTOM("Custom (Drag)")
}

enum class ClockSize(val label: String, val defaultSp: Float) {
    SMALL("Small", 32f),
    MEDIUM("Medium", 52f),
    LARGE("Large", 76f),
    CUSTOM("Custom Slider", 60f)
}

enum class ClockStyle(val label: String) {
    DIGITAL("Digital"),
    MINIMAL("Minimal"),
    BOLD("Bold Display"),
    FUTURISTIC("Futuristic"),
    GLASS("Glassmorphism"),
    NEON("Neon Glow"),
    CLASSIC("Classic Roman"),
    ELEGANT("Elegant Serif"),
    GAMING("Cyber Gaming")
}

enum class DateFormatOption(val label: String, val pattern: String) {
    DD_MM_YYYY("DD/MM/YYYY", "dd/MM/yyyy"),
    MM_DD_YYYY("MM/DD/YYYY", "MM/dd/yyyy"),
    DD_MMM("DD MMM (e.g. 12 Sep)", "dd MMM"),
    MMM_DD("MMM DD (e.g. Sep 12)", "MMM dd"),
    FULL_DATE("EEEE, MMM d", "EEEE, MMM d"),
    CUSTOM("Custom Pattern", "EEE, d MMM yyyy")
}

enum class ClockAlignment(val label: String) {
    LEFT("Left"),
    CENTER("Center"),
    RIGHT("Right")
}

data class ClockConfig(
    val position: ClockPosition = ClockPosition.TOP,
    val customOffsetX: Float = 0f,
    val customOffsetY: Float = 0f,
    val size: ClockSize = ClockSize.LARGE,
    val customSizeSp: Float = 64f,
    val style: ClockStyle = ClockStyle.BOLD,
    val is24Hour: Boolean = false,
    val showHours: Boolean = true,
    val showMinutes: Boolean = true,
    val showSeconds: Boolean = false,
    val showAmPm: Boolean = true,
    val showDate: Boolean = true,
    val showDayOfWeek: Boolean = true,
    val dateFormat: DateFormatOption = DateFormatOption.FULL_DATE,
    val customDateFormat: String = "EEE, d MMM",
    val fontFamilyName: String = "SansSerif", // SansSerif, Monospace, Serif, Cursive
    val fontWeightValue: Int = 700, // 300 to 900
    val colorHex: String = "#FFFFFF",
    val alpha: Float = 0.95f,
    val hasShadow: Boolean = true,
    val shadowColorHex: String = "#80000000",
    val hasGlow: Boolean = false,
    val glowColorHex: String = "#00F0FF",
    val letterSpacingSp: Float = 1.0f,
    val alignment: ClockAlignment = ClockAlignment.CENTER
)

// Icon Customization Models
enum class IconShape(val label: String) {
    CIRCLE("Circle"),
    SQUIRCLE("Squircle"),
    ROUNDED_RECT("Rounded Rect"),
    HEXAGON("Hexagon"),
    TEARDROP("Teardrop"),
    SQUARE("Square")
}

enum class IconPack(val label: String, val description: String) {
    NEON_GLOW("Neon Glow", "Vibrant glowing cyber lines"),
    MINIMAL_MONO("Minimal Monochrome", "Clean monochrome line art"),
    MATERIAL_YOU("Material You", "Dynamic colors matching wallpaper"),
    GLASSMORPHISM("Glassmorphic", "Frosted glass translucent icons"),
    CYBERPUNK("Cyberpunk 2077", "High contrast neon and yellow"),
    AMOLED_DARK("Pure AMOLED", "Deep black with minimal accents"),
    PASTEL_CREAM("Pastel Cream", "Soft aesthetic modern tones")
}

data class IconConfig(
    val pack: IconPack = IconPack.MATERIAL_YOU,
    val shape: IconShape = IconShape.SQUIRCLE,
    val sizeDp: Float = 56f,
    val spacingDp: Float = 16f,
    val showLabels: Boolean = true
)

// Editor Models
enum class FilterMode(val label: String) {
    NORMAL("Normal"),
    GRAYSCALE("Grayscale"),
    SEPIA("Warm Sepia"),
    CYBERPUNK("Cyberpunk"),
    INVERT("Invert"),
    AMOLED_POP("AMOLED Boost"),
    VINTAGE("Vintage Fade"),
    COOL_BLUE("Cool Blue")
}

enum class AspectRatioOption(val label: String, val ratio: Float?) {
    FREE("Free", null),
    NINE_SIXTEEN("9:16 (Phone)", 9f / 16f),
    NINE_TWENTY("9:20 (Tall)", 9f / 20f),
    ONE_ONE("1:1 (Square)", 1f),
    FOUR_FIVE("4:5 (Post)", 4f / 5f),
    SIXTEEN_NINE("16:9 (Wide)", 16f / 9f)
}

enum class GradientOverlayOption(val label: String) {
    NONE("None"),
    SUNSET("Sunset Horizon"),
    CYBER_NEON("Cyber Neon"),
    MIDNIGHT_PURPLE("Midnight Purple"),
    BOTTOM_SHADOW("Bottom Text Shadow"),
    VIGNETTE_DARK("Deep Vignette"),
    AMOLED_GRADIENT("AMOLED Fade")
}

data class EditorState(
    val brightness: Float = 0f, // -1.0 to 1.0
    val contrast: Float = 1f, // 0.5 to 2.0
    val saturation: Float = 1f, // 0.0 to 2.0
    val blurRadius: Float = 0f, // 0 to 25
    val vignette: Float = 0f, // 0 to 1
    val filterMode: FilterMode = FilterMode.NORMAL,
    val rotationAngle: Float = 0f, // 0, 90, 180, 270
    val cropRatio: AspectRatioOption = AspectRatioOption.NINE_SIXTEEN,
    val gradientOverlay: GradientOverlayOption = GradientOverlayOption.NONE,
    val gradientOpacity: Float = 0.4f,
    val overlayText: String = "",
    val overlayTextColorHex: String = "#FFFFFF",
    val overlayTextSizeSp: Float = 24f,
    val overlayTextPosY: Float = 0.5f,
    val showClockStamp: Boolean = false,
    val showDateStamp: Boolean = false,
    val decorativeSticker: String = "" // e.g. "4K_ULTRA", "AMOLED", "MINIMAL_FRAME"
)
