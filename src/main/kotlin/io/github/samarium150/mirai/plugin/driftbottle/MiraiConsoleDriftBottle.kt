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
package io.github.samarium150.mirai.plugin.driftbottle

import io.github.samarium150.mirai.plugin.driftbottle.command.*
import io.github.samarium150.mirai.plugin.driftbottle.config.*
import io.github.samarium150.mirai.plugin.driftbottle.data.CommentData
import io.github.samarium150.mirai.plugin.driftbottle.data.ContentCensorToken
import io.github.samarium150.mirai.plugin.driftbottle.data.Sea
import io.github.samarium150.mirai.plugin.driftbottle.util.alsoSave
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin

object MiraiConsoleDriftBottle : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.samarium150.mirai.plugin.mirai-console-drift-bottle",
        name = "Drift Bottle",
        version = "1.9.0",
    ) {
        author("Samarium150")
        info("简单的漂流瓶插件")
    }
) {

    lateinit var client: HttpClient

    private fun init() {
        // 重载只读配置
        AdvancedConfig.alsoSave()
        GeneralConfig.alsoSave()
        ReplyConfig.alsoSave()
        CommandConfig.alsoSave()

        // 重载配置
        ContentCensorConfig.reload()

        // 重载数据
        ContentCensorToken.reload()
        CommentData.reload()
        Sea.reload()

        // 注册命令
        JumpInto.register()
        Pickup.register()
        ThrowAway.register()
        SeaOperation.register()
        if (GeneralConfig.incrementalBottle)
            Comment.register()
    }

    override fun onEnable() {
        // 初始化插件
        init()

        // 初始化 HTTP 客户端
        if (GeneralConfig.enableContentCensor)
            client = HttpClient {
                install(JsonFeature) {
                    serializer = KotlinxSerializer(
                        kotlinx.serialization.json.Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

        logger.info("Plugin loaded")
    }

    override fun onDisable() {
        // 注销命令
        JumpInto.unregister()
        Pickup.unregister()
        ThrowAway.unregister()

        // 关闭 HTTP 客户端
        if (GeneralConfig.enableContentCensor)
            client.close()

        logger.info("Plugin unloaded")
    }
}
