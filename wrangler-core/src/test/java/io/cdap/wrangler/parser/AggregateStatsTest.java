// package io.cdap.wrangler.parser;

// import io.cdap.wrangler.api.Row;
// import io.cdap.wrangler.api.parser.Token;
// import io.cdap.wrangler.api.parser.ColumnName;
// import io.cdap.wrangler.api.parser.Text;
// import io.cdap.wrangler.api.Arguments;
// import io.cdap.wrangler.api.DirectiveExecutionException;
// import io.cdap.wrangler.api.ExecutorContext;
// import io.cdap.wrangler.codec.AggregateStats;
// import io.cdap.wrangler.codec.SimpleExecutionContext;
// import org.junit.Test;

// import java.util.*;

// import static org.junit.Assert.assertEquals;

// public class AggregateStatsTest {

//     // Inline ColumnName and Text token implementations
//     private static class DummyColumnName extends ColumnName {
//         private final String name;
//         public DummyColumnName(String name) {
//             super(name);
//             this.name = name;
//         }
//         @Override
//         public String value() {
//             return name;
//         }
//     }

//     private static class DummyText extends Text {
//         private final String text;
//         public DummyText(String text) {
//             super(text);
//             this.text = text;
//         }
//         @Override
//         public String value() {
//             return text;
//         }
//     }

//     private static class DummyArguments implements Arguments {
//         private final Map<String, Token> args;

//         public DummyArguments(Map<String, Token> args) {
//             this.args = args;
//         }

//         @Override
//         public <T extends Token> T value(String name) {
//             return (T) args.get(name);
//         }

//         @Override
//         public boolean contains(String name) {
//             return args.containsKey(name);
//         }

//         @Override public com.google.gson.JsonElement toJson() {
//             com.google.gson.JsonObject jsonObject = new com.google.gson.JsonObject();
//             args.forEach((key, value) -> jsonObject.addProperty(key, String.valueOf(value.value())));
//             return jsonObject;
//         }
//         @Override public String column() { return "dummy_column"; }
//         @Override public int line() { return 0; }
//         @Override public int size() { return args.size(); }
//         @Override public String type(String name) { return "dummy_type"; }
//         @Override public String source() { return "dummy_source"; }
//     }

//     @Test
//     public void testAggregateExecution() throws DirectiveExecutionException {
//         AggregateStats directive = new AggregateStats();

//         Arguments args = new DummyArguments(Map.of(
//             "sizeSource", new DummyColumnName("size_col"),
//             "timeSource", new DummyColumnName("time_col"),
//             "sizeTarget", new DummyColumnName("total_size"),
//             "timeTarget", new DummyColumnName("total_time"),
//             "sizeUnit", new DummyText("MB"),
//             "timeUnit", new DummyText("seconds")
//         ));

//         directive.initialize(args);

//         List<Row> input = new ArrayList<>();
//         input.add(new Row("size_col", "1048576").add("time_col", "2"));
//         input.add(new Row("size_col", "524288").add("time_col", "1.5"));

//         ExecutorContext ctx = new SimpleExecutionContext();
//         directive.execute(input, ctx);
//         List<Row> result = directive.complete(ctx);

//         Row output = result.get(0);
//         assertEquals(1.5, (Double) output.getValue("total_size"), 0.01);
//         assertEquals(3.5, (Double) output.getValue("total_time"), 0.01);

//         SimpleExecutionContext.clear();
//     }
// }
