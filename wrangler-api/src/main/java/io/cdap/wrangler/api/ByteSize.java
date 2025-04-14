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

package io.cdap.wrangler.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.cdap.wrangler.api.parser.Token;
import io.cdap.wrangler.api.parser.TokenType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a byte size value parsed from a string (e.g., "10KB", "1.5MB").
 */
public class ByteSize implements Token {
  private final long bytes;
  private final String original;

  public ByteSize(String original) {
    this.original = original;
    this.bytes = parseByteSize(original);
  }

  private long parseByteSize(String value) {
    value = value.trim().toUpperCase();

    Pattern pattern = Pattern.compile("^([0-9]*\\.?[0-9]+)\\s*(B|KB|MB|GB|TB)?$");
    Matcher matcher = pattern.matcher(value);

    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid byte size: " + value);
    }

    double number = Double.parseDouble(matcher.group(1));
    String unit = matcher.group(2);

    long multiplier;
    if (unit == null || unit.equals("B")) {
      multiplier = 1L;
    } else if (unit.equals("KB")) {
      multiplier = 1024L;
    } else if (unit.equals("MB")) {
      multiplier = 1024L * 1024L;
    } else if (unit.equals("GB")) {
      multiplier = 1024L * 1024L * 1024L;
    } else if (unit.equals("TB")) {
      multiplier = 1024L * 1024L * 1024L * 1024L;
    } else {
      throw new IllegalArgumentException("Invalid byte size unit: " + unit);
    }

    return (long) (number * multiplier);
  }

  @Override
  public Object value() {
    return bytes;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(bytes);
  }

  public long getBytes() {
    return bytes;
  }
}

