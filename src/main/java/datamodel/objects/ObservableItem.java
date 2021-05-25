package datamodel.objects;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.*;

public class ObservableItem extends Observable implements Article, ArticleTable {

    StringProperty name;

    /**
     * The string property of the item's description identifier. The wrapped value is nullable.
     */
    StringProperty desc;
    StringProperty rPath;
    ListProperty<String> unlocks;
    MapProperty<String, Integer> decons;

    /**
     * The map property of the item's common loot. The wrapped value is nullable.
     */
    MapProperty<String, String> lootCommon;

    /**
     * The map property of the item's uncommon loot. The wrapped value is nullable.
     */
    MapProperty<String, String> lootUncommon;

    /**
     * The map property of the item's rare loot. The wrapped value is nullable.
     */
    MapProperty<String, String> lootRare;
    ListProperty<String> notes;
    BooleanProperty tradable;

    public ObservableItem(String name, String desc, String rPath, String[] unlocks) {
        this.name = new SimpleStringProperty(name);
        this.desc = new SimpleStringProperty(desc);
        this.rPath = new SimpleStringProperty(rPath);
        this.unlocks = new SimpleListProperty<>(FXCollections.observableArrayList(unlocks));
        this.notes = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.tradable = new SimpleBooleanProperty(true);

        this.decons = new SimpleMapProperty<>(FXCollections.observableHashMap());
        this.lootCommon = new SimpleMapProperty<>(null);
        this.lootUncommon = new SimpleMapProperty<>(null);
        this.lootRare = new SimpleMapProperty<>(null);
    }

    public ObservableItem(String name, String desc, String rPath, String[] unlocks, Map<String, Integer> decons,
                          Map<String, String> lootCommon, Map<String, String> lootUncommon, Map<String, String> lootRare,
                          List<String> notes, boolean isTradable) {
        this.name = new SimpleStringProperty(name);
        this.desc = desc == null ? new SimpleStringProperty("N/A") : new SimpleStringProperty(desc);
        this.rPath = new SimpleStringProperty(rPath);
        this.unlocks = new SimpleListProperty<>(FXCollections.observableArrayList(unlocks));
        this.notes = new SimpleListProperty<>(FXCollections.observableArrayList(notes));

        ObservableMap<String, Integer> tempDecons = FXCollections.observableHashMap();
        ObservableMap<String, String> tempLootC = FXCollections.observableHashMap();
        ObservableMap<String, String> tempLootU = FXCollections.observableHashMap();
        ObservableMap<String, String> tempLootR = FXCollections.observableHashMap();

        decons.forEach(tempDecons::put);
        lootCommon.forEach(tempLootC::put);
        lootUncommon.forEach(tempLootU::put);
        lootRare.forEach(tempLootR::put);

        this.decons = new SimpleMapProperty<>(tempDecons);
        this.lootCommon = new SimpleMapProperty<>(tempLootC);
        this.lootUncommon = new SimpleMapProperty<>(tempLootU);
        this.lootRare = new SimpleMapProperty<>(tempLootR);
        this.tradable = new SimpleBooleanProperty(isTradable);
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

    public void addUnlocks(String unlock) {
        this.unlocks.getValue().add(unlock);
        notifyObservers();
    }

    public void upsertDecon(String itemRPath, int quantity) {
        this.decons.getValue().put(itemRPath, quantity);
        notifyObservers();
    }

    public void upsertLootCommon(String itemRPath, String quantity) {
        this.lootCommon.getValue().put(itemRPath, quantity);
        notifyObservers();
    }

    public void upsertLootUncommon(String itemRPath, String quantity) {
        this.lootUncommon.getValue().put(itemRPath, quantity);
        notifyObservers();
    }

    public void upsertLootRare(String itemRPath, String quantity) {
        this.lootRare.getValue().put(itemRPath, quantity);
        notifyObservers();
    }

    public void setUnlocks(List<String> unlocks) {
        this.unlocks.setValue(FXCollections.observableArrayList(unlocks));
    }

    public void addNote(String noteID) {
        this.notes.getValue().add(noteID);
        notifyObservers();
    }

    public void setTradable(boolean tradable) {
        this.tradable.set(tradable);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDesc() {
        return desc.get();
    }

    public StringProperty descProperty() {
        return desc;
    }

    public String getRPath() {
        return rPath.get();
    }

    public StringProperty rPathProperty() {
        return rPath;
    }

    public ObservableList<String> getUnlocks() {
        return unlocks.get();
    }

    public ListProperty<String> unlocksProperty() {
        return unlocks;
    }

    public ObservableMap<String, Integer> getDecons() {
        return decons.get();
    }

    public MapProperty<String, Integer> deconsProperty() {
        return decons;
    }

    public ObservableMap<String, String> getLootCommon() {
        return lootCommon.get();
    }

    public MapProperty<String, String> lootCommonProperty() {
        return lootCommon;
    }

    public ObservableMap<String, String> getLootUncommon() {
        return lootUncommon.get();
    }

    public MapProperty<String, String> lootUncommonProperty() {
        return lootUncommon;
    }

    public ObservableMap<String, String> getLootRare() {
        return lootRare.get();
    }

    public MapProperty<String, String> lootRareProperty() {
        return lootRare;
    }

    public ObservableList<String> getNotes() {
        return notes.get();
    }

    public ListProperty<String> notesProperty() {
        return notes;
    }

    public boolean isTradable() {
        return tradable.get();
    }

    public BooleanProperty tradableProperty() {
        return tradable;
    }
}
