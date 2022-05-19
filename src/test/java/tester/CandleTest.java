package tester;

import java.util.List;

import org.junit.Test;

import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import junit.framework.TestCase;
import tester.Candle;
import tester.Utils;

public class CandleTest extends TestCase {

    public void testFromCsv() {
        // TICKER;FRAME;TIME;OPEN;HIGH;LOW;CLOSE;VOLUME
        Candle candle = 
                Candle.fromCsv("BTCUSDT;1m;2022-03-19T23-53;0.0;20.0;30.0;50.0;0.0",null);
        
        assertEquals("BTCUSDT", candle.ticker());
        assertEquals(33.33,candle.hlc3());
    }

    @Test
    public void testLocalDate() {
        SymbolFilter filter = new SymbolFilter();
        filter.setFilterType(FilterType.PRICE_FILTER);
        filter.setTickSize("0.00001000");

        SymbolInfo sym = new SymbolInfo();
        List<SymbolFilter> filters = List.of(filter);
        sym.setFilters(filters);
        Utils utils = new Utils(sym);
        
        Candle candle =
                Candle.fromCsv("BTCUSDT;1m;2022-03-19T23-53;0.0;20.0;30.0;50.0;0.0",utils);

        System.out.println(candle.unixTime());
        
    }
}
