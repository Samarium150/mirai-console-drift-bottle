package io.github.samarium150.mirai.plugin.driftbottle.util

import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle
import java.io.File

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
