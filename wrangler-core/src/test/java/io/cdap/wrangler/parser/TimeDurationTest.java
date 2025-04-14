package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.ByteSize;
import io.cdap.wrangler.api.TimeDuration;
import org.junit.Assert;
import org.junit.Test;


public class TimeDurationTest {

    @Test
    public void testTimeParsing() {
        Assert.assertEquals(5_000_000, new TimeDuration("5ms").getMilliseconds());
        Assert.assertEquals(2_100_000_000L, new TimeDuration("2.1s").getMilliseconds());
        Assert.assertEquals(60_000_000_000L, new TimeDuration("1m").getMilliseconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTime() {
        new TimeDuration("hellotime");
    }
}
