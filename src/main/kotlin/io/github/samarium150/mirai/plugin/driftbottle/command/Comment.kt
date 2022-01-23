package io.github.samarium150.mirai.plugin.driftbottle.command

import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle
import io.github.samarium150.mirai.plugin.driftbottle.config.CommandConfig
import io.github.samarium150.mirai.plugin.driftbottle.data.CommentData
import io.github.samarium150.mirai.plugin.driftbottle.util.indexOfBottle
import io.github.samarium150.mirai.plugin.driftbottle.util.isNotOutOfRange
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.nameCardOrNick

object Comment : SimpleCommand(
    MiraiConsoleDriftBottle,
    primaryName = "comment",
    secondaryNames = CommandConfig.comment,
    description = "评论漂流瓶"
) {
    private val comments by CommentData.Companion::comments

    @Handler
    suspend fun CommandSender.handle(
        index: Int? = subject?.let { sub ->
            indexOfBottle[sub.id]?.takeIf { it.isNotEmpty() }?.peek()?.minus(1)
        }, comment: String
    ) {
        if (isNotOutOfRange(index)) {
            val nick = user?.nameCardOrNick ?: "Console"
            val tempo = comments[index] ?: mutableListOf()
            tempo.add(index, CommentData(nick, comment))
            comments[index] = tempo
            sendMessage("已评论$index 漂流瓶") // 或许可由用户自行配置
        }
    }
}
