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
package io.github.samarium150.mirai.plugin.driftbottle.command

import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle
import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle.logger
import io.github.samarium150.mirai.plugin.driftbottle.config.CommandConfig
import io.github.samarium150.mirai.plugin.driftbottle.config.GeneralConfig
import io.github.samarium150.mirai.plugin.driftbottle.config.ReplyConfig
import io.github.samarium150.mirai.plugin.driftbottle.data.Item
import io.github.samarium150.mirai.plugin.driftbottle.data.Owner
import io.github.samarium150.mirai.plugin.driftbottle.data.Sea
import io.github.samarium150.mirai.plugin.driftbottle.data.Source
import io.github.samarium150.mirai.plugin.driftbottle.util.ContentCensor
import kotlinx.coroutines.delay
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChain.Companion.serializeToJsonString
import net.mamoe.mirai.message.nextMessage
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.net.URL
import java.util.*


object ThrowAway : SimpleCommand(
    MiraiConsoleDriftBottle,
    primaryName = "throw-away",
    secondaryNames = CommandConfig.throwAway,
    description = "丢出漂流瓶"
) {
    private val inActive = mutableSetOf<User>()

    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @Suppress("unused")
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(vararg messages: Message = arrayOf()) {
        val sender = fromEvent.sender
        val subject = fromEvent.subject
        val chain = if (messages.isNotEmpty()) messageChainOf(*messages)
        else {
            sendMessage(ReplyConfig.waitForNextMessage)
            if (!inActive.add(sender)) return
            runCatching {
                fromEvent.nextMessage(30_000)
            }.onFailure {
                sendMessage(ReplyConfig.timeoutMessage)
            }.also {
                delay(100)
                inActive.remove(sender)
            }.getOrNull() ?: return
        }
        if (GeneralConfig.enableContentCensor) runCatching {
            if (!ContentCensor.determine(chain)) {
                sendMessage(ReplyConfig.invalidMessage)
                return
            }
        }.onFailure {
            logger.error(it)
        }
        val owner = Owner(
            sender.id,
            sender.nick,
            sender.avatarUrl
        )
        val source = if (subject is Group) Source(
            subject.id,
            subject.name
        ) else null
        // 考虑到使用的是Json
        var chainJson = chain.serializeToJsonString()
        chain.forEach {
            if (it is Image) {
                val uuid = UUID.randomUUID()
                val fileOutputStream = FileOutputStream(MiraiConsoleDriftBottle.dataFolder.resolve("$uuid.image"))
               URL(it.queryUrl()).openStream().use { input ->
                   BufferedOutputStream(fileOutputStream).use { out ->
                       input.copyTo(out)
                   }
               }
                chainJson = chainJson.replace(it.imageId, "%image$uuid.image%") // 貌似mirai是通过文件头读取而不是通过后缀判断
            }
        }
        val bottle = Item(Item.Type.BOTTLE, owner, source, chainJson)
        Sea.contents.add(bottle)
        val parts = ReplyConfig.throwAway.split("%content")
        sendMessage(buildMessageChain {
            +PlainText(parts[0])
            +chain
            +PlainText(parts[1])
        })
    }
}
