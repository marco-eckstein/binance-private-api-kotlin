@file:UseSerializers(InstantAsEpochMilliSerializer::class)

package com.marcoeckstein.binance.prvt.api.client.account.request

import com.binance.api.client.domain.OrderSide
import com.marcoeckstein.binance.prvt.api.client.account.AccountType
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

@Serializable
data class OrderHistoryQuery(
    @SerialName("page")
    override val pageIndex: Int = 1,
    /**
     * Max: 2000
     */
    @SerialName("rows")
    override val pageSize: Int = 2000,
    val accountType: AccountType,
    override val startTime: Instant? = null,
    /**
     * End time, exclusive
     */
    override val endTime: Instant? = null,
    val baseAsset: String? = null,
    val quoteAsset: String? = null,
    /**
     * An alternative to using baseAsset and/or quoteAsset.
     */
    val symbol: String? = null,
    val direction: OrderSide? = null,
    val hideCancel: Boolean,
) : PagingQuery<OrderHistoryQuery>, PeriodQuery<OrderHistoryQuery> {

    init {
        requireValidPeriod()
    }

    override fun forNextPage() = copy(pageIndex = pageIndex + 1)

    override val isEndTimeInclusive get() = false

    override fun copyWith(startTime: Instant?, endTime: Instant?) =
        copy(startTime = startTime, endTime = endTime)
}
