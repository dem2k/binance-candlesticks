package tester;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import common.CommonUtils;


public class CandleGnrTest {

    @Test
    public void testFromCsv() {
        SymbolFilter filter = new SymbolFilter();
        filter.setFilterType(FilterType.PRICE_FILTER);
        filter.setTickSize("0.01000000");

        SymbolInfo sym = new SymbolInfo();
        List<SymbolFilter> filters = List.of(filter);
        sym.setFilters(filters);
        CommonUtils utils = new CommonUtils(sym);

        // TICKER;FRAME;TIME;OPEN;HIGH;LOW;CLOSE;VOLUME
        CandleGnr candle =
                CandleGnr.from("BTCUSDT;1m;2022-03-19T23-53;0.0;20.0;30.0;50.0;0.0", utils);

        assertEquals("BTCUSDT", candle.ticker());
        assertEquals(33.33, candle.hlc3(), 0);
    }

    @Test
    public void testLocalDate() {
        SymbolFilter filter = new SymbolFilter();
        filter.setFilterType(FilterType.PRICE_FILTER);
        filter.setTickSize("0.00001000");

        SymbolInfo sym = new SymbolInfo();
        List<SymbolFilter> filters = List.of(filter);
        sym.setFilters(filters);
        CommonUtils utils = new CommonUtils(sym);

        CandleGnr candle =
                CandleGnr.from("BTCUSDT;1m;2022-03-19T23-53;0.0;20.0;30.0;50.0;0.0", utils);

        System.out.println(candle.unixTime());

    }
}
