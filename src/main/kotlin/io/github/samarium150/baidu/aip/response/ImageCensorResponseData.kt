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
package io.github.samarium150.baidu.aip.response

import kotlinx.serialization.Serializable

@Serializable
data class ImageCensorResponseData(
    override val error_code: Int? = null,
    override val error_msg: String? = null,
    override val type: Int? = null,
    override val subType: Int? = null,
    override val msg: String? = null,
    val probability: Double? = null,
    val datasetName: Double? = null,
    val stars: List<Star>? = null,
    val conclusion: String? = null,
    val conclusionType: Int? = null
) : ResponseData
