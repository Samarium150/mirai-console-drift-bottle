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

import io.github.samarium150.mirai.plugin.driftbottle.config.ReplyConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.ByteArrayOutputStream
import java.net.URL
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.imageio.ImageIO

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

    suspend fun toMessageChain(contact: Contact?): MessageChain {
        return buildMessageChain {
            when (type) {
                Type.BOTTLE -> {
                    var from = "$owner"
                    if (source != null)
                        from = "${source}的" + from
                    else
                        from += "悄悄留下"
                    add(ReplyConfig.pickupBottle.replace("%source", from))
                    add(MessageChain.deserializeFromJsonString(content!!))
                }
                Type.BODY -> {
                    val img = if (contact != null) {
                        val avatar = withContext(Dispatchers.IO) {
                            ImageIO.read(URL(owner.avatarUrl))
                        }
                        val inputStream = withContext(Dispatchers.IO) {
                            val byteArrayOutputStream = ByteArrayOutputStream()
                            ImageIO.write(avatar, "jpg", byteArrayOutputStream)
                            byteArrayOutputStream.toByteArray().inputStream()
                        }
                        inputStream.uploadAsImage(contact)
                    } else null
                    if (img != null) add(img)
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
