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
    StringProperty blueprint;
    BooleanProperty tradable;

    public Placeable(String name, String desc, String rPath, String blueprint) {
        this.name = new SimpleStringProperty(name);
        this.desc = new SimpleStringProperty(desc);
        this.rPath = new SimpleStringProperty(rPath);
        this.blueprint = new SimpleStringProperty(blueprint);
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
