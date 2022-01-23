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
package io.github.samarium150.mirai.plugin.driftbottle.data

import io.github.samarium150.mirai.plugin.driftbottle.config.GeneralConfig
import io.github.samarium150.mirai.plugin.driftbottle.config.ReplyConfig
import io.github.samarium150.mirai.plugin.driftbottle.util.CacheType
import io.github.samarium150.mirai.plugin.driftbottle.util.cacheFolderByType
import io.github.samarium150.mirai.plugin.driftbottle.util.saveFrom
import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.net.URL
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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

    suspend fun toMessageChain(contact: Contact, index: Int): MessageChain {
        return buildMessageChain {
            when (type) {
                Type.BOTTLE -> {
                    var from = owner.name
                    if (source != null)
                        from = "${source}的$from\n"
                    else
                        from += "悄悄留下"
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
                    var comments = ""
                    CommentData.comments[index]?.let {
                        if (it.isNotEmpty()) comments += "\n此漂流瓶的评论为"
                        it.forEach { each ->
                            comments += "${each.sender}: ${each.content}"
                        }
                    }
                    add(ReplyConfig.pickupBottle.replace("%source", from).replace("%index", (index + 1).toString()))
                    add(MessageChain.deserializeFromJsonString(chainJson))
                    add(comments) // 本来想让用户自定义评论位置的，但是...摆了
                }
                Type.BODY -> {
                    val avatarStream = URL(owner.avatarUrl).openStream()
                    val img = avatarStream.use { it.uploadAsImage(contact) }
                    add(img)
                    add(
                        PlainText(
                            ReplyConfig.pickupBody
                                .replace("%who", owner.name)
                                .replace(
                                    "%time",
                                    Date(timestamp)
                                        .toInstant()
                                        .atZone(ZoneId.of("Asia/Shanghai"))
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                )
                        )
                    )
                    val where = if (source != null)
                        ReplyConfig.inGroup.replace("%group", source.toString())
                    else ReplyConfig.inPrivate
                    add(where)
                }
            }
        }
    }
}
