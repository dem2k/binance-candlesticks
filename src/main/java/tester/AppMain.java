package tester;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.SymbolInfo;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import data.CandleCsv;
import common.Utils;
import picocli.CommandLine;

public class AppMain {

    public static final String MONGO_DATABASE = "binance";

    private AppConfig config;

    // ANSI ESC-SEQ:  https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797
    public static void main(String[] args) throws Exception {

        AppConfig config = CommandLine.populateCommand(new AppConfig(), args);
        if (config.isUsageHelpRequested()) {
            CommandLine.usage(config, System.out);
            return;
        }

        AppMain main = new AppMain(config);
        main.start();
        System.exit(0);
    }

    public AppMain(AppConfig config) {
        this.config = config;
    }

    private void start() throws Exception {

        BinanceApiClientFactory binanceClientFactory =
                BinanceApiClientFactory.newInstance();
        BinanceApiRestClient binanceRestClient = binanceClientFactory.newRestClient();

        ExchangeInfo exchangeInfo = binanceRestClient.getExchangeInfo();
        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(config.ticker());

        Utils utils = new Utils(symbolInfo);
        Wallet wallet = new Wallet(config.baseAsset(), config.quotAsset());

        List<CandleGnr> candles = getCandlesFromMongo(utils);

        Grid grid = createGrid(getFirstOpen(candles), config.gridLowestPrice(), config.gridHighestPrice(), config.gridStepFactor(), utils);
        TvChart tvChart = new TvChart1h(grid, utils);
        Strategy strategy = new Strategy(grid, wallet, tvChart, utils);

        for (CandleGnr candle : candles) {
            tvChart.update(candle);
            strategy.process(candle);
            wallet.updatePnl(candle);
            if (config.verbose()) {
                cursorHome();
                printHeader(candle);
                if (config.veryVerbose()) {
                    printGrid(grid, wallet, candle);
                }
                printFooter(wallet, candle.close());
            }
        }

        strategy.finish();
        tvChart.save(config.ticker() + "-grid-test.html");

        if (!config.verbose()) {
            String summary = wallet.summary(getLastClose(candles));
            System.out.println(summary);
        }
    }

    private double getFirstOpen(List<CandleGnr> candles) {
        CandleGnr candle =
                candles.stream().min(Comparator.comparing(CandleGnr::time)).orElseThrow();
        return candle.open();
    }

    private double getLastClose(List<CandleGnr> candles) {
        CandleGnr candle =
                candles.stream().max(Comparator.comparing(CandleGnr::time)).orElseThrow();
        return candle.close();
    }

    private List<CandleGnr> getCandlesFromMongo(Utils utils) {
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry).build();
        MongoDatabase mongoDatabase =
                MongoClients.create(mongoClientSettings).getDatabase(MONGO_DATABASE);

        MongoCollection<CandleCsv> data =
                mongoDatabase.getCollection(config.ticker() + "1m", CandleCsv.class);

        FindIterable<CandleCsv> candles = data.find(eq("frame", "1m"))
                .sort(ascending("openTime"));

        List<CandleGnr> result = new ArrayList<>();
        for (CandleCsv csvCndl : candles) {
            // TICKER;FRAME;TIME;OPEN;HIGH;LOW;CLOSE;VOLUME
            String csvLine = csvCndl.getTicker() + ";" + csvCndl.getFrame() + ";" + csvCndl.getTime()
                    + ";" + csvCndl.getOpen() + ";" + csvCndl.getHigh() + ";" + csvCndl.getLow()
                    + ";" + csvCndl.getClose() + ";" + csvCndl.getVolume();
            result.add(CandleGnr.from(csvLine, utils));
        }

        return result;
    }

    private Grid createGrid(double price, double from, double to, double step, Utils utils) {
        Grid.GridBuilder builder = Grid.builder(utils, price);
        while (from <= to) {
            builder.add(from);
            from = utils.truncateTickSize(from + from * step);
        }
        return builder.build();
    }

    private static void cursorHome() {
        System.out.print(Utils.HOME);
    }

    private static void printFooter(Wallet wallet, double price) {
        StringBuilder footer = new StringBuilder();
        footer.append(wallet.summary(price));
        footer.append(Utils.ERASE_LINE).append("\n");
        System.out.println(footer);
    }

    private static void printHeader(CandleGnr candle) {
        StringBuilder header = new StringBuilder();
        header.append("=== GRID === ").append(candle.time()).append(" ==========");
        header.append(Utils.ERASE_LINE);
        System.out.println(header);
    }

    private static void printGrid(Grid grid, Wallet wallet, CandleGnr candle) {
        double price = candle.hlc3();
        StringBuilder screen = new StringBuilder();
        screen.append(grid.summary(price));
        screen.append("========================================").append(Utils.ERASE_LINE);
        System.out.println(screen);
    }

}
