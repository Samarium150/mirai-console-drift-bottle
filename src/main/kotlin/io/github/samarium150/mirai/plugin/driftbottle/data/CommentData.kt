package io.github.samarium150.mirai.plugin.driftbottle.data

import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

@Serializable
data class CommentData(
    val sender: String,
    val content: String,
){
    @Suppress("unused")
    val time: Long = getTimeMillis()

    companion object : AutoSavePluginData("Comment") {
        val comments by value(mutableMapOf<Int, MutableList<CommentData>>())
    }
}
