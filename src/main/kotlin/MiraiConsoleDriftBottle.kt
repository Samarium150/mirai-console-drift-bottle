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
package com.github.samarium150

import com.github.samarium150.command.JumpInto
import com.github.samarium150.command.Pickup
import com.github.samarium150.command.ThrowAway
import com.github.samarium150.config.CommandConfig
import com.github.samarium150.config.GeneralConfig
import com.github.samarium150.config.ReplyConfig
import com.github.samarium150.data.Sea
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info

object MiraiConsoleDriftBottle : KotlinPlugin(
    JvmPluginDescription(
        id = "com.github.samarium150.mirai-console-drift-bottle",
        name = "Drift Bottle",
        version = "1.0.1",
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
