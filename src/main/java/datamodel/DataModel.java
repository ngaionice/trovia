package datamodel;

import datamodel.objects.*;
import datamodel.objects.Collection;
import datamodel.parser.Parser;
import datamodel.parser.parsestrategies.ParseException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.io.IOException;
import java.util.*;

public class DataModel implements Observer {

    Parser parser = new Parser();

    ObservableMap<String, Bench> sessionBenches = FXCollections.observableHashMap();
    ObservableMap<String, Collection> sessionCollections = FXCollections.observableHashMap();
    ObservableMap<String, Item> sessionItems = FXCollections.observableHashMap();
    ObservableMap<String, Placeable> sessionPlaceables = FXCollections.observableHashMap();
    ObservableMap<String, Recipe> sessionRecipes = FXCollections.observableHashMap();
    Map<String, Strings> sessionStrings = new HashMap<>();

    Map<String, Bench> changedBenches = new HashMap<>();
    Map<String, Collection> changedCollections = new HashMap<>();
    Map<String, Item> changedItems = new HashMap<>();
    Map<String, Placeable> changedPlaceables = new HashMap<>();
    Map<String, Recipe> changedRecipes = new HashMap<>();
    Map<String, String> changedExtractedStrings = new HashMap<>();
    Map<String, String> changedCustomStrings = new HashMap<>();

    private final ObjectProperty<Bench> currentBench = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Collection> currentCollection = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Item> currentItem = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Placeable> currentPlaceable = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Recipe> currentRecipe = new SimpleObjectProperty<>(null);
    private final StringProperty currentString = new SimpleStringProperty(null);

    @Override
    public void update(Observable o, Object arg) {
        // yes, this is not at all OO and is ugly, will refactor eventually
        if (o instanceof Bench) {
            Bench object = (Bench) o;
            changedBenches.put(object.getRPath(), object);
        } else if (o instanceof Collection) {
            Collection object = (Collection) o;
            changedCollections.put(object.getRPath(), object);
        } else if (o instanceof Item) {
            Item object = (Item) o;
            changedItems.put(object.getRPath(), object);
        } else if (o instanceof Placeable) {
            Placeable object = (Placeable) o;
            changedPlaceables.put(object.getRPath(), object);
        } else if (o instanceof Recipe) {
            Recipe object = (Recipe) o;
            changedRecipes.put(object.getRPath(), object);
        } else if (o instanceof Strings) {
            Strings object = (Strings) o;
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

    public void createObject(String absPath, Enums.ObjectType type) throws IOException, ParseException {
        switch (type) {
            case ITEM:
                upsertItem((Item) parser.createObject(absPath, type));
                break;
            case PLACEABLE:
                upsertPlaceable((Placeable) parser.createObject(absPath, type));
                break;
            case BENCH:
            case PROFESSION:
                upsertBench((Bench) parser.createObject(absPath, type));
                break;
            case RECIPE:
                upsertRecipe((Recipe) parser.createObject(absPath, type));
                break;
            case COLLECTION:
                upsertCollection((Collection) parser.createObject(absPath, type));
                break;
            case STRING:
                insertExtractedStrings((LangFile) parser.createObject(absPath, type));
                break;
        }
    }

    private void upsertItem(Item item) {
        String rPath = item.getRPath();
        if (!sessionItems.containsKey(rPath)) {
            sessionItems.put(item.getRPath(), item);
            changedItems.put(item.getRPath(), item);
        } else {
            // update the name, description, and unlocks
            Item existing = sessionItems.get(rPath);
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

    private void upsertPlaceable(Placeable placeable) {
        String rPath = placeable.getRPath();
        if (!sessionPlaceables.containsKey(rPath)) {
            sessionPlaceables.put(rPath, placeable);
            changedPlaceables.put(rPath, placeable);
        } else {
            Placeable existing = sessionPlaceables.get(rPath);
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

    private void upsertRecipe(Recipe recipe) {
        String rPath = recipe.getRPath();
        if (!sessionRecipes.containsKey(rPath)) {
            sessionRecipes.put(rPath, recipe);
            changedRecipes.put(rPath, recipe);
        } else {
            Recipe existing = sessionRecipes.get(rPath);
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

    private void upsertBench(Bench bench) {
        String rPath = bench.getRPath();
        if (!sessionBenches.containsKey(rPath)) {
            sessionBenches.put(rPath, bench);
            changedBenches.put(rPath, bench);
        } else {
            Bench existing = sessionBenches.get(rPath);
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

    private void upsertCollection(Collection collection) {
        String rPath = collection.getRPath();
        if (!sessionCollections.containsKey(rPath)) {
            sessionCollections.put(rPath, collection);
            changedCollections.put(rPath, collection);
        } else {
            Collection existing = sessionCollections.get(rPath);
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

    private void insertExtractedStrings(LangFile newStrings) {
        Strings extractedStrings = sessionStrings.get("extracted");
        if (extractedStrings != null) {
            newStrings.getStrings().forEach((k, v) -> {
                extractedStrings.upsertString(k, v);
                changedExtractedStrings.put(k, v);
            });
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

    public ObservableMap<String, Bench> getSessionBenches() {
        return sessionBenches;
    }

    public ObservableMap<String, Collection> getSessionCollections() {
        return sessionCollections;
    }

    public ObservableMap<String, Item> getSessionItems() {
        return sessionItems;
    }

    public ObservableMap<String, Placeable> getSessionPlaceables() {
        return sessionPlaceables;
    }

    public ObservableMap<String, Recipe> getSessionRecipes() {
        return sessionRecipes;
    }

    public Map<String, Strings> getSessionStrings() {
        return sessionStrings;
    }

    // GETTERS - CHANGED DATA

    public Map<String, Bench> getChangedBenches() {
        return changedBenches;
    }

    public Map<String, Collection> getChangedCollections() {
        return changedCollections;
    }

    public Map<String, Item> getChangedItems() {
        return changedItems;
    }

    public Map<String, Placeable> getChangedPlaceables() {
        return changedPlaceables;
    }

    public Map<String, Recipe> getChangedRecipes() {
        return changedRecipes;
    }

    public Map<String, String> getChangedExtractedStrings() {
        return changedExtractedStrings;
    }

    public Map<String, String> getChangedCustomStrings() {
        return changedCustomStrings;
    }

    // GETTERS AND SETTERS - CURRENTLY SELECTED DATA

    public Bench getCurrentBench() {
        return currentBench.get();
    }

    public ObjectProperty<Bench> currentBenchProperty() {
        return currentBench;
    }

    public void setCurrentBench(Bench currentBench) {
        this.currentBench.set(currentBench);
    }

    public ObjectProperty<Collection> currentCollectionProperty() {
        return currentCollection;
    }

    public void setCurrentCollection(Collection currentCollection) {
        this.currentCollection.set(currentCollection);
    }

    public Item getCurrentItem() {
        return currentItem.get();
    }

    public ObjectProperty<Item> currentItemProperty() {
        return currentItem;
    }

    public void setCurrentItem(Item currentItem) {
        this.currentItem.set(currentItem);
    }

    public ObjectProperty<Placeable> currentPlaceableProperty() {
        return currentPlaceable;
    }

    public void setCurrentPlaceable(Placeable currentPlaceable) {
        this.currentPlaceable.set(currentPlaceable);
    }

    public ObjectProperty<Recipe> currentRecipeProperty() {
        return currentRecipe;
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe.set(currentRecipe);
    }

    public StringProperty currentStringProperty() {
        return currentString;
    }

    public void setCurrentString(String currentString) {
        this.currentString.set(currentString);
    }

}
