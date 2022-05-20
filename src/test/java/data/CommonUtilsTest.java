package data;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.Ignore;
import org.junit.Test;

import common.CommonUtils;

public class CommonUtilsTest {

    @Test
    @Ignore
    public void test2359() {
        var atDay = LocalDate.of(2021, 8, 15)
                .atStartOfDay(ZoneId.of("GMT")).toLocalDate();
        long result = CommonUtils.toUnixTime2359(atDay);
        assertEquals(1629071999000L, result);
    }

    @Test
    public void testFromTo() {
        var atDay = LocalDate.of(2021, 8, 15)
                .atStartOfDay(ZoneId.of("GMT")).toLocalDate();
        long from = CommonUtils.toUnixTime0000(atDay);
        long to = CommonUtils.toUnixTime2359(atDay);
        long result = to - from;
        assertEquals(86399000, result);
    }

}
