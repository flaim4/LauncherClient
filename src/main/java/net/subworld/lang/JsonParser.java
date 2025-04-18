package net.subworld.lang;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.experimental.Delegate;

import java.io.Reader;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class JsonParser implements IParser {
    private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    public Gson GSON = new Gson();

    public Stack<BiConsumer<String, String>> stack = new Stack<>();

    @Override
    public void push(BiConsumer<String, String> consumer) {
        stack.push(consumer);
    }

    @Override
    public void pop() {
        stack.pop();
    }

    @Override
    public void parse(Reader reader) {
        JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            Stack<String> stack = new Stack<>();
            stack.push(entry.getKey());
            accept(entry.getValue(), stack);
        }
    }

    @Override
    public void accept(JsonElement jsonElement, Stack<String> strings) {
        if (jsonElement.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
                String key = entry.getKey();
                if (key.endsWith("#")) {

                }
                else {
                    Stack<String> strings2 = new Stack<>();
                    strings2.addAll(strings);
                    strings2.push(key);
                    accept(entry.getValue(), strings2);
                }
            }
        } else {
            stack.peek().accept(String.join(".", strings), UNSUPPORTED_FORMAT_PATTERN.matcher(jsonElement.getAsString()).replaceAll("%$1s"));
        }
    }

    @Override
    public void close() throws Exception {

    }
}
