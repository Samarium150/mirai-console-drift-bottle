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

import io.github.samarium150.mirai.plugin.driftbottle.command.JumpInto
import io.github.samarium150.mirai.plugin.driftbottle.command.Pickup
import io.github.samarium150.mirai.plugin.driftbottle.command.ThrowAway
import io.github.samarium150.mirai.plugin.driftbottle.config.CommandConfig
import io.github.samarium150.mirai.plugin.driftbottle.config.ContentCensorConfig
import io.github.samarium150.mirai.plugin.driftbottle.config.GeneralConfig
import io.github.samarium150.mirai.plugin.driftbottle.config.ReplyConfig
import io.github.samarium150.mirai.plugin.driftbottle.data.ContentCensorToken
import io.github.samarium150.mirai.plugin.driftbottle.data.Sea
import io.github.samarium150.mirai.plugin.driftbottle.util.CacheType
import io.github.samarium150.mirai.plugin.driftbottle.util.cacheFolder
import io.github.samarium150.mirai.plugin.driftbottle.util.cacheFolderByType
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
        version = "1.2.1",
    ) {
        author("Samarium150")
        info("简单的漂流瓶插件")
    }
) {

    lateinit var client: HttpClient

    override fun onEnable() {
        // 重载数据
        GeneralConfig.reload()
        ReplyConfig.reload()
        CommandConfig.reload()
        ContentCensorConfig.reload()
        ContentCensorToken.reload()
        Sea.reload()

        // 注册命令
        JumpInto.register()
        Pickup.register()
        ThrowAway.register()

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

        if (!cacheFolder.exists())
            cacheFolder.mkdir()
        CacheType.values().forEach {
            val cache = cacheFolderByType(it)
            if (!cache.exists())
                cache.mkdir()
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
