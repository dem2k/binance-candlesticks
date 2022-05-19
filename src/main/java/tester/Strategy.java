package tester;

public class Strategy {
    private Grid grid;
    private Wallet wallet;
    private TvChart tvChart;
    private Utils utils;

    private double leoPrice = 0;

    private double orderSize = 100;

    private static final org.slf4j.Logger LOG =
            org.slf4j.LoggerFactory.getLogger(Strategy.class);

    public Strategy(Grid grid, Wallet wallet, TvChart tvChart, Utils utils) {
        this.grid = grid;
        this.wallet = wallet;
        this.tvChart = tvChart;
        this.utils = utils;
    }

    public void process(Candle candle) {
        Order buyOrder = grid.buyOrder(candle.low());
        if (buyOrder != null) {
            buyAt(buyOrder.price());
            grid.remove(buyOrder);
            tvChart.update(buyOrder, candle);
            if (leoPrice > 0) {
                grid.addOrder(new Order(leoPrice, OrderType.SELL));
            }
            leoPrice = buyOrder.price();
        }

        Order sellOrder = grid.sellOrder(candle.high());
        if (sellOrder != null) {
            sellAt(sellOrder.price());
            grid.remove(sellOrder);
            tvChart.update(sellOrder, candle);
            if (leoPrice > 0) {
                grid.addOrder(new Order(leoPrice, OrderType.BUY));
            }
            leoPrice = sellOrder.price();
        }
    }

    private void buyAt(double price) {
        double baseAmnt = utils.truncateLotSize(orderSize / price);
        wallet.putBase(baseAmnt);
        double quoteAmnt = baseAmnt * price;
        wallet.removeQuote(quoteAmnt);
        wallet.payFeeOf(quoteAmnt);
    }

    private void sellAt(double price) {
        double baseAmnt = utils.truncateLotSize(orderSize / price);
        wallet.removeBase(baseAmnt);
        double quoteAmnt = baseAmnt * price;
        wallet.putQuote(quoteAmnt);
        wallet.payFeeOf(quoteAmnt);
    }

    public void finish() {
    }

}
