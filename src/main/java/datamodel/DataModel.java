package datamodel;

import datamodel.objects.*;
import datamodel.parser.Parser;
import datamodel.parser.parsestrategies.ParseException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableMap;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class DataModel implements Observer {

    Parser parser;
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
        parser = new Parser();
        language = lang;
        database.loadDatabase(databasePath);

        sessionBenches = database.getBenches();
        sessionCollections = database.getCollections(language);
        sessionItems = database.getItems(language);
        sessionPlaceables = database.getPlaceables(language);
        sessionRecipes = database.getRecipes();
        sessionStrings = database.getStrings(language);
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

    // CREATING OBJECTS

    public void createObject(String absPath, Parser.ObjectType type) throws IOException, ParseException {
        switch (type) {
            case ITEM:
                upsertItem((ObservableItem) parser.createObject(absPath, type));
                break;
            case PLACEABLE:
                upsertPlaceable((ObservablePlaceable) parser.createObject(absPath, type));
                break;
            case BENCH:
            case PROFESSION:
                upsertBench((ObservableBench) parser.createObject(absPath, type));
                break;
            case RECIPE:
                upsertRecipe((ObservableRecipe) parser.createObject(absPath, type));
                break;
            case COLLECTION:
                upsertCollection((ObservableCollection) parser.createObject(absPath, type));
                break;
            case STRING:
//                try {
//                    langM.addLangFile((LangFile) parser.createObject(absPath, type)); // TODO: fix this line up properly
//                    return null;
//                } catch (ParseException e) {
//                    return absPath;
//                }
        }
    }

    private void upsertItem(ObservableItem item) {
        String rPath = item.getRPath();
        if (!sessionItems.containsKey(rPath)) {
            sessionItems.put(item.getRPath(), item);
            changedItems.put(item.getRPath(), item);
        } else {
            // update the name, description, and unlocks
            ObservableItem existing = sessionItems.get(rPath);
            boolean changed = false;
            if (!existing.getName().equals(item.getName())) { // names are not nullable
                existing.setName(item.getName());
                changed = true;
            }
            if (existing.getDesc() == null || (item.getDesc() != null || !existing.getDesc().equals(item.getDesc()))) {
                existing.setDesc(item.getDesc());
                changed = true;
            }
            if (!isListEqual(existing.getUnlocks(), item.getUnlocks())) {
                existing.setUnlocks(item.getUnlocks());
                changed = true;
            }
            if (changed) changedItems.put(rPath, existing);
        }
    }

    private void upsertPlaceable(ObservablePlaceable placeable) {
        String rPath = placeable.getRPath();
        if (!sessionPlaceables.containsKey(rPath)) {
            sessionPlaceables.put(rPath, placeable);
            changedPlaceables.put(rPath, placeable);
        } else {
            ObservablePlaceable existing = sessionPlaceables.get(rPath);
            boolean changed = false;
            if (!existing.getName().equals(placeable.getName())) {
                existing.setName(placeable.getName());
                changed = true;
            }
            if (existing.getDesc() == null || (placeable.getDesc() != null || !existing.getDesc().equals(placeable.getDesc()))) {
                existing.setDesc(placeable.getDesc());
                changed = true;
            }
            if (changed) changedPlaceables.put(rPath, existing);
        }
    }

    private void upsertRecipe(ObservableRecipe recipe) {
        String rPath = recipe.getRPath();
        if (!sessionRecipes.containsKey(rPath)) {
            sessionRecipes.put(rPath, recipe);
            changedRecipes.put(rPath, recipe);
        } else {
            ObservableRecipe existing = sessionRecipes.get(rPath);
            boolean changed = false;
            if (!existing.getName().equals(recipe.getName())) {
                existing.setName(recipe.getName());
                changed = true;
            }
            if (!existing.getCosts().equals(recipe.getCosts())) {
                existing.setCosts(recipe.getCosts());
                changed = true;
            }
            if (!existing.getOutput().equals(recipe.getOutput())) {
                existing.setOutput(recipe.getOutput());
                changed = true;
            }
            if (changed) changedRecipes.put(rPath, recipe);
        }
    }

    private void upsertBench(ObservableBench bench) {
        String rPath = bench.getRPath();
        if (!sessionBenches.containsKey(rPath)) {
            sessionBenches.put(rPath, bench);
            changedBenches.put(rPath, bench);
        } else {
            ObservableBench existing = sessionBenches.get(rPath);
            boolean changed = false;
            if (!existing.getName().equals(bench.getName())) {
                existing.setName(bench.getName());
                changed = true;
            }
            // don't compare the profession name, since it always starts as null
            if (!existing.getCategories().equals(bench.getCategories())) {
                existing.setCategories(bench.getCategories());
                changed = true;
            }
            if (changed) changedBenches.put(rPath, existing);
        }
    }

    private void upsertCollection(ObservableCollection collection) {
        String rPath = collection.getRPath();
        if (!sessionCollections.containsKey(rPath)) {
            sessionCollections.put(rPath, collection);
            changedCollections.put(rPath, collection);
        } else {
            ObservableCollection existing = sessionCollections.get(rPath);
            boolean changed = false;
            if (!existing.getName().equals(collection.getName())) {
                existing.setName(collection.getName());
                changed = true;
            }
            if (existing.getDesc() == null || (collection.getDesc() != null || !existing.getDesc().equals(collection.getDesc()))) {
                existing.setDesc(collection.getDesc());
                changed = true;
            }
            // ignore the mastery values as they are always added manually
            if (!existing.getTypes().equals(collection.getTypes())) {
                existing.setTypes(collection.getTypes());
                changed = true;
            }
            if (!existing.getBuffs().equals(collection.getBuffs())) {
                existing.setBuffs(collection.getBuffs());
                changed = true;
            }
            if (!existing.getProperties().equals(collection.getProperties())) {
                existing.setProperties(collection.getProperties());
                changed = true;
            }
            if (changed) changedCollections.put(rPath, existing);
        }
    }

    /**
     * Compares 2 lists of strings and returns whether they contain the same strings (regardless of order).
     */
    private boolean isListEqual(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            return false;
        } else {
            Collections.sort(list1);
            Collections.sort(list2);
            return list1.equals(list2);
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
