package data;

import static com.mongodb.client.model.Indexes.ascending;

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

    protected String ticker;
    protected BinanceApiRestClient binance;
    protected MongoCollection<CandleCsv> mongo;

    private static final org.slf4j.Logger LOG =
            org.slf4j.LoggerFactory.getLogger(Updater5m.class);

    private static final Integer LIMIT = 288; // 5*12*24

    public Updater5m(BinanceApiRestClient binance, MongoCollection<CandleCsv> mongo, String ticker) {
        this.mongo = mongo;
        this.ticker = ticker;
        this.binance = binance;
        this.mongo.createIndex(ascending("openTime"));
    }

    @Override
    public boolean update(LocalDate atDay) {
        if (isUpToDate(atDay)) {
            LOG.info("{} @ {}. is up-to-date.", this.ticker, atDay);
            return true;
        }

        long timeFrom = Utils.toUnixTime0000(atDay);
        long timeTo = Utils.toUnixTime2359(atDay);

        List<Candlestick> candles =
                binance.getCandlestickBars(ticker, timeframe(), LIMIT, timeFrom, timeTo);
        LOG.info("{} @ {}. received {} candles.",
                ticker, atDay, candles.size());

        if (candles.isEmpty()) {
            return false;
        }

        List<CandleCsv> documents = candles.stream()
                .map(candle -> CandleCsv.from(ticker, timeframe(), candle))
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
    public MongoCollection<CandleCsv> mongo() {
        return mongo;
    }

    @Override
    public String ticker() {
        return ticker;
    }

    @Override
    public CandlestickInterval timeframe() {
        return CandlestickInterval.FIVE_MINUTES;
    }


}
