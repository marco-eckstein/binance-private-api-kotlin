package com.marcoeckstein.binance.prvt.api.client.response

import kotlinx.serialization.Serializable

@Serializable
internal class QueryResult2<T : Any>(
    override val code: String,
    override val message: String?,
    override val messageDetail: String?,
    override val success: Boolean,
    val data: DataContainer<T>,
) : QueryResult<T> {

    override val items: List<T> get() = data.data ?: listOf()

    @Serializable
    class DataContainer<T>(
        val data: List<T>?,
        val direction: Int? = null,
        val page: Int,
        val pages: Int,
        val rows: Int,
        val total: Int,
    )
}
