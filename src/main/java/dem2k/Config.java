package dem2k;

import picocli.CommandLine;

public class Config {

    @CommandLine.Option(names = "-ti", description = "ticker name, e.g. BTCUSDT.", required = true)
    private String ticker;

    @CommandLine.Option(names = "-cl", description = "cleanup incomplete data.")
    private boolean check;

    @CommandLine.Option(names = "-ex", description = "export data to csv-file.")
    private boolean export;

    @CommandLine.Option(names = "-up", description = "update data for ticker.")
    private boolean update;

    @CommandLine.Option(names = "-ds", description = "decimal separator for csv-values. default '.'")
    private String decimalSeparator;

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

    public String decimalseparator() {
        return decimalSeparator;
    }

}
