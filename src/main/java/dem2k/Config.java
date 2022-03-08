package dem2k;

import picocli.CommandLine;

public class Config {

    @CommandLine.Option(names = "-ti", description = "Ticker.", required = true)
    private String ticker;

    @CommandLine.Option(names = "-cl", description = "check and cleanup.")
    private boolean check;

    @CommandLine.Option(names = "-ex", description = "export csv values.")
    private boolean export;

    @CommandLine.Option(names = "-up", description = "update ticker.")
    private boolean update;

    @CommandLine.Option(names = "-ds", description = "decimal separator. default '.'")
    private String decimalSeparator;

    @CommandLine.Option(names = { "-?", "-h", "--help" }, description = "Display this Help Message", usageHelp = true)
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
