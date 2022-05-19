package tester;

import picocli.CommandLine;

public class AppConfig {

    @CommandLine.Option(names = "-ba", description = "Base Asset.", required = true)
    private String baseAsset;

    @CommandLine.Option(names = "-qa", description = "Quote Asset. Default USDT.")
    private String quotAsset = "USDT";

    @CommandLine.Option(names = "-csv", description = "Data File in CSV-Format.", required = true)
    private String csvFile;

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

    public String symbol() {
        return baseAsset + quotAsset;
    }

    public String baseAsset() {
        return baseAsset;
    }

    public String quotAsset() {
        return quotAsset;
    }

    public boolean isUsageHelpRequested() {
        return usageHelpRequested;
    }

    public String csvFile() {
        return csvFile;
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

    public String csvFileWithoutExt() {
        int dotIndex = csvFile.lastIndexOf('.');
        return (dotIndex == -1) ? csvFile : csvFile.substring(0, dotIndex);
    }

}

