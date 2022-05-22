package tester;

import common.CommonUtils;

public class Wallet {

    private String baseAssetName;
    private String quoteAssetName;
    private CommonUtils utils;
    private double baseAssetAmnt, quoteAssetAmnt;
    private double totalMin, totalMax;
    private double fee = 0.001, totalFee = 0;

    public Wallet(String baseAsset, String quoteAsset, CommonUtils utils) {
        this.baseAssetName = baseAsset;
        this.quoteAssetName = quoteAsset;
        this.utils = utils;
    }

    public double putBase(double amount) {
        baseAssetAmnt += amount;
        return baseAssetAmnt;
    }

    public double putQuote(double amount) {
        quoteAssetAmnt += amount;
        return quoteAssetAmnt;
    }

    public double removeBase(double amount) {
        baseAssetAmnt -= amount;
        return baseAssetAmnt;
    }

    public double removeQuote(double amount) {
        quoteAssetAmnt -= amount;
        return quoteAssetAmnt;
    }

    public double baseAmnt() {
        return baseAssetAmnt;
    }

    public double quoteAmnt() {
        return quoteAssetAmnt;
    }

    public double totalAt(double price) {
        return baseAssetAmnt * price + quoteAssetAmnt;
    }

    public double payFeeOf(double amount) {
        double feeToPay = amount * fee;
        totalFee += feeToPay;
        quoteAssetAmnt -= feeToPay;
        return quoteAssetAmnt;
    }

    public String baseName() {
        return baseAssetName;
    }

    public String quoteName() {
        return quoteAssetName;
    }

    public double totalPayedFee() {
        return totalFee;
    }

    public String summary(double openPrice, double closePrice) {
        var precision = utils.tickSize();
        var t1 = "%4s: %10." + precision + "f   %4s: %10.2f %s\n";
        var t2 = " FEE: %10.2f  TOTAL: %10.2f %s\n";
        var t3 = " MIN: %10.2f    MAX: %10.2f %s\n";
        var t4 = "OPEN: %10." + precision + "f  CLOSE: %10." + precision + "f %s\n";

        return String.format(t1 + t2 + t3 + t4
                , baseName(), baseAmnt(), quoteName(), quoteAmnt(), CommonUtils.ERASE_LINE
                , totalPayedFee(), totalAt(closePrice), CommonUtils.ERASE_LINE
                , totalMin, totalMax, CommonUtils.ERASE_LINE
                , openPrice, closePrice, CommonUtils.ERASE_LINE
        );
    }

    public void updatePnl(CandleGnr candle) {
        var total = totalAt(candle.close());
        if (total < totalMin) {
            totalMin = total;
        }
        if (total > totalMax) {
            totalMax = total;
        }
    }
}
