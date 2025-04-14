package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.ByteSize;
import org.junit.Assert;
import org.junit.Test;

public class ByteSizeTest {

    @Test
    public void testByteSizeParsing() {
        Assert.assertEquals(10240, new ByteSize("10kb").getBytes());
        Assert.assertEquals(1572864, new ByteSize("1.5MB").getBytes());
        Assert.assertEquals(1073741824, new ByteSize("1GB").getBytes());
        Assert.assertEquals(42, new ByteSize("42B").getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidByteSize() {
        new ByteSize("abcxyz");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidByteSizeWithSpaces() {
        new ByteSize("1 KB");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidByteSizeWithNegativeValue() {
        new ByteSize("-1KB");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidByteSizeWithZeroValue() {
        new ByteSize("0KB");
    }
}
// Compare this snippet from wrangler-core/src/test/java/io/cdap/wrangler/parser/ByteSizeTimeDurationTest.java: