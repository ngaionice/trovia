package datamodel;

import datamodel.objects.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class DataModel implements Observer {

    DatabaseController database;
    String language;

    ObservableMap<String, ObservableBench> sessionBenches;
    ObservableMap<String, ObservableCollection> sessionCollections;
    ObservableMap<String, ObservableItem> sessionItems;
    ObservableMap<String, ObservablePlaceable> sessionPlaceables;
    ObservableMap<String, ObservableRecipe> sessionRecipes;
    Map<String, ObservableStrings> sessionStrings;

    Map<String, ObservableBench> changedBenches = new HashMap<>();
    Map<String, ObservableCollection> changedCollections = new HashMap<>();
    Map<String, ObservableItem> changedItems = new HashMap<>();
    Map<String, ObservablePlaceable> changedPlaceables = new HashMap<>();
    Map<String, ObservableRecipe> changedRecipes = new HashMap<>();
    Map<String, String> changedExtractedStrings = new HashMap<>();
    Map<String, String> changedCustomStrings = new HashMap<>();

    private final ObjectProperty<ObservableBench> currentBench = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ObservableCollection> currentCollection = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ObservableItem> currentItem = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ObservablePlaceable> currentPlaceable = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ObservableRecipe> currentRecipe = new SimpleObjectProperty<>(null);
    private final StringProperty currentString = new SimpleStringProperty(null);

    public DataModel(String databasePath, String lang) throws SQLException {
        database = new DatabaseController();
        language = lang;
        database.loadDatabase(databasePath);

        sessionBenches = database.getBenches();
        sessionCollections = database.getCollections(language);
        sessionItems = database.getItems(language);
        sessionPlaceables = database.getPlaceables(language);
        sessionRecipes = database.getRecipes();
        sessionStrings = database.getStrings(language);
        System.out.println("Data loaded.");
    }

    @Override
    public void update(Observable o, Object arg) {
        // yes, this is not at all OO and is ugly, will refactor eventually
        if (o instanceof ObservableBench) {
            ObservableBench object = (ObservableBench) o;
            changedBenches.put(object.getRPath(), object);
        } else if (o instanceof ObservableCollection) {
            ObservableCollection object = (ObservableCollection) o;
            changedCollections.put(object.getRPath(), object);
        } else if (o instanceof ObservableItem) {
            ObservableItem object = (ObservableItem) o;
            changedItems.put(object.getRPath(), object);
        } else if (o instanceof ObservablePlaceable) {
            ObservablePlaceable object = (ObservablePlaceable) o;
            changedPlaceables.put(object.getRPath(), object);
        } else if (o instanceof ObservableRecipe) {
            ObservableRecipe object = (ObservableRecipe) o;
            changedRecipes.put(object.getRPath(), object);
        } else if (o instanceof ObservableStrings) {
            ObservableStrings object = (ObservableStrings) o;
            if (arg instanceof String[]) {
                String[] entry = (String[]) arg;
                if (object.getName().equals("extracted")) {
                    changedExtractedStrings.put(entry[0], entry[1]);
                } else {
                    changedCustomStrings.put(entry[0], entry[1]);
                }
            }

        }
    }

    // GETTERS - SESSION DATA

    public ObservableMap<String, ObservableBench> getSessionBenches() {
        return sessionBenches;
    }

    public ObservableMap<String, ObservableCollection> getSessionCollections() {
        return sessionCollections;
    }

    public ObservableMap<String, ObservableItem> getSessionItems() {
        return sessionItems;
    }

    public ObservableMap<String, ObservablePlaceable> getSessionPlaceables() {
        return sessionPlaceables;
    }

    public ObservableMap<String, ObservableRecipe> getSessionRecipes() {
        return sessionRecipes;
    }

    public Map<String, ObservableStrings> getSessionStrings() {
        return sessionStrings;
    }

    // GETTERS - CHANGED DATA

    public Map<String, ObservableBench> getChangedBenches() {
        return changedBenches;
    }

    public Map<String, ObservableCollection> getChangedCollections() {
        return changedCollections;
    }

    public Map<String, ObservableItem> getChangedItems() {
        return changedItems;
    }

    public Map<String, ObservablePlaceable> getChangedPlaceables() {
        return changedPlaceables;
    }

    public Map<String, ObservableRecipe> getChangedRecipes() {
        return changedRecipes;
    }

    public Map<String, String> getChangedExtractedStrings() {
        return changedExtractedStrings;
    }

    public Map<String, String> getChangedCustomStrings() {
        return changedCustomStrings;
    }

    // GETTERS AND SETTERS - CURRENTLY SELECTED DATA

    public ObservableBench getCurrentBench() {
        return currentBench.get();
    }

    public ObjectProperty<ObservableBench> currentBenchProperty() {
        return currentBench;
    }

    public void setCurrentBench(ObservableBench currentBench) {
        this.currentBench.set(currentBench);
    }

    public ObservableCollection getCurrentCollection() {
        return currentCollection.get();
    }

    public ObjectProperty<ObservableCollection> currentCollectionProperty() {
        return currentCollection;
    }

    public void setCurrentCollection(ObservableCollection currentCollection) {
        this.currentCollection.set(currentCollection);
    }

    public ObservableItem getCurrentItem() {
        return currentItem.get();
    }

    public ObjectProperty<ObservableItem> currentItemProperty() {
        return currentItem;
    }

    public void setCurrentItem(ObservableItem currentItem) {
        this.currentItem.set(currentItem);
    }

    public ObservablePlaceable getCurrentPlaceable() {
        return currentPlaceable.get();
    }

    public ObjectProperty<ObservablePlaceable> currentPlaceableProperty() {
        return currentPlaceable;
    }

    public void setCurrentPlaceable(ObservablePlaceable currentPlaceable) {
        this.currentPlaceable.set(currentPlaceable);
    }

    public ObservableRecipe getCurrentRecipe() {
        return currentRecipe.get();
    }

    public ObjectProperty<ObservableRecipe> currentRecipeProperty() {
        return currentRecipe;
    }

    public void setCurrentRecipe(ObservableRecipe currentRecipe) {
        this.currentRecipe.set(currentRecipe);
    }

    public String getCurrentString() {
        return currentString.get();
    }

    public StringProperty currentStringProperty() {
        return currentString;
    }

    public void setCurrentString(String currentString) {
        this.currentString.set(currentString);
    }
}
