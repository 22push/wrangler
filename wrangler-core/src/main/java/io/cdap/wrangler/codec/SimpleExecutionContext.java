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
import java.util.HashMap;
import java.util.Map;

public class SimpleExecutionContext {
    private static final ThreadLocal<Map<String, Object>> context = ThreadLocal.withInitial(HashMap::new);

    public static Object get(String key) {
        return context.get().get(key);
    }

    public static void set(String key, Object value) {
        context.get().put(key, value);
    }

    public static void clear() {
        context.remove();
    }
}
// This class provides a simple execution context for storing and retrieving key-value pairs in a thread-local manner.
// It uses a ThreadLocal variable to ensure that each thread has its own context, preventing interference between threads.