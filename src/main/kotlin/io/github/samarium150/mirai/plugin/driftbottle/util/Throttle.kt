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
package io.github.samarium150.mirai.plugin.driftbottle.util

import java.util.concurrent.ConcurrentHashMap

private val throttleSet = ConcurrentHashSet<Long>()

class ConcurrentHashSet<T: Any>(initialCapacity: Int) {
    constructor(): this(0)
    private val map = ConcurrentHashMap<T, Unit>(initialCapacity)

    fun contains(value: T) = null != map[value]

    fun add(value: T) = null == map.put(value, Unit)

    fun remove(value: T) = null != map.remove(value)
}

fun lock(id: Long): Boolean {
    return !throttleSet.add(id)
}

fun unlock(id: Long) {
    throttleSet.remove(id)
}
