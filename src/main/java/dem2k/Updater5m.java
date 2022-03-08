package dem2k;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.conversions.Bson;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;

public class Updater5m extends Updater {

    private static final org.slf4j.Logger LOG =
            org.slf4j.LoggerFactory.getLogger(Updater5m.class);

    private static final Integer LIMIT = 288; // 5*12*24

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
                binance.getCandlestickBars(ticker, timeframe(), LIMIT, timeFrom, timeTo);
        LOG.info("{} @ {}. received {} candles.",
                ticker, atDay, candles.size());

        if (candles.isEmpty()) {
            return false;
        }

        List<TfCandle> documents = candles.stream()
                .map(candle -> TfCandle.from(ticker, timeframe(), candle))
                .collect(Collectors.toList());
        mongo.insertMany(documents);

        return true;
    }

    @Override
    public void checkAndClean(LocalDate atDay) {
        Bson oneDayCriteria = oneDayCriteria(atDay);
        long documents = mongo.countDocuments(oneDayCriteria);
        if (documents > 0 && documents != LIMIT) {
            DeleteResult result = mongo.deleteMany(oneDayCriteria);
            LOG.info("{} @ {}. cleaned {} candles.", ticker, atDay, result.getDeletedCount());
        }
    }

    @Override
    public CandlestickInterval timeframe() {
        return CandlestickInterval.FIVE_MINUTES;
    }

    private boolean isUpToDate(LocalDate atDay) {
        long documents = mongo.countDocuments(oneDayCriteria(atDay));
        return documents > 0;
    }


}
