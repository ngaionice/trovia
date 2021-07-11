package datamodel.objects;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Observable;

public class Item extends Observable implements Article, ArticleTable {

    StringProperty name;
    StringProperty desc; // nullable
    StringProperty rPath;
    ListProperty<String> unlocks;
    StringProperty blueprint;
    BooleanProperty tradable;
    BooleanProperty isLootbox;
    BooleanProperty willDecay;

    public Item(String name, String desc, String rPath, String[] unlocks, String blueprint, boolean isTradable, boolean isLootbox, boolean willDecay) {
        this.name = new SimpleStringProperty(name);
        this.desc = new SimpleStringProperty(desc);
        this.rPath = new SimpleStringProperty(rPath);
        this.unlocks = new SimpleListProperty<>(FXCollections.observableArrayList(unlocks));
        this.blueprint = new SimpleStringProperty(blueprint);

        this.tradable = new SimpleBooleanProperty(true);
        this.isLootbox = new SimpleBooleanProperty(isLootbox);
        this.willDecay = new SimpleBooleanProperty(willDecay);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
        notifyObservers();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDesc() {
        return desc.get();
    }

    public void setDesc(String desc) {
        this.desc.set(desc);
        notifyObservers();
    }

    public StringProperty descProperty() {
        return desc;
    }

    public String getRPath() {
        return rPath.get();
    }

    public void setRPath(String rPath) {
        this.rPath.set(rPath);
        notifyObservers();
    }

    public StringProperty rPathProperty() {
        return rPath;
    }

    public ObservableList<String> getUnlocks() {
        return unlocks.get();
    }

    public void addUnlock(String unlock) {
        this.unlocks.add(unlock);
        notifyObservers();
    }

    public void setUnlocks(List<String> unlocks) {
        this.unlocks.setValue(FXCollections.observableArrayList(unlocks));
        notifyObservers();
    }

    public ListProperty<String> unlocksProperty() {
        return unlocks;
    }

    public boolean isTradable() {
        return tradable.get();
    }

    public void setTradable(boolean tradable) {
        this.tradable.set(tradable);
        notifyObservers();
    }

    public BooleanProperty tradableProperty() {
        return tradable;
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

    public boolean isLootbox() {
        return isLootbox.get();
    }

    public BooleanProperty isLootboxProperty() {
        return isLootbox;
    }

    public void setIsLootbox(boolean isLootbox) {
        this.isLootbox.set(isLootbox);
        notifyObservers();
    }

    public boolean willDecay() {
        return willDecay.get();
    }

    public BooleanProperty willDecayProperty() {
        return willDecay;
    }

    public void setWillDecay(boolean willDecay) {
        this.willDecay.set(willDecay);
        notifyObservers();
    }
}
