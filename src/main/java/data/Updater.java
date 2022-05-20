package data;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import java.time.LocalDate;

import org.bson.conversions.Bson;

import com.binance.api.client.domain.market.CandlestickInterval;
import com.mongodb.client.MongoCollection;
import common.CommonUtils;

public abstract class Updater {

    public abstract boolean update(LocalDate atDay);

    public abstract void checkAndClean(LocalDate atDay);

    public abstract MongoCollection<CandleCsv> mongo();

    public abstract String ticker();

    public abstract CandlestickInterval timeframe();

    protected Bson oneDayCriteria(LocalDate atDay) {
        long timeFrom = CommonUtils.toUnixTime0000(atDay);
        long timeTo = CommonUtils.toUnixTime2359(atDay);
        return oneDayCriteria(timeFrom, timeTo);
    }

    protected Bson oneDayCriteria(long timeFrom, long timeTo) {
        return and(
                eq("frame", timeframe().getIntervalId()),
                gte("openTime", timeFrom),
                lte("openTime", timeTo));
    }

    protected boolean isUpToDate(LocalDate atDay) {
        long documents = mongo().countDocuments(oneDayCriteria(atDay));
        return documents > 0;
    }


}
