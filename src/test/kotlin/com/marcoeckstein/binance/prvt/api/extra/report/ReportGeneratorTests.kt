package com.marcoeckstein.binance.prvt.api.extra.report

import com.binance.api.client.BinanceApiClientFactory
import com.marcoeckstein.binance.prvt.api.Config
import com.marcoeckstein.binance.prvt.api.client.BinancePrivateApiRestClientFactory
import com.marcoeckstein.binance.prvt.api.extra.BinancePrivateApiFacade
import com.marcoeckstein.klib.java.math.equalsComparing
import com.marcoeckstein.klib.java.math.notEqualsComparing
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal

@ExperimentalSerializationApi
class ReportGeneratorTests {

    companion object {

        private val config = Config()

        private val reportGenerator = ReportGenerator(
            BinanceApiClientFactory.newInstance(config.apiKey, config.secret).newRestClient(),
            BinancePrivateApiFacade(
                BinancePrivateApiRestClientFactory.newInstance(config.curlAddressPosix).newRestClient()
            ),
        )

        @JvmStatic
        val assetQuantitiesReports: Map<String, AssetQuantitiesReport> by lazy {
            reportGenerator.getAssetQuantitiesReports()
        }

        @JvmStatic
        val assetHistoryReports: Map<String, AssetHistoryReport> by lazy {
            reportGenerator.getAssetHistoryReport(config.accountStartTime)
        }

        @JvmStatic
        val assets
            get() = assetQuantitiesReports.keys
    }

    @ParameterizedTest
    @MethodSource("getAssets")
    fun `quantities are consistent with history`(asset: String) {
        val quantities = assetQuantitiesReports.getValue(asset)
        val history = assetHistoryReports.getValue(asset)
        val diffGross = quantities.gross - history.gross
        val diffNet = quantities.net - history.net
        if (diffGross notEqualsComparing BigDecimal.ZERO || diffNet notEqualsComparing BigDecimal.ZERO) {
            val message = "Reports for $asset are not consistent.\n\n" +
                quantities.toReportString() + "\n\n" +
                history.toReportString() + "\n\n" +
                "= Summary =\n" + (
                if (!(diffGross equalsComparing BigDecimal.ZERO)) """
                    Quantity gross:  ${quantities.gross.toPlainString()}
                    History gross: ${history.gross.toPlainString()}
                    Diff gross (should be zero): ${diffGross.toPlainString()}
                """.trimIndent() + "\n" else ""
                ) + (
                if (!(diffNet equalsComparing BigDecimal.ZERO)) """
                    Quantity net:  ${quantities.net.toPlainString()}
                    History net: ${history.net.toPlainString()}
                    Diff net (should be zero): ${diffNet.toPlainString()}
                """.trimIndent() else ""
                )
            throw AssertionError(message)
        }
    }
}
