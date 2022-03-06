package dem2k;

import static com.mongodb.client.model.Filters.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;

public class Updater1m {

    private final BinanceApiRestClient binance;
    private final MongoCollection<CandlestickWithInterval> mongo;
    private final String ticker;
    private final CandlestickInterval interval;

    public Updater1m(BinanceApiRestClient binance, MongoCollection<CandlestickWithInterval> mongo, String ticker, CandlestickInterval interval) {
        this.binance = binance;
        this.mongo = mongo;
        this.ticker = ticker;
        this.interval = interval;
    }

    public void update(LocalDate atDay) {
        System.out.format("X-DBG='%s'\n", cleanup(atDay));
        updateFromTime(atDay.atTime(0, 0, 0, 0));
        updateFromTime(atDay.atTime(12, 0, 0, 0));
    }

    private void updateFromTime(LocalDateTime fromTime) {
        long unixTime = fromTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<Candlestick> candles =
                binance.getCandlestickBars(ticker, interval, 720, unixTime, null);

        for (Candlestick candlestick : candles) {
            CandlestickWithInterval candlestickWithInterval =
                    new CandlestickWithInterval(ticker, interval, candlestick);
            mongo.insertOne(candlestickWithInterval);
        }
    }

    private DeleteResult cleanup(LocalDate atDay) {
        long fromTime = atDay.atTime(0, 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long toTime = atDay.atTime(23, 59, 59, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return mongo.deleteMany(and(
                eq("interval", "1m"), 
                gte("openTime", fromTime),
                lt("openTime", toTime)));
    }
}
