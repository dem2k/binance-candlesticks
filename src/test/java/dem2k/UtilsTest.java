package dem2k;

import java.time.LocalDate;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

public class UtilsTest  {

    @Test
    public void test0000() {
        var atDay = LocalDate.of(2021, 8, 15);
        long result = Utils.toUnixTime(atDay);
        System.out.format("X-DBG='%s'\n", new Date(result));
    }
    
    @Test
    public void test2359() {
        var atDay = LocalDate.of(2021, 8, 15);
        long result = Utils.toUnixTime2359(atDay);
        Assert.assertEquals(1629064799000L,result);
    }

    @Test
    public void testFromTo() {
        var atDay = LocalDate.of(2021, 8, 15);
        long from = Utils.toUnixTime(atDay);
        long to = Utils.toUnixTime2359(atDay);
        long result = to - from;
        Assert.assertEquals(86399000,result);
    }

}
