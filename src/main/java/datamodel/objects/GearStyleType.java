package datamodel.objects;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map;
import java.util.Observable;

public class GearStyleType extends Observable implements Article{

    StringProperty rPath;
    StringProperty type;
    MapProperty<String, Map<String, String[]>> styles;

    public GearStyleType(String rPath, String type, Map<String, Map<String, String[]>> styles) {
        this.rPath = new SimpleStringProperty(rPath);
        this.type = new SimpleStringProperty(type);
        ObservableMap<String, Map<String, String[]>> baseMap =FXCollections.observableHashMap();
        styles.forEach(baseMap::put);
        this.styles = new SimpleMapProperty<>(baseMap);
    }

    public void upsertStyle(String category, String blueprint, String[] data) {
        if (data.length == 3) {
            styles.get(category).put(blueprint, data);
            notifyObservers();
        }
    }

    public void updateBlueprint(String category, String oldBlueprint, String newBlueprint) {
        String[] value = styles.get().get(category).get(oldBlueprint);
        styles.get().remove(oldBlueprint);
        styles.get(category).put(newBlueprint, value);
        notifyObservers();
    }

    public String getRPath() {
        return rPath.get();
    }

    public StringProperty rPathProperty() {
        return rPath;
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public ObservableMap<String, Map<String, String[]>> getStyles() {
        return styles.get();
    }

    public MapProperty<String, Map<String, String[]>> stylesProperty() {
        return styles;
    }
}
