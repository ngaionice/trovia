package datamodel.objects;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map;

public class CollectionIndex implements Article{

    StringProperty rPath;
    StringProperty type;
    MapProperty<String, String> names;
    MapProperty<String, Map<String, String>> categories;

    public CollectionIndex(String rPath, String type, Map<String, String> nameMap, Map<String, Map<String, String>> categories) {
        this.rPath = new SimpleStringProperty(rPath);
        this.type = new SimpleStringProperty(type);
        ObservableMap<String, String> tempNames = FXCollections.observableHashMap();
        nameMap.forEach(tempNames::put);
        this.names = new SimpleMapProperty<>(tempNames);
        ObservableMap<String, Map<String, String>> tempCats = FXCollections.observableHashMap();
        categories.forEach(tempCats::put);
        this.categories = new SimpleMapProperty<>(tempCats);
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

    public ObservableMap<String, String> getNames() {
        return names.get();
    }

    public MapProperty<String, String> namesProperty() {
        return names;
    }

    public ObservableMap<String, Map<String, String>> getCategories() {
        return categories.get();
    }

    public MapProperty<String, Map<String, String>> categoriesProperty() {
        return categories;
    }
}
