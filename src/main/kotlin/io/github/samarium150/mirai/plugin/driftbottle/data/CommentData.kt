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
package io.github.samarium150.mirai.plugin.driftbottle.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @author LaoLittle
 */
@Serializable
data class CommentData(
    val senderId: Long,
    val senderName: String,
    val content: String,
) {
    @Suppress("unused")
    val timestamp: Long = Date().time

    companion object : AutoSavePluginData("Comment") {
        val comments by value(mutableMapOf<Int, MutableList<CommentData>>())
    }
}

private val mutex = Mutex()

var isLocked = false
    private set

@OptIn(ExperimentalContracts::class)
suspend fun <T> useLock(owner: Any? = null, action: () -> T): T {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }

    mutex.lock(owner)
    isLocked = true
    try {
        return action()
    } finally {
        isLocked = false
        mutex.unlock(owner)
    }
}

val comments by CommentData.Companion::comments
