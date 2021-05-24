package datamodel.objects;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map;
import java.util.Observable;

public class ObservableStrings extends Observable {

    // the key is the identifier, and the value is the content
    String name;
    MapProperty<String, String> strings;

    public ObservableStrings(String name, Map<String, String> strings) {
        ObservableMap<String, String> temp = FXCollections.observableHashMap();
        strings.forEach(temp::put);
        this.name = name;
        this.strings = new SimpleMapProperty<>(temp);
    }

    public String getName() {
        return name;
    }

    public ObservableMap<String, String> getStrings() {
        return strings.get();
    }

    public MapProperty<String, String> stringsProperty() {
        return strings;
    }

    public void upsertString(String id, String content) {
        this.strings.getValue().put(id, content);
        notifyObservers(new String[]{id, content});
    }
}
