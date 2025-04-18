package net.subworld.lang;

import com.google.gson.JsonElement;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Stack;
import java.util.function.BiConsumer;

public interface IParser extends AutoCloseable, BiConsumer<JsonElement, Stack<String>> {
    void parse(Reader reader);
    void push(BiConsumer<String, String> consumer);
    void pop();
    default void parse(String str) {
        parse(new StringReader(str));
    }
    default void parse(InputStream is) {
        parse(new InputStreamReader(is, StandardCharsets.UTF_8));
    }
    default void parse(File file) throws IOException {
        parse(Files.newInputStream(file.toPath()));
    }
    default void parse(URL url) throws IOException {
        parse(url.openConnection().getInputStream());
    }
}