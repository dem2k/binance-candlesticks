package tester;

import common.CommonUtils;

public class Wallet {

    private String baseAssetName, quoteAssetName;
    private double baseAssetAmnt, quoteAssetAmnt;
    private double totalMin, totalMax;
    private double fee = 0.001, totalFee = 0;

    public Wallet(String baseAsset, String quoteAsset) {
        this.baseAssetName = baseAsset;
        this.quoteAssetName = quoteAsset;
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

    public String summary(double price) {
        return String.format("""
                         %4s: %10.4f   %4s: %10.2f %s
                          FEE: %10.2f  TOTAL: %10.2f %s
                          MIN: %10.2f    MAX: %10.2f %s
                        """
                , baseName(), baseAmnt(), quoteName(), quoteAmnt(), CommonUtils.ERASE_LINE
                , totalPayedFee(), totalAt(price), CommonUtils.ERASE_LINE
                , totalMin, totalMax, CommonUtils.ERASE_LINE);
    }


    public void updatePnl(CandleGnr candle) {
        var total = totalAt(candle.hlc3());
        if (total < totalMin) {
            totalMin = total;
        }
        if (total > totalMax) {
            totalMax = total;
        }
    }
}
