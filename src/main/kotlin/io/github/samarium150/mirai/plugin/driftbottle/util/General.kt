package io.github.samarium150.mirai.plugin.driftbottle.util

import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

internal enum class CacheType {
    IMAGE
}

internal val cacheFolder: File by lazy {
    val folder = MiraiConsoleDriftBottle.dataFolder.resolve("cache")
    if (!folder.exists())
        folder.mkdir()
    folder
}

internal val cacheFolderByType: (CacheType) -> File = {
    val folder = cacheFolder.resolve(it.name.lowercase())
    if (!folder.exists())
        folder.mkdir()
    folder
}

internal suspend fun saveImage(image: Image, file: File) = withContext(Dispatchers.IO) {
    URL(image.queryUrl()).openStream().use { input ->
        BufferedOutputStream(FileOutputStream(file)).use { out ->
            input.copyTo(out)
        }
    }
}
