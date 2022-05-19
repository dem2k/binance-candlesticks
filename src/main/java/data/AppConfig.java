package data;

import java.util.List;

import picocli.CommandLine;

public class AppConfig {

    @CommandLine.Option(names = "-ti", description = "ticker name, e.g. BTCUSDT.", required = true)
    private String ticker;

    @CommandLine.Option(names = "-cl", description = "cleanup incomplete data.")
    private boolean check;

    @CommandLine.Option(names = "-ex", description = "export data to csv-file.")
    private boolean export;

    @CommandLine.Option(names = "-up", description = "update data for ticker.")
    private boolean update;

    @CommandLine.Option(names = "-ds", description = "decimal separator for csv-values. default '.'")
    private Character decimalSeparator = '.';

    @CommandLine.Option(names = "-tf", description = "timeframe. now support only 1m and 5m. default '1m'")
    private List<String> timeframes = List.of("1m");

    @CommandLine.Option(names = {"-?", "-h", "--help"}, description = "display this help message.", usageHelp = true)
    private boolean usageHelpRequested = false;

    public String ticker() {
        return ticker.toUpperCase();
    }

    public boolean isUsageHelpRequested() {
        return usageHelpRequested;
    }

    public boolean check() {
        return check;
    }

    public boolean export() {
        return export;
    }

    public boolean update() {
        return update;
    }

    public Character decimalseparator() {
        return decimalSeparator;
    }

    public List<String> timeframes() {
        return timeframes;
    }
}
