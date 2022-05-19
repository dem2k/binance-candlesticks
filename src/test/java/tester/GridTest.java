package tester;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import tester.Grid;
import tester.Order;
import tester.Utils;

public class GridTest {

    @Test
    public void buy_order_should_exist() {
        Grid grid = Grid.builder(null, 35)
                .add(22).add(33).add(44).add(55)
                .build();
        System.out.println(grid);
        double order = grid.buyOrder(25).price();
        System.out.println(order);
        Assert.assertEquals(33, order, 0);
    }
    
    @Test
    public void sell_order_should_exist() {
        Grid grid = Grid.builder(null, 35)
                .add(22).add(33).add(44).add(55)
                .build();
        System.out.println(grid);
        double order = grid.sellOrder(48).price();
        System.out.println(order);
        Assert.assertEquals(44, order, 0);
    }

    @Test
    public void buy_order_should_not_exist() {
        Grid grid = Grid.builder(null, 10)
                .add(20).add(30).build();
        
        Order order = grid.buyOrder(10);
        Assert.assertNull(order);
    }
    
    @Test public void testSummar(){
        SymbolFilter filter = new SymbolFilter();
        filter.setFilterType(FilterType.PRICE_FILTER);
        filter.setTickSize("0.00010000");

        SymbolInfo sym = new SymbolInfo();
        List<SymbolFilter> filters = List.of(filter);
        sym.setFilters(filters);
        Utils utils = new Utils(sym);

        Assert.assertEquals(4, utils.tickSize());

        Grid grid = Grid.builder(utils, 0.21).add(0.20).add(0.24).build();
        String result = grid.summary(0.2211);
        System.out.println(result);

    }

}
