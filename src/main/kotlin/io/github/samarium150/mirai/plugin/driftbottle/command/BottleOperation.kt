package io.github.samarium150.mirai.plugin.driftbottle.command

import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle
import io.github.samarium150.mirai.plugin.driftbottle.data.Sea
import io.github.samarium150.mirai.plugin.driftbottle.util.indexOfBottle
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.utils.error
import java.time.Instant
import java.time.ZoneOffset
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

object BottleOperation : CompositeCommand(
    MiraiConsoleDriftBottle, "bottle", "b",
    description = "漂流瓶操作指令"
) {
    @SubCommand("del", "rm")
    suspend fun CommandSender.remove(index: Int? = subject?.let { indexOfBottle[it.id] }) {
        if (isNotOutOfRange(index)) {
            val result = runCatching { Sea.contents.removeAt(index) }.onFailure { e ->
                if (e !is IndexOutOfBoundsException) MiraiConsoleDriftBottle.logger.error(e)
            }
            sendMessage(if (result.isSuccess) "已删除漂流瓶$index" else "删除漂流瓶$index 失败")
        }
    }

    @SubCommand("query", "see")
    suspend fun CommandSender.query(index: Int? = subject?.let { indexOfBottle[it.id] }) {
        if (isNotOutOfRange(index)) {
            val result = runCatching {
                val one = Sea.contents[index]
                """
                    漂流瓶序号: $index
                    ${one.source?.let { "漂流瓶发送群: ${it.name}(${it.id})" } ?: "此漂流瓶是私聊发送"}
                    漂流瓶发送人: ${one.owner.name}(${one.owner.id})
                    漂流瓶发送时间: ${Instant.ofEpochMilli(one.timestamp).atZone(ZoneOffset.of("+8")).toLocalDate()}
                """.trimIndent()
            }.onFailure { e ->
                if (e !is IndexOutOfBoundsException) MiraiConsoleDriftBottle.logger.error(e)
            }
            sendMessage(result.getOrNull() ?: "无法找到漂流瓶$index")
        }
    }

    @OptIn(ExperimentalContracts::class)
    private suspend fun CommandSender.isNotOutOfRange(index: Int?): Boolean {
        contract {
            returns(true) implies (index != null)
        }
        if (index == null) {
            subject?.sendMessage("请尝试输入序号") ?: MiraiConsoleDriftBottle.logger.error { "控制台使用请输入序号" }
            return false
        }
        if (index < 0 || index > Sea.contents.size) {
            sendMessage("数字超出范围！")
            return false
        }
        return true
    }
}
