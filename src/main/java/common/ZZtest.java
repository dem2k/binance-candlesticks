package common;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;

public class ZZtest {
    public static void main(String[] args) {
        BinanceApiClientFactory binanceClientFactory =
                BinanceApiClientFactory.newInstance();
        BinanceApiRestClient binanceRestClient = binanceClientFactory.newRestClient();

        ExchangeInfo exchangeInfo = binanceRestClient.getExchangeInfo();
        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo("BTCUSDT");
        System.out.println(symbolInfo);
//        symbolInfo.getFilters().stream()
//                .filter(filter -> filter.getFilterType() == FilterType.PRICE_FILTER)
//                .findFirst()
//                .map(SymbolFilter::getTickSize)
//                .map(this::computeFactor).orElse(1);

    }
}
