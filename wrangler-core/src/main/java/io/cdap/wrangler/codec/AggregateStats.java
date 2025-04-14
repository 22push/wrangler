/*
 *  Copyright © 2017-2019 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */
package io.cdap.wrangler.codec;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.annotations.Categories;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.wrangler.api.Arguments;


import java.util.ArrayList;
import java.util.List;

@Name("aggregate-stats")
@Description("Aggregates byte size and time duration values across rows.")
@Categories(categories = { "aggregate" })
public class AggregateStats implements Directive {
    private String sizeSourceCol;
    private String timeSourceCol;
    private String sizeTargetCol;
    private String timeTargetCol;
    private String sizeUnit = "B"; // Optional: MB, GB
    private String timeUnit = "ns"; // Optional: seconds, minutes

    private static final String STORE_KEY = "aggregate-stats-store";

    @Override
    public UsageDefinition define() {
        UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
        builder.define("sizeSource", TokenType.COLUMN_NAME);
        builder.define("timeSource", TokenType.COLUMN_NAME);
        builder.define("sizeTarget", TokenType.COLUMN_NAME);
        builder.define("timeTarget", TokenType.COLUMN_NAME);
        builder.define("sizeUnit", TokenType.TEXT);
        builder.define("timeUnit", TokenType.TEXT);
        return builder.build();
    }

    @Override
    public void initialize(Arguments args) {
        sizeSourceCol = ((ColumnName) args.value("sizeSource")).value();
        timeSourceCol = ((ColumnName) args.value("timeSource")).value();
        sizeTargetCol = ((ColumnName) args.value("sizeTarget")).value();
        timeTargetCol = ((ColumnName) args.value("timeTarget")).value();

        Object sizeUnitObj = args.value("sizeUnit");
        sizeUnit = sizeUnitObj != null && sizeUnitObj instanceof Text 
                   ? ((Text) sizeUnitObj).value().toUpperCase() 
                   : sizeUnit;
        Object timeUnitObj = args.value("timeUnit");
        timeUnit = timeUnitObj != null && timeUnitObj instanceof Text 
                   ? ((Text) timeUnitObj).value().toLowerCase() 
                   : timeUnit;
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext ctx) throws DirectiveExecutionException {
        List<Row> result = new ArrayList<>();
        for (Row row : rows) {
            try {
                result.addAll(execute(ctx, row));
            } catch (Exception e) {
                throw new DirectiveExecutionException("Error executing row aggregation", e);
            }
        }
        return result;
    }

    public List<Row> execute(ExecutorContext ctx, Row row) throws Exception {
        AggregateStore store = (AggregateStore) SimpleExecutionContext.get(STORE_KEY);
        if (store == null) {
            store = new AggregateStore();
            SimpleExecutionContext.set(STORE_KEY, store);
        }
    
        Object sizeObj = row.getValue(sizeSourceCol);
        if (sizeObj == null) {
            throw new DirectiveExecutionException(
                    String.format("Column '%s' is missing or null in the row.", sizeSourceCol));
        }
        long sizeBytes = parseSizeToBytes(sizeObj.toString());
    
        Object timeObj = row.getValue(timeSourceCol);
        if (timeObj == null) {
            throw new DirectiveExecutionException(
                    String.format("Column '%s' is missing or null in the row.", timeSourceCol));
        }
        long timeNanos = parseTimeToNanos(timeObj.toString());
    
        store.totalBytes += sizeBytes;
        store.totalNanos += timeNanos;
    
        SimpleExecutionContext.set(STORE_KEY, store);
        return new ArrayList<>();
    }
    

    public List<Row> complete(ExecutorContext ctx) {
        AggregateStore store = (AggregateStore) SimpleExecutionContext.get(STORE_KEY);
        if (store == null) {
            return new ArrayList<>();
        }
    
        double finalSize = convertSize(store.totalBytes);
        double finalTime = convertTime(store.totalNanos);
    
        Row row = new Row();
        row.add(sizeTargetCol, finalSize);
        row.add(timeTargetCol, finalTime);
    
        SimpleExecutionContext.clear(); // Cleanup after aggregation
        return List.of(row);
    }
    

    private double convertSize(long bytes) {
        switch (sizeUnit) {
            case "MB":
                return bytes / (1024.0 * 1024);
            case "GB":
                return bytes / (1024.0 * 1024 * 1024);
            default:
                return bytes;
        }
    }

    private double convertTime(long nanos) {
        switch (timeUnit) {
            case "seconds":
                return nanos / 1_000_000_000.0;
            case "minutes":
                return nanos / (60_000_000_000.0);
            default:
                return nanos;
        }
    }

    private long parseSizeToBytes(String size) throws DirectiveExecutionException {
        try {
            // Example implementation: parse size in bytes
            return Long.parseLong(size);
        } catch (NumberFormatException e) {
            throw new DirectiveExecutionException("Invalid size format: " + size, e);
        }
    }

    private long parseTimeToNanos(String time) throws DirectiveExecutionException {
        try {
            // Example implementation: parse time in seconds and convert to nanoseconds
            return (long) (Double.parseDouble(time) * 1_000_000_000);
        } catch (NumberFormatException e) {
            throw new DirectiveExecutionException("Invalid time format: " + time, e);
        }
    }

    private static class AggregateStore {
        long totalBytes = 0;
        long totalNanos = 0;
    }

    @Override
    public void destroy() {
        // Clean up resources if necessary
    }
}