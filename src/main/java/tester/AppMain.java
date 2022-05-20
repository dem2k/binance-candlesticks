package tester;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.binance.api.client.BinanceApiClientFactory;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import common.CommonUtils;
import common.MongoUtils;
import data.CandleCsv;
import picocli.CommandLine;

public class AppMain {

    private AppConfig config;

    // ANSI ESC-SEQ:  https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797
    public static void main(String[] args) throws Exception {

        var config = CommandLine.populateCommand(new AppConfig(), args);
        if (config.isUsageHelpRequested()) {
            CommandLine.usage(config, System.out);
            return;
        }

        var main = new AppMain(config);
        main.start();
        System.exit(0);
    }

    public AppMain(AppConfig config) {
        this.config = config;
    }

    private void start() throws Exception {

        var binance =
                BinanceApiClientFactory.newInstance().newRestClient();

        var symbolInfo =
                binance.getExchangeInfo().getSymbolInfo(config.ticker());

        var utils = new CommonUtils(symbolInfo);
        var wallet = new Wallet(config.baseAsset(), config.quotAsset());

        List<CandleGnr> candles = getCandlesFromMongo(utils);

        var grid = createGrid(getFirstOpen(candles), config.gridLowestPrice(), config.gridHighestPrice(), config.gridStepFactor(), utils);
        var tvChart = new TvChart1h(grid, utils);
        var strategy = new Strategy(grid, wallet, tvChart, utils);

        for (var candle : candles) {
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
        tvChart.save(config.ticker() + "1h-grid-test.html");

        if (!config.verbose()) {
            String summary = wallet.summary(getLastClose(candles));
            System.out.println(summary);
        }
    }

    private double getFirstOpen(List<CandleGnr> candles) {
        var candle =
                candles.stream().min(Comparator.comparing(CandleGnr::time)).orElseThrow();
        return candle.open();
    }

    private double getLastClose(List<CandleGnr> candles) {
        var candle =
                candles.stream().max(Comparator.comparing(CandleGnr::time)).orElseThrow();
        return candle.close();
    }

    private List<CandleGnr> getCandlesFromMongo(CommonUtils utils) {
        MongoCollection<CandleCsv> data =
                MongoUtils.mongoDatabase().getCollection(config.ticker() + "1m", CandleCsv.class);

        FindIterable<CandleCsv> candles = data.find(eq("frame", "1m"))
                .sort(ascending("openTime"));

        long counter = 0;
        System.out.print("Reading Database...");
        List<CandleGnr> result = new ArrayList<>();
        for (var candle : candles) {
            // TICKER;FRAME;TIME;OPEN;HIGH;LOW;CLOSE;VOLUME
            var csvLine = candle.getTicker() + ";" + candle.getFrame() + ";" + candle.getTime()
                    + ";" + candle.getOpen() + ";" + candle.getHigh() + ";" + candle.getLow()
                    + ";" + candle.getClose() + ";" + candle.getVolume();
            result.add(CandleGnr.from(csvLine, utils));
            if (counter++ % 10000 == 0) {
                System.out.print(".");
            }
        }
        System.out.println("done.");
        return result;
    }


    private Grid createGrid(double price, double from, double to, double step, CommonUtils utils) {
        Grid.GridBuilder builder = Grid.builder(utils, price);
        while (from <= to) {
            builder.add(from);
            from = utils.truncateTickSize(from + from * step);
        }
        return builder.build();
    }

    private static void cursorHome() {
        System.out.print(CommonUtils.HOME);
    }

    private static void printFooter(Wallet wallet, double price) {
        StringBuilder footer = new StringBuilder();
        footer.append(wallet.summary(price));
        footer.append(CommonUtils.ERASE_LINE).append("\n");
        System.out.println(footer);
    }

    private static void printHeader(CandleGnr candle) {
        StringBuilder header = new StringBuilder();
        header.append("=== GRID === ").append(candle.time()).append(" ==========");
        header.append(CommonUtils.ERASE_LINE);
        System.out.println(header);
    }

    private static void printGrid(Grid grid, Wallet wallet, CandleGnr candle) {
        double price = candle.hlc3();
        StringBuilder screen = new StringBuilder();
        screen.append(grid.summary(price));
        screen.append("========================================").append(CommonUtils.ERASE_LINE);
        System.out.println(screen);
    }

}
