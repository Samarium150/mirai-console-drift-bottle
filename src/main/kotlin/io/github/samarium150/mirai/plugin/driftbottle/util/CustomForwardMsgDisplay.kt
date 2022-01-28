package io.github.samarium150.mirai.plugin.driftbottle.util

import io.github.samarium150.mirai.plugin.driftbottle.data.Item
import net.mamoe.mirai.message.data.ForwardMessage.DisplayStrategy
import net.mamoe.mirai.message.data.RawForwardMessage

class CustomForwardMsgDisplay(
    private val index: Int,
    private val item: Item
) : DisplayStrategy {
    override fun generateBrief(forward: RawForwardMessage): String = "[漂流瓶]"

    override fun generateSummary(forward: RawForwardMessage): String = "漂流瓶序号: $index"

    override fun generatePreview(forward: RawForwardMessage): List<String> {
        val subject = if (item.source == null) "此漂流瓶由私聊发送" else "此漂流瓶来自群: ${item.source}"
        val sender = "发送人是: ${item.owner.name}"
        val message = "点击打开漂流瓶"
        return listOf(subject, sender, message)
    }
}
