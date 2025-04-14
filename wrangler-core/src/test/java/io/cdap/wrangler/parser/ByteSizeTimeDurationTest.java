/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.ByteSize;
import io.cdap.wrangler.api.TimeDuration;
import org.junit.Assert;
import org.junit.Test;

public class ByteSizeTimeDurationTest {

  @Test
  public void testByteSizeParsing() {
    Assert.assertEquals(1024L, new ByteSize("1KB").getBytes());
    Assert.assertEquals(1572864L, new ByteSize("1.5MB").getBytes()); // 1.5 * 1024 * 1024
    Assert.assertEquals(1073741824L, new ByteSize("1GB").getBytes()); // 1024^3
    Assert.assertEquals(1099511627776L, new ByteSize("1TB").getBytes()); // 1024^4
  }

  @Test
  public void testTimeDurationParsing() {
    Assert.assertEquals(1L, new TimeDuration("1ms").getMilliseconds()); // 1 ms = 1M ns
    Assert.assertEquals(2500L, new TimeDuration("2.5s").getMilliseconds()); // 2.5 s = 2.5B ns
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidByteUnit() {
    new ByteSize("5XB");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTimeUnit() {
    new TimeDuration("10hr");
  }
}
