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
import io.github.samarium150.mirai.plugin.driftbottle.config.CommandConfig
import io.github.samarium150.mirai.plugin.driftbottle.config.GeneralConfig
import io.github.samarium150.mirai.plugin.driftbottle.config.ReplyConfig
import io.github.samarium150.mirai.plugin.driftbottle.data.Item
import io.github.samarium150.mirai.plugin.driftbottle.data.Owner
import io.github.samarium150.mirai.plugin.driftbottle.data.Sea
import io.github.samarium150.mirai.plugin.driftbottle.data.Source
import io.github.samarium150.mirai.plugin.driftbottle.util.*
import kotlinx.coroutines.delay
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChain.Companion.serializeToJsonString
import net.mamoe.mirai.message.nextMessage

object ThrowAway : SimpleCommand(
    MiraiConsoleDriftBottle,
    primaryName = "throw-away",
    secondaryNames = CommandConfig.throwAway,
    description = "丢出漂流瓶"
) {

    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @Suppress("unused")
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(vararg messages: Message = arrayOf()) {
        val sender = fromEvent.sender
        val subject = fromEvent.subject
        if (!lock(sender.id)) return
        val chain = if (messages.isNotEmpty()) messageChainOf(*messages)
        else {
            randomDelay().also {
                sendMessage(ReplyConfig.waitForNextMessage)
            }
            runCatching {
                fromEvent.nextMessage(30_000)
            }.onFailure {
                sendMessage(ReplyConfig.timeoutMessage)
            }.getOrNull() ?: run {
                unlock(sender.id)
                return@handle
            }
        }
        if (GeneralConfig.enableContentCensor) runCatching {
            if (!ContentCensor.determine(chain)) {
                sendMessage(ReplyConfig.invalidMessage)
                return
            }
        }.onFailure {
            MiraiConsoleDriftBottle.logger.error(it)
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
        val chainJson = chain.serializeToJsonString()
        if (GeneralConfig.cacheImage)
            chain.forEach {
                if (it is Image)
                    cacheFolderByType(CacheType.IMAGE).resolve(it.imageId).saveFrom(it.queryUrl())
            }
        val bottle = Item(Item.Type.BOTTLE, owner, source, chainJson)
        Sea.contents.add(bottle)
        val parts = ReplyConfig.throwAway.split("%content")
        if (parts.size == 1) {
            randomDelay().also {
                sendMessage(parts[0]).also {
                    delay(GeneralConfig.perUse * 1000L)
                    unlock(sender.id)
                }
            }
        } else {
            randomDelay().also {
                sendMessage(buildMessageChain {
                    +PlainText(parts[0])
                    +disableAt(chain, subject)
                    +PlainText(parts[1])
                }).also {
                    delay(GeneralConfig.perUse * 1000L)
                    unlock(sender.id)
                }
            }
        }
    }
}
