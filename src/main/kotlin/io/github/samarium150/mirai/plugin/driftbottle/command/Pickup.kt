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
import io.github.samarium150.mirai.plugin.driftbottle.data.Sea
import io.github.samarium150.mirai.plugin.driftbottle.util.*
import kotlinx.coroutines.delay
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import java.util.*

object Pickup : SimpleCommand(
    MiraiConsoleDriftBottle,
    primaryName = "pickup",
    secondaryNames = CommandConfig.pickup,
    description = "捡起漂流瓶"
) {
    @ConsoleExperimentalApi
    @ExperimentalCommandDescriptors
    override val prefixOptional = true

    @Suppress("unused")
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(index: Int = Random().nextInt(Sea.contents.size) + 1) {
        val realIndex = index - 1
        if (isNotOutOfRange(realIndex)) {
            val sender = fromEvent.sender
            val subject = fromEvent.subject
            if (!lock(sender.id)) {
                sendMessage(ReplyConfig.overspeedMessage)
                return
            }
            if (Sea.contents.size == 0) {
                randomDelay()
                unlock(sender.id)
                sendMessage(ReplyConfig.noItem)
                return
            }
            val item = Sea.contents[realIndex]
            if ((item.type == Item.Type.BOTTLE && !GeneralConfig.incrementalBottle)
                || (item.type == Item.Type.BODY && !GeneralConfig.incrementalBody)
            ) Sea.contents.removeAt(realIndex)
            else indexOfBottle[subject.id]?.push(realIndex) ?: indexOfBottle.put(
                subject.id,
                Stack<Int>().put(realIndex)
            )
            runCatching {
                randomDelay()
                sendMessage(disableAt(item.toMessageChain(subject, realIndex), subject))
                delay(GeneralConfig.perUse * 1000L)
            }
            unlock(sender.id)
        }
    }
}
