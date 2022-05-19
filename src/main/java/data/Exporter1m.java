package data;

import com.binance.api.client.domain.market.CandlestickInterval;
import com.mongodb.client.MongoCollection;

public class Exporter1m extends Exporter {

    public Exporter1m(MongoCollection<CandleCsv> data, String ticker) {
        super(data, ticker);
    }

    @Override
    public CandlestickInterval timeframe() {
        return CandlestickInterval.ONE_MINUTE;
    }

}
