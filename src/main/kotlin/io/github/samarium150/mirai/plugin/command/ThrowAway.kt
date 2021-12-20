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
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain.Companion.serializeToJsonString
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.PlainText
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
        val commandSender = fromEvent.sender
        val group = fromEvent.subject
        val chain = if (messages.isNotEmpty()) messageChainOf(*messages)
        else {
                group.sendMessage("请输入你想放入漂流瓶的内容")
                fromEvent.nextMessage(30_000)
        }

        val owner = Owner(
            commandSender.id,
            commandSender.nick,
            commandSender.avatarUrl
        )
        val source = if (group is Group) Source(
            group.id,
            group.name
        ) else null
        val bottle = Item(Item.Type.BOTTLE, owner, source, chain.serializeToJsonString())
        Sea.contents.add(bottle)
        val builder = MessageChainBuilder()
        val parts = ReplyConfig.throwAway.split("%content")
        builder.append(PlainText(parts[0])).append(chain).append(parts[1])
        sendMessage(builder.asMessageChain())
    }
}
