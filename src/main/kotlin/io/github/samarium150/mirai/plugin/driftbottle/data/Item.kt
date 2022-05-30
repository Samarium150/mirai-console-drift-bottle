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
package io.github.samarium150.mirai.plugin.driftbottle.data

import io.github.samarium150.mirai.plugin.driftbottle.config.GeneralConfig
import io.github.samarium150.mirai.plugin.driftbottle.config.ReplyConfig
import io.github.samarium150.mirai.plugin.driftbottle.data.Item.Type.BODY
import io.github.samarium150.mirai.plugin.driftbottle.data.Item.Type.BOTTLE
import io.github.samarium150.mirai.plugin.driftbottle.util.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.net.URL
import java.util.*

@Serializable
class Item {

    enum class Type {
        BOTTLE,
        BODY
    }

    val type: Type
    val owner: Owner
    val source: Source?
    private var content: String? = null
    val timestamp = Date().time

    constructor(type: Type, owner: Owner, source: Source?) {
        this.type = type
        this.owner = owner
        this.source = source
    }

    constructor(type: Type, owner: Owner, source: Source?, content: String?) {
        this.type = type
        this.owner = owner
        this.source = source
        this.content = content
    }

    suspend fun toMessage(contact: Contact, index: Int): Message {
        val messageChain = buildMessageChain {
            when (type) {
                BOTTLE -> {
                    var chainJson = content ?: throw NoSuchElementException("未知错误")
                    if (GeneralConfig.cacheImage)
                        Image.IMAGE_ID_REGEX.findAll(chainJson).forEach {
                            val imageId = it.value.trimEnd('}', '"')
                            val file = cacheFolderByType(CacheType.IMAGE).resolve(imageId)
                            runCatching {
                                val image = file.uploadAsImage(contact)
                                if (imageId != image.imageId)
                                    chainJson = chainJson.replace(imageId, image.imageId)
                            }.onFailure {
                                val image = Image(imageId)
                                file.saveFrom(image.queryUrl())
                            }
                        }
                    if (!GeneralConfig.displayInForward) {
                        var from = owner.name
                        if (source != null)
                            from = "${source}的$from\n"
                        else
                            from += "悄悄留下"
                        var comments = ""
                        CommentData.comments[index]?.let {
                            if (it.isNotEmpty()) comments += "\n此漂流瓶的评论为"
                            it.forEach { each ->
                                comments += "\n${each.senderName}: ${each.content}"
                            }
                        }
                        add(
                            ReplyConfig.pickupBottle
                                .replace("%source", from)
                                .replace("%index", (index + 1).toString())
                        )
                        add(MessageChain.deserializeFromJsonString(chainJson))
                        add(comments) // 本来想让用户自定义评论位置的，但是...摆了
                    } else add(MessageChain.deserializeFromJsonString(chainJson))
                }
                BODY -> {
                    val avatarStream = URL(owner.avatarUrl).openStream()
                    val img = avatarStream.use { it.uploadAsImage(contact) }
                    add(img)
                    add(
                        PlainText(
                            ReplyConfig.pickupBody
                                .replace("%who", owner.name)
                                .replace("%time", timestamp.timestampToString())
                        )
                    )
                    val where = if (source != null)
                        ReplyConfig.inGroup.replace("%group", source.toString())
                    else ReplyConfig.inPrivate
                    add(where)
                }
            }
        }
        return if (!GeneralConfig.displayInForward || messageChain.any {
                when (it) {
                    is Audio, is FlashImage -> true
                    else -> false
                }
            }) messageChain
        else buildForwardMessage(contact, CustomForwardMsgDisplay(index + 1, this)) {
            add(owner.id, owner.name, messageChain, timestamp.seconds)
            CommentData.comments[index]?.let {
                it.forEach { each ->
                    add(each.senderId, each.senderName, PlainText(each.content), each.timestamp.seconds)
                }
            }
        }
    }
}
