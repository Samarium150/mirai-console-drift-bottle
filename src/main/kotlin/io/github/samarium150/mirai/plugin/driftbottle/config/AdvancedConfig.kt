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
package io.github.samarium150.mirai.plugin.driftbottle.config

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.PluginDataExtensions.withDefault
import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.message.data.MessageContent
import net.mamoe.mirai.message.data.MessageKey

object AdvancedConfig : ReadOnlyPluginConfig("Advanced") {

    @ValueDescription("At显示为纯文本")
    val disableDirectAt by value(false)

    @ConsoleExperimentalApi
    @ValueDescription(
        """
        漂流瓶消息类型控制
        可在此配置以下类型: 
        Image: 图片, LightApp: 小程序, Audio: 语音, FlashImage: 闪照
        MarketFace: 商城表情, Dice: 骰子, ForwardMessage: 合并转发消息
        FileMessage: 文件消息 (貌似漂流瓶无法存放文件，但还是先放着)
        """
    )
    val saveMessageTypes by value(MessageType.values().associateWith { true }).withDefault { true }

    @Serializable
    enum class MessageType {
        Image,
        LightApp,
        Audio,
        FlashImage,
        MarketFace,
        Dice,
        ForwardMessage,
        FileMessage,
        ;

        fun toMessageKey(): MessageKey<MessageContent> {
            return when (this) {
                Image -> net.mamoe.mirai.message.data.Image
                LightApp -> net.mamoe.mirai.message.data.LightApp
                Audio -> net.mamoe.mirai.message.data.Audio
                FlashImage -> net.mamoe.mirai.message.data.FlashImage
                MarketFace -> net.mamoe.mirai.message.data.MarketFace
                Dice -> net.mamoe.mirai.message.data.Dice
                ForwardMessage -> net.mamoe.mirai.message.data.ForwardMessage
                FileMessage -> net.mamoe.mirai.message.data.FileMessage
            }
        }
    }
}
