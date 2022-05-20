package common;

import java.time.LocalDate;
import java.time.ZoneId;

import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import tester.Order;

public class CommonUtils {

    public static final String HOME = "\033[1;1H";
    public static String ERASE_LINE = "\033[K";
    public static String CLEAR_SCRN = "\033[H\033[2J";

    private SymbolInfo symbolInfo;

    public CommonUtils(SymbolInfo symbolInfo) {
        this.symbolInfo = symbolInfo;
    }

    public static long toUnixTime0000(LocalDate local) {
        return local.atTime(0, 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long toUnixTime1159(LocalDate local) {
        return local.atTime(11, 59, 59, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long toUnixTime1200(LocalDate local) {
        return local.atTime(12, 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long toUnixTime2359(LocalDate local) {
        return local.atTime(23, 59, 59, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public double asNumber(String str) {
        return Double.parseDouble(str);
    }

    public double truncateTickSize(double price) {
        int truncteFactor = tickSizeFactor();
        price = Math.floor(price * truncteFactor);
        return (price / truncteFactor);
    }

    public int tickSizeFactor() {
        return symbolInfo.getFilters().stream()
                .filter(filter -> filter.getFilterType() == FilterType.PRICE_FILTER)
                .findFirst()
                .map(SymbolFilter::getTickSize)
                .map(this::computeFactor).orElse(1);
    }

    public int tickSize() {
        return symbolInfo.getFilters().stream()
                .filter(filter -> filter.getFilterType() == FilterType.PRICE_FILTER)
                .findFirst()
                .map(SymbolFilter::getTickSize)
                .map(this::computeTickSize).orElse(4);
    }

    public String tickSizeOrigin() {
        return symbolInfo.getFilters().stream()
                .filter(filter -> filter.getFilterType() == FilterType.PRICE_FILTER)
                .findFirst()
                .map(SymbolFilter::getTickSize).orElse("1");
    }

    private int computeTickSize(String precision) {
        int tSize = 0, factor = 1;
        double tick = asNumber(precision);
        while (tick * factor < 1) {
            factor *= 10;
            tSize++;
        }
        return tSize;
    }

    public double truncateLotSize(double amount) {
        int truncteFactor = lotSizeFactor();
        amount = Math.floor(amount * truncteFactor);
        return (amount / truncteFactor);
    }


    public int lotSizeFactor() {
        return symbolInfo.getFilters().stream()
                .filter(filter -> filter.getFilterType() == FilterType.LOT_SIZE)
                .findFirst()
                .map(SymbolFilter::getMinQty)
                .map(this::computeFactor).orElse(1);
    }

    private int computeFactor(String precision) {
        int factor = 1;
        double tick = asNumber(precision);
        while (tick * factor < 1) {
            factor *= 10;
        }
        return factor;
    }

    public static Double hlc3(String csvLine) {
        if (csvLine == null) {
            return null;
        }
        String[] candle = csvLine.split(";");
        String high = candle[4];
        String low = candle[5];
        String close = candle[6];

        double sum = (Double.parseDouble(high) +
                Double.parseDouble(low) + Double.parseDouble(close)) / 3;
        return Math.ceil(sum * 100) / 100;
    }

    public static String orderPrettyString(Order order) {
        return String.format("--- %4s @ %s               #%n", order.side(), order.price());
    }

}
