package datamodel.objects;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map;
import java.util.Observable;

public class Strings {

    String lang;
    Map<String, String> strings; // the key is the identifier, and the value is the content

    public Strings(String lang, Map<String, String> strings) {
        this.lang = lang;
        this.strings = strings;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Map<String, String> getStrings() {
        return strings;
    }

    public boolean hasString(String id) {
        return strings.containsKey(id);
    }

    public String getString(String id) {
        return strings.get(id);
    }

    public void upsertString(String id, String content) {
        this.strings.put(id, content);
    }

    public void removeString(String id) {
        this.strings.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Strings)) return false;
        Strings s = (Strings) o;
        return lang.equals(s.getLang()) && strings.equals(s.getStrings());
    }
}
