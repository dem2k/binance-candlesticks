package dem2k;

import java.time.LocalDate;
import java.time.ZoneId;

public class Utils {

    public static long toUnixTime0000(LocalDate local) {
        return local.atTime(0, 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long toUnixTime1159(LocalDate local) {
        return local.atTime(11, 59, 59, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long toUnixTime1200(LocalDate local) {
        return local.atTime(12, 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long toUnixTime2359(LocalDate local) {
        return local.atTime(23, 59, 59, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
