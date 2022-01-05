package io.github.samarium150.mirai.plugin.driftbottle.util

import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle
import java.io.File

enum class CacheType {
    IMAGE
}

val cacheFolder: File = MiraiConsoleDriftBottle.dataFolder.resolve("cache")

val cacheFolderByType: (CacheType) -> File = { cacheFolder.resolve(it.name.lowercase()) }
