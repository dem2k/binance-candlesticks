package data;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.binance.api.client.domain.market.CandlestickInterval;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public abstract class Exporter {

    private String ticker;
    private MongoCollection<CandleCsv> mongo;

    public abstract CandlestickInterval timeframe();

    public String ticker() {
        return ticker;
    }

    public MongoCollection<CandleCsv> mongo() {
        return mongo;
    }

    public Exporter(MongoCollection<CandleCsv> mongo, String ticker) {
        this.mongo = mongo;
        this.ticker = ticker;
    }

    public void export(Character decimalSeparator) throws IOException {
        FindIterable<CandleCsv> candles = mongo().find(eq("frame", timeframe().getIntervalId()))
                .sort(ascending("openTime"));
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(ticker() + timeframe().getIntervalId() + ".csv"));
        writer.write("sep=;");
        writer.newLine();
        writer.write(CandleCsv.CSV_HEADER());
        writer.newLine();

        for (CandleCsv candle : candles) {
            String csv = candle.toCsvValues();
            if (decimalSeparator != null && !decimalSeparator.equals('.')) {
                csv = csv.replace('.', decimalSeparator);
            }
            writer.write(csv);
            writer.newLine();
        }
        writer.close();
    }

}
