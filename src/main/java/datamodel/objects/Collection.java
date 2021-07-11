package datamodel.objects;

import datamodel.Enums;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;
import java.util.Map;
import java.util.Observable;

public class Collection extends Observable implements Article, ArticleTable {

    StringProperty name;
    StringProperty desc;
    StringProperty rPath;
    IntegerProperty troveMR;
    IntegerProperty geodeMR;
    ListProperty<Enums.Type> types;
    MapProperty<Enums.Property, Double> properties;
    MapProperty<Enums.Buff, Double> buffs;
    StringProperty blueprint;

    public Collection(String name, String desc, String rPath, int troveMR, int geodeMR, String blueprint,
                      List<Enums.Type> types,
                      Map<Enums.Property, Double> properties,
                      Map<Enums.Buff, Double> buffs) {
        this.name = new SimpleStringProperty(name);
        this.desc = new SimpleStringProperty(desc);
        this.rPath = new SimpleStringProperty(rPath);
        this.troveMR = new SimpleIntegerProperty(troveMR);
        this.geodeMR = new SimpleIntegerProperty(geodeMR);
        this.blueprint = new SimpleStringProperty(blueprint);
        this.types = new SimpleListProperty<>(FXCollections.observableArrayList(types));

        ObservableMap<Enums.Property, Double> tempProps = FXCollections.observableHashMap();
        ObservableMap<Enums.Buff, Double> tempBuffs = FXCollections.observableHashMap();

        properties.forEach(tempProps::put);
        buffs.forEach(tempBuffs::put);

        this.properties = new SimpleMapProperty<>(tempProps);
        this.buffs = new SimpleMapProperty<>(tempBuffs);

    }

    public void addType(Enums.Type type) {
        types.getValue().add(type);
        notifyObservers();
    }

    public void updateProperties(Enums.Property key, double val) {
        properties.getValue().put(key, val);
        notifyObservers();
    }

    public void updateBuffs(Enums.Buff key, double val) {
        buffs.getValue().put(key, val);
        notifyObservers();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descProperty() {
        return desc;
    }

    public StringProperty rPathProperty() {
        return rPath;
    }

    public IntegerProperty troveMRProperty() {
        return troveMR;
    }

    public IntegerProperty geodeMRProperty() {
        return geodeMR;
    }

    public ListProperty<Enums.Type> typesProperty() {
        return types;
    }

    public MapProperty<Enums.Property, Double> propertiesProperty() {
        return properties;
    }

    public MapProperty<Enums.Buff, Double> buffsProperty() {
        return buffs;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
        notifyObservers();
    }

    public String getDesc() {
        return desc.get();
    }

    public void setDesc(String desc) {
        this.desc.set(desc);
        notifyObservers();
    }

    public String getRPath() {
        return rPath.get();
    }

    public void setRPath(String rPath) {
        this.rPath.set(rPath);
        notifyObservers();
    }

    public int getTroveMR() {
        return troveMR.get();
    }

    public void setTroveMR(int troveMR) {
        this.troveMR.set(troveMR);
        notifyObservers();
    }

    public int getGeodeMR() {
        return geodeMR.get();
    }

    public void setGeodeMR(int geodeMR) {
        this.geodeMR.set(geodeMR);
        notifyObservers();
    }

    public ObservableList<Enums.Type> getTypes() {
        return types.get();
    }

    public void setTypes(ObservableList<Enums.Type> types) {
        this.types.set(types);
    }

    public ObservableMap<Enums.Property, Double> getProperties() {
        return properties.get();
    }

    public void setProperties(ObservableMap<Enums.Property, Double> properties) {
        this.properties.set(properties);
    }

    public ObservableMap<Enums.Buff, Double> getBuffs() {
        return buffs.get();
    }

    public void setBuffs(ObservableMap<Enums.Buff, Double> buffs) {
        this.buffs.set(buffs);
    }

    public String getBlueprint() {
        return blueprint.get();
    }

    public StringProperty blueprintProperty() {
        return blueprint;
    }

    public void setBlueprint(String blueprint) {
        this.blueprint.set(blueprint);
    }
}
