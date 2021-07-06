package datamodel.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Skin implements Article, ArticleTable{

    StringProperty rPath;
    StringProperty name;
    StringProperty desc;
    StringProperty blueprint;

    public Skin(String rPath, String name, String desc, String blueprint) {
        this.rPath = new SimpleStringProperty(rPath);
        this.name = new SimpleStringProperty(name);
        this.desc = new SimpleStringProperty(desc);
        this.blueprint = new SimpleStringProperty(blueprint);
    }

    public String getRPath() {
        return rPath.get();
    }

    public StringProperty rPathProperty() {
        return rPath;
    }

    public void setRPath(String rPath) {
        this.rPath.set(rPath);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDesc() {
        return desc.get();
    }

    public StringProperty descProperty() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc.set(desc);
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
