package dem2k;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Sorts.ascending;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.bson.conversions.Bson;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public abstract class Updater {

    protected String ticker;
    protected BinanceApiRestClient binance;
    protected MongoCollection<TfCandle> mongo;

    public abstract boolean update(LocalDate atDay);

    public abstract void checkAndClean(LocalDate atDay);

    public void export() throws IOException {
        FindIterable<TfCandle> candles =
                mongo.find(eq("frame", timeframe().getIntervalId()))
                        .sort(ascending("openTime"));
        BufferedWriter writer =
                Files.newBufferedWriter(Paths.get(ticker + timeframe().getIntervalId() + ".csv"));
        writer.write(TfCandle.CSV_HEADER());
        writer.newLine();

        for (TfCandle candle : candles) {
            writer.write(candle.toCsvValues());
            writer.newLine();
        }
        writer.close();
    }

    public abstract CandlestickInterval timeframe();

    protected Bson oneDayCriteria(LocalDate atDay) {
        long timeFrom = Utils.toUnixTime(atDay);
        long timeTo = Utils.toUnixTime2359(atDay);
        return oneDayCriteria(timeFrom, timeTo);
    }

    protected Bson oneDayCriteria(long timeFrom, long timeTo) {
        return and(
                eq("frame", timeframe().getIntervalId()),
                gte("openTime", timeFrom),
                lte("openTime", timeTo));
    }


}
