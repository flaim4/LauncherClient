package net.subworld.lang;

import java.util.HashMap;
import java.util.Map;

public class Language implements Lang {
    private Formatter formatter = String::format;
    private Map<String, String> map;
    public Language() {
        map = new HashMap<>();
        try (IParser parser = new JsonParser()) {
            parser.push(map::put);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public String get(String id) {
        return map.get(id);
    }

    @Override
    public String getOrDefult(String id) {
        return map.getOrDefault(id, id);
    }

    @Override
    public String getOrDefult(String id, String defult) {
        return map.getOrDefault(id, defult);
    }

    @Override
    public boolean has(String id) {
        return map.containsKey(id);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public String format(String id, Object... args) {
        return formatter.format(getOrDefult(id), args);
    }

    public Formatter setFormater(Formatter formatter) {
        this.formatter = formatter;
        return formatter;
    }
}
