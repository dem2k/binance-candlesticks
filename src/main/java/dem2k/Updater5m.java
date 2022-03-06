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
import java.util.List;
import java.util.stream.Collectors;

import org.bson.conversions.Bson;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;

public class Updater5m implements Updater {

    private static final org.slf4j.Logger LOG =
            org.slf4j.LoggerFactory.getLogger(Updater5m.class);

    private static final Integer LIMIT = 288; // 5*12*24
    private static final CandlestickInterval TIME_FRAME = CandlestickInterval.FIVE_MINUTES;

    private final String ticker;
    private final BinanceApiRestClient binance;
    private final MongoCollection<TfCandle> mongo;

    public Updater5m(BinanceApiRestClient binance, MongoCollection<TfCandle> mongo, String ticker) {
        this.mongo = mongo;
        this.ticker = ticker;
        this.binance = binance;
    }

    @Override
    public boolean update(LocalDate atDay) {
        if (isUpToDate(atDay)) {
            LOG.info("{} @ {}. is up-to-date.", this.ticker, atDay);
            return true;
        }

        long timeFrom = Utils.toUnixTime(atDay);
        long timeTo = Utils.toUnixTime2359(atDay);

        List<Candlestick> candles =
                binance.getCandlestickBars(ticker, TIME_FRAME, LIMIT, timeFrom, timeTo);
        LOG.info("{} @ {}. received {} candles.",
                ticker, atDay, candles.size());

        if (candles.isEmpty()) {
            return false;
        }

        List<TfCandle> documents = candles.stream()
                .map(candle -> TfCandle.from(ticker, TIME_FRAME, candle))
                .collect(Collectors.toList());
        mongo.insertMany(documents);

        return true;
    }

    @Override
    public void checkAndClean(LocalDate atDay) {
        Bson oneDayCriteria = oneDayCriteria(atDay);
        long documents = mongo.countDocuments(oneDayCriteria);
        if (documents > 0 && documents < LIMIT) {
            DeleteResult result = mongo.deleteMany(oneDayCriteria);
            LOG.info("{} @ {}. cleaned {} candles.", ticker, atDay, result.getDeletedCount());
        }
    }

    @Override
    public void export() throws IOException {
        FindIterable<TfCandle> candles =
                mongo.find(eq("frame", TIME_FRAME.getIntervalId()))
                        .sort(ascending("openTime"));
        BufferedWriter writer =
                Files.newBufferedWriter(Paths.get(ticker + TIME_FRAME.getIntervalId() + ".csv"));
        writer.write(TfCandle.CSV_HEADER());
        writer.newLine();

        for (TfCandle candle : candles) {
            writer.write(candle.toCsvValues());
            writer.newLine();
        }
        writer.close();
    }

    private boolean isUpToDate(LocalDate atDay) {
        long documents = mongo.countDocuments(oneDayCriteria(atDay));
        return documents > 0;
    }

    private Bson oneDayCriteria(LocalDate atDay) {
        long timeFrom = Utils.toUnixTime(atDay);
        long timeTo = Utils.toUnixTime2359(atDay);
        return oneDayCriteria(timeFrom, timeTo);
    }

    private Bson oneDayCriteria(long timeFrom, long timeTo) {
        return and(
                eq("frame", "5m"),
                gte("openTime", timeFrom),
                lte("openTime", timeTo));
    }

}
