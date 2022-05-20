package tester;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import common.Utils;

public class UtilsTest  {

    @Test
    public void testHlc3() {
        // TICKER;FRAME;TIME;OPEN;HIGH;LOW;CLOSE;VOLUME
        double hlc3test = Utils.hlc3("BTCUSDT;1m;2022-03-19T23-53;0.0;11.1;22.2;33.3;0.0");
        assertEquals( 22.2, hlc3test,0.0);

        double hlc3 = Utils.hlc3("BTCUSDT;1m;2022-03-19T23-53;41937.66000000;41962.18000000;41907.18000000;41961.60000000;57.80766000");
        assertEquals( 41943.66, hlc3,0.0);
    }

    @Test
    public void testAssetPrecision(){
        SymbolFilter filter = new SymbolFilter();
        filter.setFilterType(FilterType.PRICE_FILTER);
        filter.setTickSize("0.01000000");

        SymbolInfo sym = new SymbolInfo();
        List<SymbolFilter> filters = List.of(filter);
        sym.setFilters(filters);
        Utils utils = new Utils(sym);
        double result = utils.truncateTickSize(123.45678);
        assertEquals(123.45, result, 0);

    }
    
    @Test
    public void testLotSizePrecision(){
        SymbolFilter filter = new SymbolFilter();
        filter.setFilterType(FilterType.LOT_SIZE);
        filter.setMinQty("0.00001000");

        SymbolInfo sym = new SymbolInfo();
        List<SymbolFilter> filters = List.of(filter);
        sym.setFilters(filters);
        Utils utils = new Utils(sym);
        double result = utils.truncateLotSize(1.23456789);
        assertEquals(1.23456, result, 0);
    }

    @Test
    public void testComputeTickSize() {
        SymbolFilter filter = new SymbolFilter();
        filter.setFilterType(FilterType.PRICE_FILTER);
        filter.setTickSize("0.00001000");

        SymbolInfo sym = new SymbolInfo();
        List<SymbolFilter> filters = List.of(filter);
        sym.setFilters(filters);
        Utils utils = new Utils(sym);
        int result = utils.tickSize();
        assertEquals(5, result);

    }
}
