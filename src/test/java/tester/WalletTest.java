package tester;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import common.CommonUtils;
import picocli.CommandLine;

public class WalletTest {

    @Test
    public void testBase() {
        Wallet wallet = new Wallet("BTC", "USD", null);
        double result = wallet.putBase(100);
        assertEquals(100, result, 0);
        double rem = wallet.removeBase(40);
        assertEquals(60, rem, 0);
    }

    @Test
    public void testQuote() {
        Wallet wallet = new Wallet("BTC", "USD", null);
        double result = wallet.putQuote(100);
        assertEquals(100, result, 0);
        double rem = wallet.removeQuote(40);
        assertEquals(60, rem, 0);
    }

    @Test
    public void testRemoveBase() {
        Wallet wallet = new Wallet("BTC", "USD", null);
        wallet.putQuote(100);
        double result = wallet.payFeeOf(25);
        assertEquals(99.975, result, 0);
    }

    @Test
    public void testSummary() {
        Wallet wallet = new Wallet("BTC", "USD", utils());
        String summary = wallet.summary(10, 20);
        System.out.println(summary);
    }
    
    private CommonUtils utils(){
        SymbolFilter filter = new SymbolFilter();
        filter.setFilterType(FilterType.LOT_SIZE);
        filter.setMinQty("0.00001000");

        SymbolInfo sym = new SymbolInfo();
        List<SymbolFilter> filters = List.of(filter);
        sym.setFilters(filters);
        CommonUtils utils = new CommonUtils(sym);
        return utils;
    }
}
