package tester;

import picocli.CommandLine;

public class AppConfig {

    @CommandLine.Option(names = "-ba", description = "Base Asset.", required = true)
    private String baseAsset;

    @CommandLine.Option(names = "-qa", description = "Quote Asset. Default USDT.")
    private String quotAsset = "USDT";

    @CommandLine.Option(names = "-glp", description = "Grid lowest Price.", required = true)
    private double gridLowestPrice;

    @CommandLine.Option(names = "-ghp", description = "Grid highest Price.", required = true)
    private double gridHighestPrice;

    @CommandLine.Option(names = "-gsf", description = "Grid Step Factor. 0.01 for 1%%.", required = true)
    private double gridStepFactor;

    @CommandLine.Option(names = "-v1", description = "Verbose.")
    private boolean verbose = false;

    @CommandLine.Option(names = "-v2", description = "Very verbose.")
    private boolean veryVerbose = false;

    @CommandLine.Option(names = {"-?", "-h", "--help"}, description = "Display this Help Message.", usageHelp = true)
    private boolean usageHelpRequested = false;

    public boolean verbose() {
        return verbose || veryVerbose;
    }

    public boolean veryVerbose() {
        return veryVerbose;
    }

    public String ticker() {
        return (baseAsset + quotAsset).toUpperCase();
    }

    public String baseAsset() {
        return baseAsset.toUpperCase();
    }

    public String quotAsset() {
        return quotAsset.toUpperCase();
    }

    public boolean isUsageHelpRequested() {
        return usageHelpRequested;
    }

    public double gridStepFactor() {
        return gridStepFactor;
    }

    public double gridHighestPrice() {
        return gridHighestPrice;
    }

    public double gridLowestPrice() {
        return gridLowestPrice;
    }

}

