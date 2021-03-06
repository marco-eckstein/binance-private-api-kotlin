@file:UseSerializers(
    InstantAsEpochMilliSerializer::class,
    BigDecimalAsPlainStringSerializer::class,
)

package com.marcoeckstein.binance.prvt.api.client.account

import com.marcoeckstein.binance.prvt.api.lib.jvm.BigDecimalAsPlainStringSerializer
import com.marcoeckstein.binance.prvt.api.lib.jvm.InstantAsEpochMilliSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.Instant

@Serializable
data class CrossMarginPaidInterest(
    override val txId: String,
    @SerialName("chargeEpoch")
    override val timestamp: Instant,
    override val asset: String,
    override val principal: BigDecimal,
    override val interest: BigDecimal,
    override val type: String,
    override val interestRate: BigDecimal,
    override val status: String? = null,
) : PaidInterest
