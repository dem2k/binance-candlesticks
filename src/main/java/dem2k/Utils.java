package dem2k;

import java.time.LocalDate;
import java.time.ZoneId;

public class Utils {

    public static long toUnixTime(LocalDate local) {
        return local.atTime(0, 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long toUnixTime2359(LocalDate local) {
        return local.atTime(23, 59, 59, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
