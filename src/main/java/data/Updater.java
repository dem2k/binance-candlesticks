package data;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.bson.conversions.Bson;

import com.binance.api.client.domain.market.CandlestickInterval;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public abstract class Updater {

    public abstract boolean update(LocalDate atDay);

    public abstract void checkAndClean(LocalDate atDay);

    public abstract MongoCollection<CandleCsv> mongo();

    public abstract String ticker();

//    public void export(Character decimalSeparator) throws IOException {
//        FindIterable<CandleCsv> candles = mongo().find(eq("frame", timeframe().getIntervalId()))
//                .sort(descending("openTime"));
//        BufferedWriter writer = Files.newBufferedWriter(Paths.get(ticker() + timeframe().getIntervalId() + ".csv"));
//        writer.write("sep=;");
//        writer.newLine();
//        writer.write(CandleCsv.CSV_HEADER());
//        writer.newLine();
//
//        for (CandleCsv candle : candles) {
//            String csv = candle.toCsvValues();
//            if (decimalSeparator != null&& !decimalSeparator.equals('.')) {
//                csv = csv.replace('.', decimalSeparator);
//            }
//            writer.write(csv);
//            writer.newLine();
//        }
//        writer.close();
//    }

    public abstract CandlestickInterval timeframe();

    protected Bson oneDayCriteria(LocalDate atDay) {
        long timeFrom = Utils.toUnixTime0000(atDay);
        long timeTo = Utils.toUnixTime2359(atDay);
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
