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

import io.github.samarium150.baidu.aip.data.Credential
import io.github.samarium150.baidu.aip.data.URLS
import io.github.samarium150.baidu.aip.response.ImageCensorResponseBody
import io.github.samarium150.baidu.aip.response.ResponseBody
import io.github.samarium150.baidu.aip.response.TextCensorResponseBody
import io.github.samarium150.mirai.plugin.driftbottle.MiraiConsoleDriftBottle
import io.github.samarium150.mirai.plugin.driftbottle.config.ContentCensorConfig
import io.github.samarium150.mirai.plugin.driftbottle.data.ContentCensorToken
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*
import javax.imageio.ImageIO

object ContentCensor {

    private val client = MiraiConsoleDriftBottle.client

    private val logger = MiraiConsoleDriftBottle.logger

    private val oauth = "${URLS.OAUTH.url}?grant_type=client_credentials&" +
        "client_id=${ContentCensorConfig.API_KEY}&" +
        "client_secret=${ContentCensorConfig.SECRET_KEY}"

    private suspend fun getCredential() {
        val credential: Credential = runCatching {
            logger.info(oauth)
            client.get(oauth).body() as Credential
        }.onFailure {
            logger.error("获取AccessToken失败")
        }.onSuccess {
            logger.info("获取AccessToken成功")
        }.getOrThrow()
        ContentCensorToken.timestamp = Date().time
        ContentCensorToken.accessToken = credential.access_token
        ContentCensorToken.expiresIn = credential.expires_in
    }

    suspend fun determine(content: String): Boolean {
        val response: TextCensorResponseBody = client.submitForm(
            url = "${URLS.TEXT_CENSOR.url}?access_token=${ContentCensorToken.accessToken}",
            formParameters = parametersOf("text", content)
        ).body()
        logger.info(response.toString())
        return if (response.error_code != null) {
            logger.error(response.error_msg)
            true
        } else response.conclusion != "不合规"
    }

    suspend fun determine(chain: MessageChain): Boolean {
        if (Date().time >= ContentCensorToken.timestamp + ContentCensorToken.expiresIn * 1000
            || ContentCensorToken.accessToken.isEmpty()
        ) getCredential()
        var flag = true
        for (message in chain) {
            val response: ResponseBody = when (message) {
                is PlainText -> client.submitForm(
                    url = "${URLS.TEXT_CENSOR.url}?access_token=${ContentCensorToken.accessToken}",
                    formParameters = parametersOf("text", message.content)
                ).body<TextCensorResponseBody>()
                is Image -> {
                    val buffer = withContext(Dispatchers.IO) {
                        ImageIO.read(URL(message.queryUrl()))
                    }
                    val output = ByteArrayOutputStream()
                    withContext(Dispatchers.IO) {
                        ImageIO.write(buffer, message.imageType.name, output)
                    }
                    val base64 = Base64.getEncoder().encodeToString(output.toByteArray())
                    client.submitForm(
                        url = "${URLS.IMAGE_CENSOR.url}?access_token=${ContentCensorToken.accessToken}",
                        formParameters = parametersOf("image", base64)
                    ).body<ImageCensorResponseBody>()
                }
                else -> continue
            }
            logger.info(response.toString())
            if (response.error_code != null)
                logger.error(response.error_msg)
            else if (response.conclusion == "不合规") {
                flag = false
                break
            }
        }
        return flag
    }
}
