package io.github.samarium150.mirai.plugin.driftbottle.util

import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

internal suspend fun File.saveFrom(url: String) = withContext(Dispatchers.IO) {
    URL(url).openStream().use { input ->
        BufferedOutputStream(FileOutputStream(this@saveFrom)).use { output ->
            input.copyTo(output)
        }
    }
}

internal val active = mutableSetOf<Long>()
