package datamodel.objects;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map;
import java.util.Observable;

public class ObservableStrings extends Observable {

    // the key is in format [language, identifier]
    MapProperty<String, String> strings;

    public ObservableStrings(Map<String, String> strings) {
        ObservableMap<String, String> temp = FXCollections.observableHashMap();
        strings.forEach(temp::put);
        this.strings = new SimpleMapProperty<>(temp);
    }

    public ObservableMap<String, String> getStrings() {
        return strings.get();
    }

    public MapProperty<String, String> stringsProperty() {
        return strings;
    }

    public void addString(String id, String content) {
        this.strings.getValue().put(id, content);
    }
}
