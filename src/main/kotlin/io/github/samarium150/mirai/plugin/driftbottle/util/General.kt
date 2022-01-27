/**
 * Copyright (c) 2020-2021 Samarium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package io.github.samarium150.mirai.plugin.driftbottle.util

import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle
import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle.reload
import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle.save
import io.github.samarium150.mirai.plugin.driftbottle.config.AdvancedConfig
import io.github.samarium150.mirai.plugin.driftbottle.config.GeneralConfig
import io.github.samarium150.mirai.plugin.driftbottle.data.Sea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.error
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

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

internal val forbidMessageKeys by lazy {
    mutableListOf<MessageKey<SingleMessage>>().apply {
        AdvancedConfig.cannotSaveMessageTypes.forEach { (type, bool) ->
            if (!bool) add(type.toMessageKey())
        }
    }.toTypedArray()
}

internal fun ReadOnlyPluginConfig.alsoSave() {
    reload()
    save()
}

@OptIn(ExperimentalContracts::class)
internal suspend fun CommandSender.isNotOutOfRange(index: Int?): Boolean {
    contract {
        returns(true) implies (index != null)
    }
    return when (index) {
        null -> {
            subject?.sendMessage("请尝试输入序号") ?: MiraiConsoleDriftBottle.logger.error { "控制台使用请输入序号" }
            false
        }
        !in 1..Sea.contents.size -> {
            sendMessage("数字超出范围！")
            false
        }
        else -> true
    }
}

internal val indexOfBottle = mutableMapOf<Long, Stack<Int>>()

internal fun Long.timestampToString(): String {
    return Date(this)
        .toInstant()
        .atZone(ZoneId.of("Asia/Shanghai"))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}
