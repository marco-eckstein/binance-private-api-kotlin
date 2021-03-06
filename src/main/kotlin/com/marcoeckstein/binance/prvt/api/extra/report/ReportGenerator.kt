package com.marcoeckstein.binance.prvt.api.extra.report

import com.binance.api.client.BinanceApiRestClient
import com.marcoeckstein.binance.prvt.api.extra.BinancePrivateApiFacade
import com.marcoeckstein.binance.prvt.api.extra.extensions.assets
import com.marcoeckstein.binance.prvt.api.extra.extensions.getAllAssetsNames
import java.math.BigDecimal
import java.time.Instant

class ReportGenerator(
    private val publicApi: BinanceApiRestClient,
    private val privateApi: BinancePrivateApiFacade,
) {

    fun getAssetQuantitiesReports(): Map<String, AssetQuantitiesReport> {
        val spotBalances = privateApi.getSpotAccountBalances()
        val isolatedMarginDetails = privateApi.getIsolatedMarginAccountDetails()
        val flexibleSavingsPositions = privateApi.getFlexibleSavingsPositions()
        val lockedStakingPositions = privateApi.getLockedStakingPositions()

        return publicApi.getAllAssetsNames().map { asset ->
            asset to AssetQuantitiesReport(
                asset = asset,
                spotFree = spotBalances.singleOrNull { it.asset == asset }?.free ?: BigDecimal.ZERO,
                spotLocked = spotBalances.singleOrNull { it.asset == asset }?.locked ?: BigDecimal.ZERO,
                isolatedMarginFree =
                isolatedMarginDetails.flatMap { it.assets }.filter { it.assetName == asset }.sumOf { it.free },
                isolatedMarginLocked =
                isolatedMarginDetails.flatMap { it.assets }.filter { it.assetName == asset }
                    .sumOf { it.locked },
                isolatedMarginBorrowed =
                isolatedMarginDetails.flatMap { it.assets }.filter { it.assetName == asset }
                    .sumOf { it.borrowed },
                isolatedMarginInterest =
                isolatedMarginDetails.flatMap { it.assets }.filter { it.assetName == asset }
                    .sumOf { it.interest },
                flexibleSavings = flexibleSavingsPositions.filter { it.asset == asset }
                    .sumOf { it.totalAmount },
                lockedStaking = lockedStakingPositions.filter { it.asset == asset }.sumOf { it.amount },
            )
        }.toMap()
    }

    fun getAssetHistoryReport(start: Instant): Map<String, AssetHistoryReport> {
        val payments = privateApi.getPaymentHistory()
        val trades = privateApi.getTradeHistory(start)
        val distributions = privateApi.getDistributionHistory(start)
        val flexibleSavingsInterests = privateApi.getFlexibleSavingsInterestHistory(start)
        val lockedStakingInterests = privateApi.getLockedStakingInterestHistory(start)
        val isolatedMarginBorrowings = privateApi.getIsolatedMarginBorrowingHistory(start)
        val isolatedMarginRepayments = privateApi.getIsolatedMarginRepaymentHistory(start)
        val isolatedMarginInterests = privateApi.getIsolatedMarginInterestHistory(start)
        val isolatedMarginRebates = privateApi.getIsolatedMarginRebateHistory(start)
        return publicApi.getAllAssetsNames().map { asset ->
            asset to AssetHistoryReport(
                asset = asset,
                payments = payments.filter { it.cryptoCurrency == asset },
                trades = trades.filter { it.baseAsset == asset || it.quoteAsset == asset },
                distributions = distributions.filter { it.asset == asset },
                flexibleSavingsInterests = flexibleSavingsInterests.filter { it.asset == asset },
                lockedStakingInterests = lockedStakingInterests.filter { it.asset == asset },
                isolatedMarginBorrowings = isolatedMarginBorrowings.filter { it.asset == asset },
                isolatedMarginRepayments = isolatedMarginRepayments.filter { it.asset == asset },
                isolatedMarginInterests = isolatedMarginInterests.filter { it.asset == asset },
                isolatedMarginRebates = isolatedMarginRebates.filter { it.asset == asset },
            )
        }.toMap()
    }
}
