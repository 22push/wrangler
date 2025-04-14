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
 * Represents a time duration value parsed from a string (e.g., "1.5s", "10ms").
 */
public class TimeDuration implements Token {
  private final long milliseconds;
  private final String original;

  public TimeDuration(String original) {
    this.original = original;
    this.milliseconds = parseDuration(original);
  }

  private long parseDuration(String value) {
    value = value.trim().toLowerCase();

    Pattern pattern = Pattern.compile("^([0-9]*\\.?[0-9]+)\\s*(ms|s|m|h|d)?$");
    Matcher matcher = pattern.matcher(value);

    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid time duration: " + value);
    }

    double number = Double.parseDouble(matcher.group(1));
    String unit = matcher.group(2);

    long multiplier;
    if (unit == null || unit.equals("ms")) {
      multiplier = 1L;
    } else if (unit.equals("s")) {
      multiplier = 1000L;
    } else if (unit.equals("m")) {
      multiplier = 60L * 1000L;
    } else if (unit.equals("h")) {
      multiplier = 60L * 60L * 1000L;
    } else if (unit.equals("d")) {
      multiplier = 24L * 60L * 60L * 1000L;
    } else {
      throw new IllegalArgumentException("Invalid time duration unit: " + unit);
    }

    return (long) (number * multiplier);
  }

  @Override
  public Object value() {
    return milliseconds;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(milliseconds);
  }

  public long getMilliseconds() {
    return milliseconds;
  }
}


