package io.github.samarium150.mirai.plugin.driftbottle.util

import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle
import io.github.samarium150.mirai.plugin.driftbottle.config.AdvancedConfig
import io.github.samarium150.mirai.plugin.driftbottle.config.GeneralConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.toMessageChain
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.*

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

internal val randomDelay: suspend () -> Unit = {
    val (low, high) = GeneralConfig.randomDelayInterval
    if (low <= high)
        delay((low..high).random())
}

internal fun disableAt(messageChain: MessageChain, subject: Contact): MessageChain {
    return if (AdvancedConfig.disableDirectAt) {
        val group = if (subject is Group) subject else null
        val chain = messageChain.toMutableList()
        for (i in 0 until chain.size) {
            val message = chain[i]
            if (message is At)
                chain[i] = PlainText(message.getDisplay(group).replace("@", "At(").plus(")"))
        }
        chain.toMessageChain()
    } else messageChain
}

internal fun <E> Stack<E>.put(item: E): Stack<E> {
    addElement(item)
    return this
}

internal val indexOfBottle = mutableMapOf<Long, Stack<Int>>()
