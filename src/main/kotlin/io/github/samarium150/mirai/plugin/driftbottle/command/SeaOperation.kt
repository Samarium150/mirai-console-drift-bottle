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
import io.github.samarium150.mirai.plugin.driftbottle.data.Item
import io.github.samarium150.mirai.plugin.driftbottle.data.Sea
import io.github.samarium150.mirai.plugin.driftbottle.util.*
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

/**
 * @author LaoLittle
 */
@Suppress("unused")
object SeaOperation : CompositeCommand(
    MiraiConsoleDriftBottle,
    primaryName = "sea",
    secondaryNames = CommandConfig.seaOperation,
    description = "漂流瓶操作复合指令"
) {

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    @SubCommand("del", "rm")
    suspend fun CommandSender.remove(
        index: Int? = subject?.let { sub ->
            indexOfBottle[sub.id]?.takeIf { it.isNotEmpty() }?.pop()?.plus(1)
        }
    ) {
        val realIndex = index?.minus(1)
        if (isNotOutOfRange(realIndex)) {
            val result = runCatching {
                Sea.contents.removeAt(realIndex)
                rearrangeComments(realIndex)
            }.onFailure { e ->
                if (e !is IndexOutOfBoundsException) MiraiConsoleDriftBottle.logger.error(e)
            }
            randomDelay()
            sendMessage(if (result.isSuccess) "已删除漂流瓶$index" else "删除漂流瓶$index 失败")
        }
    }

    @SubCommand("query", "get")
    suspend fun CommandSender.query(
        index: Int? = subject?.let { sub ->
            indexOfBottle[sub.id]?.takeIf { it.isNotEmpty() }?.peek()?.plus(1)
        }
    ) {
        val realIndex = index?.minus(1)
        if (isNotOutOfRange(realIndex)) {
            val result = runCatching {
                val item = Sea.contents[realIndex]
                """
                    漂流瓶序号: $index
                    ${item.source?.let { "漂流瓶发送群: ${it.name}(${it.id})" } ?: "此漂流瓶是私聊发送"}
                    漂流瓶发送人: ${item.owner.name}(${item.owner.id})
                    漂流瓶发送时间: ${item.timestamp.timestampToString()}
                """.trimIndent()
            }.onFailure { e ->
                if (e !is IndexOutOfBoundsException) MiraiConsoleDriftBottle.logger.error(e)
            }
            sendMessage(result.getOrNull() ?: "无法找到漂流瓶$index")
        }
    }

    @SubCommand("status")
    suspend fun CommandSender.status() {
        sendMessage(
            """
            目前海中物品总数: ${Sea.contents.size}
            其中漂流瓶数为: ${Sea.contents.count { it.type == Item.Type.BOTTLE }}
            尸体数为: ${Sea.contents.count { it.type == Item.Type.BODY }}
            """.trimIndent()
        )
    }
}
