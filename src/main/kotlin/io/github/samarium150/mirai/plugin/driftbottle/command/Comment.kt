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
import io.github.samarium150.mirai.plugin.driftbottle.data.CommentData
import io.github.samarium150.mirai.plugin.driftbottle.util.ContentCensor
import io.github.samarium150.mirai.plugin.driftbottle.util.indexOfBottle
import io.github.samarium150.mirai.plugin.driftbottle.util.isNotOutOfRange
import io.github.samarium150.mirai.plugin.driftbottle.util.randomDelay
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.SingleMessage

/**
 * @author LaoLittle
 */
object Comment : SimpleCommand(
    MiraiConsoleDriftBottle,
    primaryName = "comment",
    secondaryNames = CommandConfig.comment,
    description = "评论漂流瓶"
) {
    private val comments by CommentData.Companion::comments

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    override val usage: String
        get() = super.usage + " <内容> [漂流瓶序号]"

    @Suppress("unused")
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(
        comment: SingleMessage,
        index: Int? = indexOfBottle[fromEvent.subject.id]?.takeIf { it.isNotEmpty() }?.peek()?.plus(1)

    ) {
        if (comment !is PlainText) {
            sendMessage("评论只能包含纯文本！")
            return
        }
        val commentStr = comment.content
        if (GeneralConfig.enableContentCensor) runCatching {
            if (!ContentCensor.determine(commentStr)) {
                sendMessage(ReplyConfig.invalid)
                return
            }
        }.onFailure {
            MiraiConsoleDriftBottle.logger.error(it)
        }
        val realIndex = index?.minus(1)
        if (isNotOutOfRange(realIndex)) {
            val nick = fromEvent.sender.nameCardOrNick
            val id = fromEvent.sender.id
            comments[realIndex]?.add(CommentData(id, nick, commentStr)) ?: comments.put(
                realIndex,
                mutableListOf(CommentData(id, nick, commentStr))
            )
            randomDelay()
            sendMessage("已评论漂流瓶 $index") // 或许可由用户自行配置
        }
    }
}
