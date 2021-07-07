package datamodel.objects;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.*;

public class Item extends Observable implements Article, ArticleTable {

    StringProperty name;
    StringProperty desc;
    StringProperty rPath;
    ListProperty<String> unlocks;
    MapProperty<String, Integer> decons;
    MapProperty<String, String> lootCommon;
    MapProperty<String, String> lootUncommon;
    MapProperty<String, String> lootRare;
    IntegerProperty blueprintIndex;
    ListProperty<String> possibleBlueprints;
    BooleanProperty tradable;

    // nullable properties (the value in the value itself can be null):
    // desc, all 3 loot stuff

    public Item(String name, String desc, String rPath, String[] unlocks, String[] possibleBlueprints, boolean isLootbox) {
        this.name = new SimpleStringProperty(name);
        this.desc = new SimpleStringProperty(desc);
        this.rPath = new SimpleStringProperty(rPath);
        this.unlocks = new SimpleListProperty<>(FXCollections.observableArrayList(unlocks));
        this.tradable = new SimpleBooleanProperty(true);
        this.blueprintIndex = new SimpleIntegerProperty(possibleBlueprints.length >= 1 ? 0 : -1);
        this.possibleBlueprints = new SimpleListProperty<>(FXCollections.observableArrayList(possibleBlueprints));

        this.decons = new SimpleMapProperty<>(FXCollections.observableHashMap());
        this.lootCommon = new SimpleMapProperty<>(isLootbox ? FXCollections.observableHashMap() : null);
        this.lootUncommon = new SimpleMapProperty<>(isLootbox ? FXCollections.observableHashMap() : null);
        this.lootRare = new SimpleMapProperty<>(isLootbox ? FXCollections.observableHashMap() : null);
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


    public boolean isTradable() {
        return tradable.get();
    }

    public BooleanProperty tradableProperty() {
        return tradable;
    }

    public int getBlueprintIndex() {
        return blueprintIndex.get();
    }

    public IntegerProperty blueprintIndexProperty() {
        return blueprintIndex;
    }

    public void setBlueprintIndex(int blueprintIndex) {
        this.blueprintIndex.set(blueprintIndex);
    }

    public ObservableList<String> getPossibleBlueprints() {
        return possibleBlueprints.get();
    }

    public ListProperty<String> possibleBlueprintsProperty() {
        return possibleBlueprints;
    }

    public void setPossibleBlueprints(ObservableList<String> possibleBlueprints) {
        this.possibleBlueprints.set(possibleBlueprints);
    }
}
