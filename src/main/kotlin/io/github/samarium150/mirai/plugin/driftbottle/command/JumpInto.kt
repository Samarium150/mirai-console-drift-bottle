/**
 * Copyright (c) 2020-2022 Samarium
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
import io.github.samarium150.mirai.plugin.driftbottle.util.lock
import io.github.samarium150.mirai.plugin.driftbottle.util.randomDelay
import io.github.samarium150.mirai.plugin.driftbottle.util.unlock
import kotlinx.coroutines.delay
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Group

object JumpInto : SimpleCommand(
    MiraiConsoleDriftBottle,
    primaryName = "jump-into",
    secondaryNames = CommandConfig.jumpInto,
    description = "查看有多少物体在海中"
) {
    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @Handler
    suspend fun CommandSender.handle() {
        val sender = user
        if (sender == null)
            sendMessage(ReplyConfig.jumpInto.replace("%num", Sea.contents.size.toString()))
        else {
            if (!lock(sender.id)) {
                sendMessage(ReplyConfig.inCooldown)
                return
            }
            val subject = subject
            val owner = Owner(
                sender.id,
                sender.nick,
                sender.avatarUrl
            )
            val source = if (subject is Group) Source(
                subject.id,
                subject.name
            ) else null
            val body = Item(Item.Type.BODY, owner, source)
            Sea.contents.add(body)
            runCatching {
                randomDelay()
                sendMessage(ReplyConfig.jumpInto.replace("%num", (Sea.contents.size - 1).toString()))
                delay(GeneralConfig.perUse * 1000L)
            }
            unlock(sender.id)
        }
    }
}
