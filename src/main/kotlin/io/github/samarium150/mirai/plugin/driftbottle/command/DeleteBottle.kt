package io.github.samarium150.mirai.plugin.driftbottle.command

import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle
import io.github.samarium150.mirai.plugin.driftbottle.data.Sea
import io.github.samarium150.mirai.plugin.driftbottle.util.indexOfBottle
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.utils.error

object DeleteBottle : CompositeCommand(
    MiraiConsoleDriftBottle, "bottle", "b",
    description = "漂流瓶操作指令"
) {
    @SubCommand("del", "rm")
    suspend fun CommandSender.remove(index: Int? = subject?.let { indexOfBottle[it.id] }) {
        if (index == null) {
            subject?.sendMessage("删除失败，请尝试输入序号") ?: MiraiConsoleDriftBottle.logger.error { "控制台删除请输入序号" }
            return
        }
        if (index < 0 || index > Sea.contents.size) {
            sendMessage("数字超出范围！")
            return
        }
        val result = runCatching { Sea.contents.removeAt(index) }.onFailure { e ->
            if (e !is IndexOutOfBoundsException) MiraiConsoleDriftBottle.logger.error(e)
        }
        sendMessage(if (result.isSuccess) "已删除漂流瓶$index" else "删除漂流瓶$index 失败")
    }
}
