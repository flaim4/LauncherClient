package net.subworld.lang;

@FunctionalInterface
public interface Formatter {
    String format(String string, Object... args);
}