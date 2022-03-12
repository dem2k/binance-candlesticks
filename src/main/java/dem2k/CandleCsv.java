package dem2k;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

public class CandleCsv {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm");

    private String ticker;
    private String frame;
    private String time;
    // from candlestick
    private long openTime, closeTime;
    private String open;
    private String high;
    private String low;
    private String close;
    private String volume;
    private String quoteAssetVolume;
    private String numberOfTrades;
    private String takerBuyBaseAssetVolume;
    private String takerBuyQuoteAssetVolume;

    public CandleCsv() {
    }

    public static CandleCsv from(String ticker, CandlestickInterval timeFrame, Candlestick candlestick) {
        return new CandleCsv(ticker, timeFrame, candlestick);
    }

    private CandleCsv(String ticker, CandlestickInterval interval, Candlestick candlestick) {
        this.ticker = ticker;
        this.frame = interval.getIntervalId();
        this.time = Instant.ofEpochMilli(candlestick.getOpenTime())
                .atZone(ZoneId.systemDefault()).toLocalDateTime().format(dtf);
        this.openTime = candlestick.getOpenTime();
        this.open = candlestick.getOpen();
        this.high = candlestick.getHigh();
        this.low = candlestick.getLow();
        this.close = candlestick.getClose();
        this.closeTime = candlestick.getCloseTime();
        this.volume = candlestick.getVolume();
        this.quoteAssetVolume = candlestick.getQuoteAssetVolume();
        this.numberOfTrades = candlestick.getNumberOfTrades().toString();
        this.takerBuyBaseAssetVolume = candlestick.getTakerBuyBaseAssetVolume();
        this.takerBuyQuoteAssetVolume = candlestick.getTakerBuyQuoteAssetVolume();
    }

    public static String CSV_HEADER() {
        return "TICKER;FRAME;TIME;OPEN;HIGH;LOW;CLOSE;VOLUME";
    }

    public String toCsvValues() {
        return ticker + ";" + frame + ";" + time + ";" + open + ";" + high + ";" + low + ";" + close + ";" + volume;
    }

    public String getTicker() {
        return ticker;
    }

    public String getFrame() {
        return frame;
    }

    public String getTime() {
        return time;
    }

    public long getOpenTime() {
        return openTime;
    }

    public long getCloseTime() {
        return closeTime;
    }

    public String getOpen() {
        return open;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getClose() {
        return close;
    }

    public String getVolume() {
        return volume;
    }

    public String getQuoteAssetVolume() {
        return quoteAssetVolume;
    }

    public String getNumberOfTrades() {
        return numberOfTrades;
    }

    public String getTakerBuyBaseAssetVolume() {
        return takerBuyBaseAssetVolume;
    }

    public String getTakerBuyQuoteAssetVolume() {
        return takerBuyQuoteAssetVolume;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public void setCloseTime(long closeTime) {
        this.closeTime = closeTime;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setQuoteAssetVolume(String quoteAssetVolume) {
        this.quoteAssetVolume = quoteAssetVolume;
    }

    public void setNumberOfTrades(String numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public void setTakerBuyBaseAssetVolume(String takerBuyBaseAssetVolume) {
        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
    }

    public void setTakerBuyQuoteAssetVolume(String takerBuyQuoteAssetVolume) {
        this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
    }
}
