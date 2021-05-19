package datamodel.objects;

import datamodel.CollectionEnums;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;
import java.util.Map;
import java.util.Observable;

public class ObservableCollection extends Observable implements Article {

    StringProperty name;
    StringProperty desc;
    StringProperty rPath;
    IntegerProperty troveMR;
    IntegerProperty geodeMR;
    ListProperty<CollectionEnums.Type> types;
    MapProperty<CollectionEnums.Property, Double> properties;
    MapProperty<CollectionEnums.Buff, Double> buffs;
    ListProperty<String> notes;

    public ObservableCollection(String name, String desc, String rPath, int troveMR, int geodeMR,
                                List<CollectionEnums.Type> types,
                                Map<CollectionEnums.Property, Double> properties,
                                Map<CollectionEnums.Buff, Double> buffs, List<String> notes) {
        this.name = new SimpleStringProperty(name);
        this.desc = new SimpleStringProperty(desc);
        this.rPath = new SimpleStringProperty(rPath);
        this.troveMR = new SimpleIntegerProperty(troveMR);
        this.geodeMR = new SimpleIntegerProperty(geodeMR);
        this.types = new SimpleListProperty<>(FXCollections.observableArrayList(types));
        this.notes = new SimpleListProperty<>(FXCollections.observableArrayList(notes));

        ObservableMap<CollectionEnums.Property, Double> tempProps = FXCollections.observableHashMap();
        ObservableMap<CollectionEnums.Buff, Double> tempBuffs = FXCollections.observableHashMap();

        properties.forEach(tempProps::put);
        buffs.forEach(tempBuffs::put);

        this.properties = new SimpleMapProperty<>(tempProps);
        this.buffs = new SimpleMapProperty<>(tempBuffs);

    }

    public void setName(String name) {
        this.name.set(name);
        notifyObservers();
    }

    public void setDesc(String desc) {
        this.desc.set(desc);
        notifyObservers();
    }

    public void setRPath(String rPath) {
        this.rPath.set(rPath);
        notifyObservers();
    }

    public void setTroveMR(int troveMR) {
        this.troveMR.set(troveMR);
        notifyObservers();
    }

    public void setGeodeMR(int geodeMR) {
        this.geodeMR.set(geodeMR);
        notifyObservers();
    }

    public void addType(CollectionEnums.Type type) {
        types.getValue().add(type);
        notifyObservers();
    }

    public void updateProperties(CollectionEnums.Property key, double val) {
        properties.getValue().put(key, val);
        notifyObservers();
    }

    public void updateBuffs(CollectionEnums.Buff key, double val) {
        buffs.getValue().put(key, val);
        notifyObservers();
    }

    public void addNote(String noteID) {
        notes.getValue().add(noteID);
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

    public ListProperty<CollectionEnums.Type> typesProperty() {
        return types;
    }

    public MapProperty<CollectionEnums.Property, Double> propertiesProperty() {
        return properties;
    }

    public MapProperty<CollectionEnums.Buff, Double> buffsProperty() {
        return buffs;
    }

    public ListProperty<String> notesProperty() {
        return notes;
    }

    public String getName() {
        return name.get();
    }

    public String getDesc() {
        return desc.get();
    }

    public String getRPath() {
        return rPath.get();
    }

    public int getTroveMR() {
        return troveMR.get();
    }

    public int getGeodeMR() {
        return geodeMR.get();
    }

    public ObservableList<CollectionEnums.Type> getTypes() {
        return types.get();
    }

    public ObservableMap<CollectionEnums.Property, Double> getProperties() {
        return properties.get();
    }

    public ObservableMap<CollectionEnums.Buff, Double> getBuffs() {
        return buffs.get();
    }

    public ObservableList<String> getNotes() {
        return notes.get();
    }
}
