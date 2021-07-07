package datamodel.objects;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Observable;

public class Placeable extends Observable implements Article, ArticleTable {

    StringProperty name;
    StringProperty desc;
    StringProperty rPath;
    BooleanProperty tradable;
    IntegerProperty blueprintIndex = null;
    ListProperty<String> possibleBlueprints;

    public Placeable(String name, String desc, String rPath) {
        this.name = new SimpleStringProperty(name);
        this.desc = new SimpleStringProperty(desc);
        this.rPath = new SimpleStringProperty(rPath);
        this.tradable = new SimpleBooleanProperty(true);
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
