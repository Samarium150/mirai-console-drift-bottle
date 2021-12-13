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
package io.github.samarium150.mirai.plugin

import io.github.samarium150.mirai.plugin.command.JumpInto
import io.github.samarium150.mirai.plugin.command.Pickup
import io.github.samarium150.mirai.plugin.command.ThrowAway
import io.github.samarium150.mirai.plugin.config.CommandConfig
import io.github.samarium150.mirai.plugin.config.GeneralConfig
import io.github.samarium150.mirai.plugin.config.ReplyConfig
import io.github.samarium150.mirai.plugin.data.Sea
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info

object MiraiConsoleDriftBottle : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.samarium150.mirai.plugin.mirai-console-drift-bottle",
        name = "Drift Bottle",
        version = "1.0.2",
    ) {
        author("Samarium150")
        info("简单的漂流瓶插件")
    }
) {
    override fun onEnable() {
        // 重载数据
        GeneralConfig.reload()
        ReplyConfig.reload()
        CommandConfig.reload()
        Sea.reload()

        // 注册命令
        JumpInto.register()
        Pickup.register()
        ThrowAway.register()

        logger.info { "Plugin loaded" }
    }

    override fun onDisable() {
        // 注销命令
        JumpInto.unregister()
        Pickup.unregister()
        ThrowAway.unregister()

        logger.info { "Plugin unloaded" }
    }
}
