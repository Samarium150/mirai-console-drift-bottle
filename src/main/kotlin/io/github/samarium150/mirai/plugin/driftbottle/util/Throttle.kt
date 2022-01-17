package io.github.samarium150.mirai.plugin.driftbottle.util

import kotlinx.coroutines.sync.Mutex

private val throttleMap = mutableMapOf<Long, Mutex>()

private fun getLock(id: Long): Mutex {
    return throttleMap.getOrPut(id) { Mutex() }
}

fun lock(id: Long): Boolean {
    return getLock(id).tryLock()
}

fun unlock(id: Long) {
    throttleMap.remove(id)?.unlock()
}
