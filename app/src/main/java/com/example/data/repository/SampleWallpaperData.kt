package com.example.data.repository

import com.example.model.Wallpaper
import com.example.model.WallpaperCategory
import com.example.model.WallpaperResolution

object SampleWallpaperData {
    val wallpapers: List<Wallpaper> = listOf(
        // GAMING
        Wallpaper(
            id = "w_gaming_1",
            title = "Cyber Katana Neon",
            author = "ApexPixel",
            category = WallpaperCategory.GAMING.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("gaming", "neon", "cyberpunk", "katana", "rgb", "pc"),
            dominantColors = listOf(0xFF0F051D, 0xFFFF007F, 0xFF00F0FF),
            viewsCount = 18450,
            downloadsCount = 8920,
            likesCount = 4210,
            isFeatured = true,
            isTrending = true
        ),
        Wallpaper(
            id = "w_gaming_2",
            title = "Retro Synth Arcade",
            author = "PixelForge",
            category = WallpaperCategory.GAMING.id,
            resolution = WallpaperResolution.QHD,
            thumbnailUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("gaming", "retro", "arcade", "vintage", "synthwave"),
            dominantColors = listOf(0xFF1E1035, 0xFFFF0055, 0xFF00FFCC),
            viewsCount = 12300,
            downloadsCount = 4120,
            likesCount = 2890,
            isLatest = true
        ),

        // CARS
        Wallpaper(
            id = "w_cars_1",
            title = "Obsidian Hypercar Night",
            author = "VelocityArt",
            category = WallpaperCategory.CARS.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1617788138017-80ad40651399?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1617788138017-80ad40651399?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("cars", "supercar", "hypercar", "dark", "luxury", "speed"),
            dominantColors = listOf(0xFF0B0D12, 0xFFFF3366, 0xFF1C2430),
            viewsCount = 28900,
            downloadsCount = 14500,
            likesCount = 7600,
            isFeatured = true,
            isTrending = true
        ),
        Wallpaper(
            id = "w_cars_2",
            title = "Golden Hour Classic Roadster",
            author = "DriveTribe",
            category = WallpaperCategory.CARS.id,
            resolution = WallpaperResolution.FHD,
            thumbnailUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("cars", "porsche", "vintage", "sunset", "road"),
            dominantColors = listOf(0xFF2C1810, 0xFFE67E22, 0xFFF39C12),
            viewsCount = 9400,
            downloadsCount = 3800,
            likesCount = 1950
        ),

        // NATURE
        Wallpaper(
            id = "w_nature_1",
            title = "Emerald Alpine Peaks",
            author = "EarthShots",
            category = WallpaperCategory.NATURE.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("nature", "mountains", "alps", "fog", "serene", "green"),
            dominantColors = listOf(0xFF132018, 0xFF2E5339, 0xFF88D49E),
            viewsCount = 31200,
            downloadsCount = 19200,
            likesCount = 9800,
            isFeatured = true
        ),
        Wallpaper(
            id = "w_nature_2",
            title = "Bioluminescent Shore",
            author = "Oceanic",
            category = WallpaperCategory.NATURE.id,
            resolution = WallpaperResolution.AMOLED,
            thumbnailUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("nature", "ocean", "waves", "beach", "night", "glow"),
            dominantColors = listOf(0xFF030D1A, 0xFF00F0FF, 0xFF006699),
            viewsCount = 15600,
            downloadsCount = 7400,
            likesCount = 4100,
            isAmoled = true,
            isTrending = true
        ),

        // SPACE
        Wallpaper(
            id = "w_space_1",
            title = "Carina Nebula Pillars",
            author = "CosmosDeep",
            category = WallpaperCategory.SPACE.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("space", "nebula", "galaxy", "stars", "cosmos", "deep"),
            dominantColors = listOf(0xFF060515, 0xFF6C5CE7, 0xFF00D2FF),
            viewsCount = 44200,
            downloadsCount = 26500,
            likesCount = 15200,
            isFeatured = true,
            isTrending = true
        ),
        Wallpaper(
            id = "w_space_2",
            title = "Deep Void Ring Planet",
            author = "AstroVerse",
            category = WallpaperCategory.SPACE.id,
            resolution = WallpaperResolution.AMOLED,
            thumbnailUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("space", "planet", "saturn", "void", "black", "amoled"),
            dominantColors = listOf(0xFF000000, 0xFF8A2BE2, 0xFF333344),
            viewsCount = 19800,
            downloadsCount = 9900,
            likesCount = 5800,
            isAmoled = true
        ),

        // TECHNOLOGY
        Wallpaper(
            id = "w_tech_1",
            title = "Quantum Circuit Core",
            author = "NanoSilicon",
            category = WallpaperCategory.TECHNOLOGY.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("tech", "circuit", "chip", "hardware", "gold", "cyber"),
            dominantColors = listOf(0xFF10141A, 0xFF00E5FF, 0xFFFFD700),
            viewsCount = 16700,
            downloadsCount = 8100,
            likesCount = 4300,
            isLatest = true
        ),
        Wallpaper(
            id = "w_tech_2",
            title = "Matrix Optical Fiber Glow",
            author = "Photonics",
            category = WallpaperCategory.TECHNOLOGY.id,
            resolution = WallpaperResolution.QHD,
            thumbnailUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("tech", "data", "code", "fiber", "matrix", "green"),
            dominantColors = listOf(0xFF000D05, 0xFF00FF66, 0xFF003311),
            viewsCount = 11900,
            downloadsCount = 5200,
            likesCount = 2800
        ),

        // ANIME
        Wallpaper(
            id = "w_anime_1",
            title = "Cherry Blossom Shinto Shrine",
            author = "TokyoDream",
            category = WallpaperCategory.ANIME.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("anime", "japan", "shrine", "sakura", "aesthetic", "torii"),
            dominantColors = listOf(0xFF2A1520, 0xFFFF7597, 0xFFFFB7B2),
            viewsCount = 37800,
            downloadsCount = 21400,
            likesCount = 12900,
            isFeatured = true,
            isTrending = true
        ),
        Wallpaper(
            id = "w_anime_2",
            title = "Neo Tokyo Rooftop Sunset",
            author = "MangaNova",
            category = WallpaperCategory.ANIME.id,
            resolution = WallpaperResolution.QHD,
            thumbnailUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("anime", "tokyo", "city", "sunset", "clouds", "rooftop"),
            dominantColors = listOf(0xFF321A3B, 0xFFFF6584, 0xFFFCD34D),
            viewsCount = 24600,
            downloadsCount = 11800,
            likesCount = 6700,
            isLatest = true
        ),

        // MOVIES
        Wallpaper(
            id = "w_movies_1",
            title = "Dune Desert Monarch",
            author = "CinemaLover",
            category = WallpaperCategory.MOVIES.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("movies", "dune", "desert", "cinematic", "sand", "epic"),
            dominantColors = listOf(0xFF33200D, 0xFFC67D34, 0xFFE0A96D),
            viewsCount = 21000,
            downloadsCount = 9800,
            likesCount = 5200,
            isFeatured = true
        ),
        Wallpaper(
            id = "w_movies_2",
            title = "Gotham Rain Alley",
            author = "DarkKnightArt",
            category = WallpaperCategory.MOVIES.id,
            resolution = WallpaperResolution.AMOLED,
            thumbnailUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("movies", "batman", "rain", "dark", "noir", "city"),
            dominantColors = listOf(0xFF05070A, 0xFF1E2836, 0xFFEAB308),
            viewsCount = 18900,
            downloadsCount = 8200,
            likesCount = 4700,
            isAmoled = true
        ),

        // ANIMALS
        Wallpaper(
            id = "w_animals_1",
            title = "Midnight Golden Eagle",
            author = "FaunaLens",
            category = WallpaperCategory.ANIMALS.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("animals", "eagle", "birds", "wildlife", "eyes", "majestic"),
            dominantColors = listOf(0xFF121212, 0xFFD4AF37, 0xFF4A3E2C),
            viewsCount = 14300,
            downloadsCount = 6100,
            likesCount = 3400
        ),
        Wallpaper(
            id = "w_animals_2",
            title = "Snow Leopard Gaze",
            author = "ArcticWild",
            category = WallpaperCategory.ANIMALS.id,
            resolution = WallpaperResolution.QHD,
            thumbnailUrl = "https://images.unsplash.com/photo-1561731216-c3a4d99437d5?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1561731216-c3a4d99437d5?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("animals", "leopard", "wildlife", "snow", "cat", "predator"),
            dominantColors = listOf(0xFF1E2229, 0xFF94A3B8, 0xFFE2E8F0),
            viewsCount = 22100,
            downloadsCount = 10400,
            likesCount = 6100,
            isLatest = true
        ),

        // ABSTRACT
        Wallpaper(
            id = "w_abstract_1",
            title = "Liquid Hologram Fluid",
            author = "PrismWave",
            category = WallpaperCategory.ABSTRACT.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("abstract", "liquid", "hologram", "vibrant", "gradient", "3d"),
            dominantColors = listOf(0xFF140727, 0xFFFF007F, 0xFF7928CA),
            viewsCount = 33900,
            downloadsCount = 18700,
            likesCount = 9200,
            isFeatured = true,
            isTrending = true
        ),
        Wallpaper(
            id = "w_abstract_2",
            title = "Geometric Chrome Waves",
            author = "SphereCore",
            category = WallpaperCategory.ABSTRACT.id,
            resolution = WallpaperResolution.AMOLED,
            thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("abstract", "chrome", "waves", "metal", "amoled", "smooth"),
            dominantColors = listOf(0xFF000000, 0xFF00F0FF, 0xFF555566),
            viewsCount = 17500,
            downloadsCount = 7900,
            likesCount = 4300,
            isAmoled = true
        ),

        // MINIMAL
        Wallpaper(
            id = "w_minimal_1",
            title = "Solitary Dune Curve",
            author = "LessIsMore",
            category = WallpaperCategory.MINIMAL.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("minimal", "clean", "calm", "zen", "sand", "soft"),
            dominantColors = listOf(0xFFF7F4EB, 0xFFD3C5B4, 0xFF8A7E72),
            viewsCount = 19300,
            downloadsCount = 8900,
            likesCount = 5100
        ),
        Wallpaper(
            id = "w_minimal_2",
            title = "Monochrome Geometric Horizon",
            author = "BauhausLab",
            category = WallpaperCategory.MINIMAL.id,
            resolution = WallpaperResolution.AMOLED,
            thumbnailUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("minimal", "black", "white", "lines", "amoled", "modern"),
            dominantColors = listOf(0xFF000000, 0xFFFFFFFF, 0xFF222222),
            viewsCount = 28400,
            downloadsCount = 14300,
            likesCount = 8200,
            isAmoled = true,
            isTrending = true
        ),

        // DARK
        Wallpaper(
            id = "w_dark_1",
            title = "Midnight Velvet Shadow",
            author = "Nocturnal",
            category = WallpaperCategory.DARK.id,
            resolution = WallpaperResolution.AMOLED,
            thumbnailUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("dark", "amoled", "velvet", "night", "pure", "deep"),
            dominantColors = listOf(0xFF020205, 0xFF0D111A, 0xFF3B82F6),
            viewsCount = 39100,
            downloadsCount = 22400,
            likesCount = 13800,
            isAmoled = true,
            isFeatured = true
        ),
        Wallpaper(
            id = "w_dark_2",
            title = "Stealth Carbon Texture",
            author = "DarkForge",
            category = WallpaperCategory.DARK.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("dark", "carbon", "texture", "stealth", "matte", "weave"),
            dominantColors = listOf(0xFF0F0F12, 0xFF1A1A22, 0xFF282834),
            viewsCount = 15200,
            downloadsCount = 6800,
            likesCount = 3900,
            isLatest = true
        ),

        // CITY
        Wallpaper(
            id = "w_city_1",
            title = "Shinjuku Rain Symphony",
            author = "TokyoLights",
            category = WallpaperCategory.CITY.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("city", "tokyo", "neon", "rain", "night", "street"),
            dominantColors = listOf(0xFF100A1F, 0xFFFF007F, 0xFF00E5FF),
            viewsCount = 41200,
            downloadsCount = 23900,
            likesCount = 14600,
            isFeatured = true,
            isTrending = true
        ),
        Wallpaper(
            id = "w_city_2",
            title = "Manhattan Skyline Twilight",
            author = "GothamLens",
            category = WallpaperCategory.CITY.id,
            resolution = WallpaperResolution.QHD,
            thumbnailUrl = "https://images.unsplash.com/photo-1477959858617-67f30bc75b82?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1477959858617-67f30bc75b82?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("city", "newyork", "skyline", "twilight", "skyscrapers", "architecture"),
            dominantColors = listOf(0xFF14192A, 0xFF3E5C76, 0xFFFDBB2D),
            viewsCount = 18200,
            downloadsCount = 8400,
            likesCount = 4900
        ),

        // LUXURY
        Wallpaper(
            id = "w_luxury_1",
            title = "Royal Gold Marble Veins",
            author = "OpulenceDesign",
            category = WallpaperCategory.LUXURY.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("luxury", "gold", "marble", "royal", "rich", "elegant"),
            dominantColors = listOf(0xFF0F1115, 0xFFD4AF37, 0xFF2A2825),
            viewsCount = 26500,
            downloadsCount = 13800,
            likesCount = 8100,
            isFeatured = true
        ),
        Wallpaper(
            id = "w_luxury_2",
            title = "Platinum Tourbillon Horology",
            author = "HauteWatch",
            category = WallpaperCategory.LUXURY.id,
            resolution = WallpaperResolution.QHD,
            thumbnailUrl = "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("luxury", "watch", "tourbillon", "platinum", "craft", "mechanic"),
            dominantColors = listOf(0xFF18181B, 0xFF94A3B8, 0xFFE2E8F0),
            viewsCount = 13400,
            downloadsCount = 5900,
            likesCount = 3200,
            isLatest = true
        ),

        // 4K
        Wallpaper(
            id = "w_4k_1",
            title = "Cerulean Glacier Caves 4K",
            author = "ArcticSphere",
            category = WallpaperCategory.K4.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1517411032315-54ef2cb783bb?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1517411032315-54ef2cb783bb?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("4k", "ice", "cave", "glacier", "blue", "ultra"),
            dominantColors = listOf(0xFF031926, 0xFF00A8E8, 0xFF468189),
            viewsCount = 35200,
            downloadsCount = 18900,
            likesCount = 11200,
            isFeatured = true,
            isTrending = true
        ),

        // AMOLED
        Wallpaper(
            id = "w_amoled_1",
            title = "Zero Kelvin Supernova",
            author = "OledCraft",
            category = WallpaperCategory.AMOLED.id,
            resolution = WallpaperResolution.AMOLED,
            thumbnailUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("amoled", "black", "pure", "neon", "supernova", "minimal"),
            dominantColors = listOf(0xFF000000, 0xFFFF007F, 0xFF00F0FF),
            viewsCount = 48500,
            downloadsCount = 31200,
            likesCount = 19400,
            isAmoled = true,
            isFeatured = true,
            isTrending = true
        ),

        // AI WALLPAPERS (Special showcase)
        Wallpaper(
            id = "w_ai_1",
            title = "Futuristic Cyber Supercar Night",
            author = "Wallora AI Engine",
            category = WallpaperCategory.CARS.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1617788138017-80ad40651399?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1617788138017-80ad40651399?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("ai", "supercar", "neon", "night", "cyberpunk", "prompt"),
            dominantColors = listOf(0xFF0A0A14, 0xFFFF0055, 0xFF00D2FF),
            viewsCount = 29500,
            downloadsCount = 17100,
            likesCount = 10800,
            isAiGenerated = true,
            isFeatured = true,
            isTrending = true,
            description = "Generated with Wallora AI: 'Futuristic black supercar in a neon city at night, cinematic lighting, realistic, vertical phone wallpaper.'"
        ),
        Wallpaper(
            id = "w_ai_2",
            title = "Celestial Cosmic Cyberpunk City",
            author = "Wallora AI Engine",
            category = WallpaperCategory.SPACE.id,
            resolution = WallpaperResolution.UHD_4K,
            thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop&q=80",
            fullUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=2160&auto=format&fit=crop&q=95",
            tags = listOf("ai", "cosmic", "space", "nebula", "city", "future"),
            dominantColors = listOf(0xFF09061C, 0xFF8A2BE2, 0xFF00F0FF),
            viewsCount = 21300,
            downloadsCount = 12400,
            likesCount = 7900,
            isAiGenerated = true,
            isLatest = true,
            description = "Generated with Wallora AI: 'Orbital metropolis suspended in an iridescent cosmic nebula, 4k ultra detailed vertical wallpaper.'"
        )
    )
}
