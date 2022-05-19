package tester;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.SymbolInfo;
import picocli.CommandLine;

public class AppMain {

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
        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(config.symbol());

        Utils utils = new Utils(symbolInfo);
        Wallet wallet = new Wallet(config.baseAsset(), config.quotAsset());
        CandleStreamer candles =
                new CandleStreamer(config.csvFile(), utils);
        Grid grid = createGrid(candles.startPrice(), config.gridLowestPrice(), config.gridHighestPrice(), config.gridStepFactor(), utils);
        TvChart tvChart = new TvChart1h(grid, utils);
        Strategy strategy = new Strategy(grid, wallet, tvChart, utils);

        Candle candle = null;
        while (candles.hasNext()) {
            candle = candles.next();
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
        tvChart.save(config.csvFileWithoutExt() + ".html");

        if (!config.verbose()) {
            String summary = wallet.summary(candle.close());
            System.out.println(summary);
        }
    }

    private static void printFooter(Wallet wallet, double price) {
        StringBuffer footer = new StringBuffer();
        footer.append(wallet.summary(price));
        footer.append(Utils.ERASE_LINE).append("\n");
        System.out.println(footer);
    }

    private static void cursorHome() {
        System.out.print(Utils.HOME);
    }

    private static void printHeader(Candle candle) {
        StringBuffer header = new StringBuffer();
        header.append("=== GRID === ").append(candle.time()).append(" ==========");
        header.append(Utils.ERASE_LINE);
        System.out.println(header);
    }

    private static void printGrid(Grid grid, Wallet wallet, Candle candle) {
        double price = candle.hlc3();
        StringBuffer screen = new StringBuffer();
        screen.append(grid.summary(price));
        screen.append("========================================").append(Utils.ERASE_LINE);
        System.out.println(screen);
    }


    private Grid createGrid(double price, double from, double to, double step, Utils utils) {
        Grid.GridBuilder builder = Grid.builder(utils, price);
        while (from <= to) {
            builder.add(from);
            from = utils.truncateTickSize(from + from * step);
        }
        return builder.build();
    }

    private static Grid gridBTC1(Utils utils) {
        Grid.GridBuilder builder = Grid.builder(utils, 4000);
        double price = 30000;
        while (price < 70000) {
            builder.add(price);
            price += price * 0.025;
        }
        return builder.build();
    }

    private static Grid createGrid2(Utils utils) {
        Grid.GridBuilder builder = Grid.builder(utils, 4000);
        double price = 25000;
        while (price <= 60000) {
            builder.add(price);
            price += 1000;
        }
        return builder.build();
    }

    private static Grid gridXLM2(Utils utils) {
        Grid.GridBuilder builder = Grid.builder(utils, 0.1761);
        double price = 0.10;
        while (price <= 0.60) {
            builder.add(price);
            price += 0.05;
        }
        return builder.build();
    }

    private static Grid gridXLM(Utils utils) {
        Grid.GridBuilder builder = Grid.builder(utils, 0.1761);
        double price = 0.15;
        while (price <= 0.30) {
            builder.add(price);
            price += 0.01;
        }
        return builder.build();
    }

}
