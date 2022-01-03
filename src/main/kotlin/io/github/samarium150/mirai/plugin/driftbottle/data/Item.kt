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

import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle
import io.github.samarium150.mirai.plugin.driftbottle.config.ReplyConfig
import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.Contact
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

    private val type: Type
    private val owner: Owner
    private val source: Source?
    private var content: String? = null
    private val timestamp = Date().time

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

    suspend fun toMessageChain(contact: Contact): MessageChain {
        return buildMessageChain {
            when (type) {
                Type.BOTTLE -> {
                    var from = "$owner"
                    if (source != null)
                        from = "${source}的" + from
                    else
                        from += "悄悄留下"
                    var chainJson = content ?: throw NoSuchElementException("未知错误")
                    while (chainJson.contains("%image")) {
                        val left = chainJson.indexOf("%image") + 6
                        val right = chainJson.indexOf("%", left)
                        val fileName = chainJson.substring(left, right)
                        println(fileName)
                        val image = MiraiConsoleDriftBottle.dataFolder.resolve(fileName).uploadAsImage(contact)
                        chainJson = chainJson.replace("%image$fileName%", image.imageId)
                    }
                    add(ReplyConfig.pickupBottle.replace("%source", from))
                    add(MessageChain.deserializeFromJsonString(chainJson))
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
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss O"))
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
