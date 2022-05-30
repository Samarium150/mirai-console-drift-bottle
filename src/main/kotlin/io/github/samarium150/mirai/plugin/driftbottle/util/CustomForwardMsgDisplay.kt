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
package io.github.samarium150.mirai.plugin.driftbottle.util

import io.github.samarium150.mirai.plugin.driftbottle.data.Item
import net.mamoe.mirai.message.data.ForwardMessage.DisplayStrategy
import net.mamoe.mirai.message.data.RawForwardMessage

/**
 * @author LaoLittle
 */
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
