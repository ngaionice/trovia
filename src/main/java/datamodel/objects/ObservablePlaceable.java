package datamodel.objects;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Observable;

public class ObservablePlaceable extends Observable implements Article {

    StringProperty name;
    StringProperty desc;
    StringProperty rPath;
    ListProperty<String> notes;
    BooleanProperty tradable;

    /**
     * Constructor for new Placeable creations. Not for database imports.
     */
    public ObservablePlaceable(String name, String desc, String rPath) {
        this.name = new SimpleStringProperty(name);
        this.desc = new SimpleStringProperty(desc);
        this.rPath = new SimpleStringProperty(rPath);
        this.notes = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.tradable = new SimpleBooleanProperty(true);
    }

    public ObservablePlaceable(String name, String desc, String rPath, List<String> notes, boolean isTradable) {
        this.name = new SimpleStringProperty(name);
        this.desc = new SimpleStringProperty(desc);
        this.rPath = new SimpleStringProperty(rPath);
        this.notes = new SimpleListProperty<>(FXCollections.observableArrayList(notes));
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

    public void addNotes(String noteID) {
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
