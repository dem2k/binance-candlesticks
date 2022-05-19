package data;

import java.util.List;
import java.util.stream.Collectors;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class UpdaterFactory {
    private AppConfig config;
    private MongoDatabase mongoDatabase;
    private BinanceApiRestClient binanceRestClient;
    private List<CandlestickInterval> timeframes;

    public UpdaterFactory(BinanceApiRestClient binanceRestClient, MongoDatabase mongoDatabase, AppConfig config) {
        this.binanceRestClient = binanceRestClient;
        this.mongoDatabase = mongoDatabase;
        this.config = config;
    }

    public List<Updater> getUpdaters() {
        return config.timeframes().stream()
                .map(this::createUpdater)
                .collect(Collectors.toList());
    }

    private Updater createUpdater(String timeframe) {
        if (timeframe.equals("1m")) {
            return create1m();
        }
        if (timeframe.equals("5m")) {
            return create5m();
        }
        throw new RuntimeException("timeframe not implemented yet: " + timeframe);
    }

    private Updater create1m() {
        MongoCollection<CandleCsv> mongoCollection =
                mongoDatabase.getCollection(config.ticker() + "1m", CandleCsv.class);
        return new Updater1m(binanceRestClient, mongoCollection, config.ticker());
    }

    private Updater create5m() {
        MongoCollection<CandleCsv> mongoCollection =
                mongoDatabase.getCollection(config.ticker() + "5m", CandleCsv.class);
        return new Updater5m(binanceRestClient, mongoCollection, config.ticker());
    }

//    public List<CandlestickInterval> timeframes() {
//        return Arrays.stream(CandlestickInterval.values()).map(ci -> {
//                    if (timeFrame.contains(ci.getIntervalId())) {
//                        return ci;
//                    } else {
//                        return null;
//                    }
//                }).filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }

}
