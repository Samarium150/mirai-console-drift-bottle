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
package com.github.samarium150.command

import com.github.samarium150.MiraiConsoleDriftBottle
import com.github.samarium150.config.CommandConfig
import com.github.samarium150.config.ReplyConfig
import com.github.samarium150.data.Item
import com.github.samarium150.data.Owner
import com.github.samarium150.data.Sea
import com.github.samarium150.data.Source
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User

object JumpInto : SimpleCommand(
    MiraiConsoleDriftBottle,
    primaryName = "jump-into",
    secondaryNames = CommandConfig.jumpInto,
    description = "查看有多少物体在海中"
) {
    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @Suppress("unused")
    @Handler
    suspend fun CommandSender.handle() {
        val owner = if (user != null) Owner(
            (user as User).id,
            (user as User).nick,
            (user as User).avatarUrl,
        ) else null
        if (owner == null)
            sendMessage(ReplyConfig.jumpInto.replace("%num", Sea.contents.size.toString()))
        else {
            val source = if (subject is Group) Source(
                (subject as Group).id,
                (subject as Group).name,
            ) else null
            val body = Item(Item.Type.BODY, owner, source)
            Sea.contents.add(body)
            sendMessage(ReplyConfig.jumpInto.replace("%num", Sea.contents.size.toString()))
        }
    }
}
