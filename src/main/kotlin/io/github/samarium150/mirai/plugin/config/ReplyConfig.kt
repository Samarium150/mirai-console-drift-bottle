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
package io.github.samarium150.mirai.plugin.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object ReplyConfig : AutoSavePluginConfig("Reply") {

    @ValueDescription("海里没有物品时的回复")
    val noItem: String by value("海里暂时没有物品哦~")

    @ValueDescription("捡起漂流瓶的回复")
    val pickupBottle: String by value("你在海边捡到了一个来自【%source】的漂流瓶，打开瓶子，里面有一张纸条，写着：\n")

    @ValueDescription("捡起尸体的回复")
    val pickupBody: String by value("海面飘来了【%who】的浮尸……\n他于【%time】")

    @ValueDescription("在私聊中跳海的修饰语")
    val inPrivate: String by value("悄悄潜入深海……\n愿深蓝之意志保佑他的灵魂。")

    @ValueDescription("在群聊中跳海的修饰语")
    val inGroup: String by value("在【%group】处的海边沉入深海……")

    @ValueDescription("丢漂流瓶的回复")
    val throwAway: String by value("你将一个写着【%content】的纸条塞入瓶中扔进大海，希望有人捞到吧~")

    @ValueDescription("跳进大海的回复")
    val jumpInto: String by value(
        "你缓缓走入大海，感受着海浪轻柔地拍打着你的小腿，膝盖……\n" +
            "波浪卷着你的腰腹，你感觉有些把握不住平衡了……\n" +
            "……\n" +
            "你沉入海中，【%num】个物体与你一同沉浮。\n" +
            "不知何处涌来一股暗流，你失去了意识。"
    )

    @ValueDescription("等待漂流瓶内容的回复")
    val waitForNextMessage: String by value("请把想说的话写在纸条上发送出来吧~")

    @ValueDescription("等待漂流瓶内容的回复")
    val timeoutMessage: String by value("是不是没有还没有想到要写什么呢？那待会再找我也行哦")
}
