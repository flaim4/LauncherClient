package net.subworld.lang;

public interface Lang {
    String get(String id);
    String getOrDefult(String id, String defult);
    default String getOrDefult(String id) {
        return getOrDefult(id, id);
    }
    boolean has(String id);
    void clear();
    String format(String id, Object... args);
    Formatter setFormater(Formatter formatter);
}