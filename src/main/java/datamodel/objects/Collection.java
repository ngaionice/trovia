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
    IntegerProperty blueprintIndex = null;
    ListProperty<String> possibleBlueprints;
    ListProperty<String> notes;

    public Collection(String name, String desc, String rPath, int troveMR, int geodeMR,
                      List<Enums.Type> types,
                      Map<Enums.Property, Double> properties,
                      Map<Enums.Buff, Double> buffs, List<String> notes) {
        this.name = new SimpleStringProperty(name);
        this.desc = new SimpleStringProperty(desc);
        this.rPath = new SimpleStringProperty(rPath);
        this.troveMR = new SimpleIntegerProperty(troveMR);
        this.geodeMR = new SimpleIntegerProperty(geodeMR);
        this.types = new SimpleListProperty<>(FXCollections.observableArrayList(types));
        this.notes = new SimpleListProperty<>(FXCollections.observableArrayList(notes));

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

    public ListProperty<Enums.Type> typesProperty() {
        return types;
    }

    public MapProperty<Enums.Property, Double> propertiesProperty() {
        return properties;
    }

    public MapProperty<Enums.Buff, Double> buffsProperty() {
        return buffs;
    }

    public ListProperty<String> notesProperty() {
        return notes;
    }

    public IntegerProperty blueprintIndexProperty() {
        return blueprintIndex;
    }

    public ListProperty<String> possibleBlueprintsProperty() {
        return possibleBlueprints;
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

    public ObservableList<String> getNotes() {
        return notes.get();
    }

    public int getBlueprintIndex() {
        return blueprintIndex.get();
    }

    public void setBlueprintIndex(int blueprintIndex) {
        this.blueprintIndex.set(blueprintIndex);
    }

    public ObservableList<String> getPossibleBlueprints() {
        return possibleBlueprints.get();
    }

    public void setPossibleBlueprints(ObservableList<String> possibleBlueprints) {
        this.possibleBlueprints.set(possibleBlueprints);
    }
}
