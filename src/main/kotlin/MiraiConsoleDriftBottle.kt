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

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info

object MiraiConsoleDriftBottle : KotlinPlugin(
    JvmPluginDescription(
        id = "com.github.samarium150.mirai-console-drift-bottle",
        name = "Mirai Console Drift Bottle",
        version = "1.0.0",
    ) {
        author("Samarium150")
        info("简单的漂流瓶插件")
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
    }

    override fun onDisable() {
        logger.info { "Plugin unloaded" }
    }
}
