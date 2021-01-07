package ui.searchables;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Searchable extends RecursiveTreeObject<Searchable> {

    public StringProperty name;
    public StringProperty rPath;
    public BooleanProperty selected;

    public Searchable(String name, String rPath) {
        this.name = new SimpleStringProperty(name);
        this.rPath = new SimpleStringProperty(rPath);
    }

    public String getName() {
        return name.get();
    }

    public String getRPath() {
        return rPath.get();
    }


}
