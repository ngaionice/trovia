package datamodel.objects;

import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map;

public class Recipe implements Article {

    String name;
    String rPath;
    Map<String, Integer> costs;
    Map<String, Integer> output;

    public Recipe(String name, String rPath, Map<String, Integer> costs, Map<String, Integer> output) {
        this.name = name;
        this.rPath = rPath;

        ObservableMap<String, Integer> tempCosts = FXCollections.observableHashMap();
        ObservableMap<String, Integer> tempOutput = FXCollections.observableHashMap();

        costs.forEach(tempCosts::put);
        output.forEach(tempOutput::put);

        this.costs = new SimpleMapProperty<>(tempCosts);
        this.output = new SimpleMapProperty<>(tempOutput);
    }

    public void updateCost(String itemRPath, int quantity) {
        this.costs.put(itemRPath, quantity);
    }

    public void updateOutput(String articleRPath, int quantity) {
        this.output.put(articleRPath, quantity);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRPath() {
        return rPath;
    }

    public Map<String, Integer> getCosts() {
        return costs;
    }

    public void setCosts(Map<String, Integer> costs) {
        this.costs = costs;
    }

    public Map<String, Integer> getOutput() {
        return output;
    }

    public void setOutput(Map<String, Integer> output) {
        this.output = output;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Recipe)) return false;
        Recipe r = (Recipe) o;
        return name.equals(r.getName()) && rPath.equals(r.getRPath()) && costs.equals(r.getCosts()) && output.equals(r.getOutput());
    }
}
