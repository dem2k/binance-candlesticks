package dem2k;

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

public class Updater1m extends Updater {
    
    private static final org.slf4j.Logger LOG = 
    	org.slf4j.LoggerFactory.getLogger(Updater1m.class);

    private final String ticker;
    private final BinanceApiRestClient binance;
    private final MongoCollection<CandleCsv> mongo;

    private static final Integer LIMIT12h = 720;
    private static final Integer LIMIT24h = 1440; // 5*12*24

    public Updater1m(BinanceApiRestClient binance, MongoCollection<CandleCsv> mongo, String ticker) {
        this.binance = binance;
        this.mongo = mongo;
        this.ticker = ticker;
        this.mongo.createIndex(ascending("openTime"));
    }

    @Override
    public boolean update(LocalDate atDay) {
        if (isUpToDate(atDay)) {
            LOG.info("{} @ {}. is up-to-date.", this.ticker, atDay);
            return true;
        }

        long timeFr00 = Utils.toUnixTime0000(atDay);
        long timeTo12 = Utils.toUnixTime1159(atDay);

        boolean extracted1 = extracted(atDay, timeFr00, timeTo12,"AM");

        long timeFr12 = Utils.toUnixTime1200(atDay);
        long timeTo24 = Utils.toUnixTime2359(atDay);

        boolean extracted2 = extracted(atDay, timeFr12, timeTo24, "PM");

        return extracted1 & extracted2;
    }

    private boolean extracted(LocalDate atDay, long timeFr, long timeTo, String logsufix) {
        List<Candlestick> candles =
                binance.getCandlestickBars(ticker, timeframe(), LIMIT12h, timeFr, timeTo);
        LOG.info("{} @ {} {}. received {} candles.",
                ticker, atDay, logsufix, candles.size());

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
        if (documents > 0 && documents != LIMIT24h) {
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
        return CandlestickInterval.ONE_MINUTE;
    }

}
