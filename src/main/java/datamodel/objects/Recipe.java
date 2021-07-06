package datamodel.objects;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map;
import java.util.Observable;

public class Recipe extends Observable implements Article, ArticleTable {

    StringProperty name;
    StringProperty rPath;
    MapProperty<String, Integer> costs;
    MapProperty<String, Integer> output;

    public Recipe(String name, String rPath, Map<String, Integer> costs, Map<String, Integer> output) {
        this.name = new SimpleStringProperty(name);
        this.rPath = new SimpleStringProperty(rPath);

        ObservableMap<String, Integer> tempCosts = FXCollections.observableHashMap();
        ObservableMap<String, Integer> tempOutput = FXCollections.observableHashMap();

        costs.forEach(tempCosts::put);
        output.forEach(tempOutput::put);

        this.costs = new SimpleMapProperty<>(tempCosts);
        this.output = new SimpleMapProperty<>(tempOutput);
    }

    public void setName(String name) {
        this.name.set(name);
        notifyObservers();
    }

    public void setRPath(String rPath) {
        this.rPath.set(rPath);
        notifyObservers();
    }

    public void updateCost(String itemRPath, int quantity) {
        this.costs.getValue().put(itemRPath, quantity);
        notifyObservers();
    }

    public void setCosts(ObservableMap<String, Integer> costs) {
        this.costs.set(costs);
        notifyObservers();
    }

    public void updateOutput(String articleRPath, int quantity) {
        this.output.getValue().put(articleRPath, quantity);
        notifyObservers();
    }

    public void setOutput(ObservableMap<String, Integer> output) {
        this.output.set(output);
        notifyObservers();
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getRPath() {
        return rPath.get();
    }

    public StringProperty rPathProperty() {
        return rPath;
    }

    public ObservableMap<String, Integer> getCosts() {
        return costs.get();
    }

    public MapProperty<String, Integer> costsProperty() {
        return costs;
    }

    public ObservableMap<String, Integer> getOutput() {
        return output.get();
    }

    public MapProperty<String, Integer> outputProperty() {
        return output;
    }
}
