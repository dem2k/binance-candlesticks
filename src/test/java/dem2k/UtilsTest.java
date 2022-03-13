package dem2k;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class UtilsTest {

    @Test
    @Ignore
    public void test2359() {
        var atDay = LocalDate.of(2021, 8, 15)
                .atStartOfDay(ZoneId.of("GMT")).toLocalDate();
        long result = Utils.toUnixTime2359(atDay);
        Assert.assertEquals(1629071999000L, result);
    }

    @Test
    public void testFromTo() {
        var atDay = LocalDate.of(2021, 8, 15)
                .atStartOfDay(ZoneId.of("GMT")).toLocalDate();
        long from = Utils.toUnixTime0000(atDay);
        long to = Utils.toUnixTime2359(atDay);
        long result = to - from;
        Assert.assertEquals(86399000, result);
    }

}
