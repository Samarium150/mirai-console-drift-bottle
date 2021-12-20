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
package io.github.samarium150.mirai.plugin.command

import io.github.samarium150.mirai.plugin.MiraiConsoleDriftBottle
import io.github.samarium150.mirai.plugin.config.CommandConfig
import io.github.samarium150.mirai.plugin.config.ReplyConfig
import io.github.samarium150.mirai.plugin.data.Item
import io.github.samarium150.mirai.plugin.data.Owner
import io.github.samarium150.mirai.plugin.data.Sea
import io.github.samarium150.mirai.plugin.data.Source
import kotlinx.coroutines.TimeoutCancellationException
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain.Companion.serializeToJsonString
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.messageChainOf
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
        val chain = if (messages.isNotEmpty()) messageChainOf(*messages)
        else {
            sendMessage(ReplyConfig.waitForNextMessage)
            try {
                fromEvent.nextMessage(30_000)
            } catch (e: TimeoutCancellationException) {
                sendMessage(ReplyConfig.timeoutMessage)
                return
            }
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
        val bottle = Item(Item.Type.BOTTLE, owner, source, chain.serializeToJsonString())
        Sea.contents.add(bottle)
        val parts = ReplyConfig.throwAway.split("%content")
        sendMessage(buildMessageChain {
            +PlainText(parts[0])
            +chain
            +PlainText(parts[1])
        })
    }
}
