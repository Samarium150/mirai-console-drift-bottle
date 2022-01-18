package io.github.samarium150.mirai.plugin.driftbottle.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object AdvancedOption : AutoSavePluginConfig("AdvancedOption"){
    @ValueDescription("At显示为纯文本")
    val disableDirectAt by value(false)
}
