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
package io.github.samarium150.mirai.plugin.driftbottle.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object CommandConfig : AutoSavePluginConfig("Command") {

    @ValueDescription("jump-into命令的别名")
    val jumpInto: Array<String> by value(arrayOf("跳海", "跳进海里"))

    @ValueDescription("pickup命令的别名")
    val pickup: Array<String> by value(arrayOf("捡漂流瓶"))

    @ValueDescription("throw-away命令的别名")
    val throwAway: Array<String> by value(arrayOf("丢漂流瓶"))

}
