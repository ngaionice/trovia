package datamodel;

import datamodel.objects.Collection;
import datamodel.objects.*;
import datamodel.parser.Parser;
import datamodel.parser.parsestrategies.ParseException;

import java.io.IOException;
import java.util.*;

public class DataModel {

    Parser parser = new Parser();
    Set<String> blueprintPaths = null;
    Map<String, String> blueprintMap = null;
    Map<String, Bench> sessionBenches = new HashMap<>();
    Map<String, Collection> sessionCollections = new HashMap<>();
    Map<String, CollectionIndex> sessionCollectionIndices = new HashMap<>();
    Map<String, GearStyleType> sessionGearStyleTypes = new HashMap<>();
    Map<String, Item> sessionItems = new HashMap<>();
    Map<String, Placeable> sessionPlaceables = new HashMap<>();
    Map<String, Recipe> sessionRecipes = new HashMap<>();
    Map<String, Skin> sessionSkins = new HashMap<>();
    Strings sessionStrings = new Strings("en", new HashMap<>());
    Map<String, Bench> changedBenches = new HashMap<>();
    Map<String, Collection> changedCollections = new HashMap<>();
    Map<String, CollectionIndex> changedCollectionIndices = new HashMap<>();
    Map<String, GearStyleType> changedGearStyles = new HashMap<>();
    Map<String, Item> changedItems = new HashMap<>();
    Map<String, Placeable> changedPlaceables = new HashMap<>();
    Map<String, Recipe> changedRecipes = new HashMap<>();
    Map<String, Skin> changedSkins = new HashMap<>();
    Map<String, String> changedStrings = new HashMap<>();

    public void createBlueprintPaths(String dirPath) {
        blueprintPaths = parser.getAllBlueprintPathsFromDir(dirPath, dirPath);
    }

    public Set<String> getBlueprintPaths() {
        if (blueprintPaths == null) {
            blueprintPaths = new HashSet<>();
        }
        return blueprintPaths;
    }

    public void createBlueprintMapping(String dirPath) throws IOException {
        blueprintMap = parser.getObjectBlueprintMappingFromDir(dirPath);
    }

    // CREATING OBJECTS

    public void createObject(String absPath, Enums.ObjectType type) throws IOException, ParseException {
        switch (type) {
            case BENCH:
            case PROFESSION:
                upsertBench((Bench) parser.createObject(absPath, type));
                break;
            case COLLECTION:
                upsertCollection((Collection) parser.createObject(absPath, type));
                break;
            case COLL_INDEX:
                upsertCollectionIndex((CollectionIndex) parser.createObject(absPath, type));
                break;
            case GEAR_STYLE:
                upsertGearStyleType((GearStyleType) parser.createObject(absPath, type));
                break;
            case ITEM:
                upsertItem((Item) parser.createObject(absPath, type));
                break;
            case PLACEABLE:
                Placeable p = (Placeable) parser.createObject(absPath, type);
                if (p.getBlueprint() == null && blueprintMap != null && blueprintMap.containsKey(p.getRPath())) {
                    p.setBlueprint(blueprintMap.get(p.getRPath()));
                }
                upsertPlaceable(p);
                break;
            case RECIPE:
                upsertRecipe((Recipe) parser.createObject(absPath, type));
                break;
            case SKIN:
                upsertSkin((Skin) parser.createObject(absPath, type));
                break;
            case STRING:
                upsertStrings((LangFile) parser.createObject(absPath, type));
                break;
        }
    }

    private void upsertBench(Bench bench) {
        String rPath = bench.getRPath();
        if (!sessionBenches.containsKey(rPath) || !sessionBenches.get(rPath).equals(bench)){
            changedBenches.put(rPath, bench);
        }
    }

    private void upsertCollection(Collection collection) {
        String rPath = collection.getRPath();
        if (!sessionCollections.containsKey(rPath) || !sessionCollections.get(rPath).equals(collection)) {
            changedCollections.put(rPath, collection);
        }
    }

    private void upsertCollectionIndex(CollectionIndex index) {
        String rPath = index.getRPath();
        if (!sessionCollectionIndices.containsKey(rPath) || !sessionCollectionIndices.get(rPath).equals(index)) {
            changedCollectionIndices.put(rPath, index);
        }
    }

    private void upsertGearStyleType(GearStyleType gearStyleType) {
        String rPath = gearStyleType.getRPath();
        if (!sessionGearStyleTypes.containsKey(rPath) || !sessionGearStyleTypes.get(rPath).equals(gearStyleType)) {
            changedGearStyles.put(rPath, gearStyleType);
        }
    }

    private void upsertItem(Item item) {
        String rPath = item.getRPath();
        if (!sessionItems.containsKey(rPath) || !sessionItems.get(rPath).equals(item)) {
            changedItems.put(item.getRPath(), item);
        }
    }

    private void upsertPlaceable(Placeable placeable) {
        String rPath = placeable.getRPath();
        if (!sessionPlaceables.containsKey(rPath) || !sessionPlaceables.get(rPath).equals(placeable)) {
            changedPlaceables.put(rPath, placeable);
        }
    }

    private void upsertRecipe(Recipe recipe) {
        String rPath = recipe.getRPath();
        if (!sessionRecipes.containsKey(rPath) || !sessionRecipes.get(rPath).equals(recipe)) {
            changedRecipes.put(rPath, recipe);
        }
    }

    private void upsertSkin(Skin skin) {
        String rPath = skin.getRPath();
        if (!sessionSkins.containsKey(rPath) || !sessionSkins.get(rPath).equals(skin)) {
            changedSkins.put(rPath, skin);
        }
    }

    private void upsertStrings(LangFile newStrings) {
        newStrings.getStrings().forEach((k, v) -> {
            if (!sessionStrings.hasString(k) || !sessionStrings.getString(k).equals(v)) {
                changedStrings.put(k, v);
            }
        });
    }

    // GETTERS - SESSION DATA

    public Map<String, Bench> getSessionBenches() {
        return sessionBenches;
    }

    public Map<String, Collection> getSessionCollections() {
        return sessionCollections;
    }

    public Map<String, Item> getSessionItems() {
        return sessionItems;
    }

    public Map<String, Placeable> getSessionPlaceables() {
        return sessionPlaceables;
    }

    public Map<String, Recipe> getSessionRecipes() {
        return sessionRecipes;
    }

    public Strings getSessionStrings() {
        return sessionStrings;
    }

    public Map<String, CollectionIndex> getSessionCollectionIndices() {
        return sessionCollectionIndices;
    }

    public Map<String, GearStyleType> getSessionGearStyles() {
        return sessionGearStyleTypes;
    }

    public Map<String, Skin> getSessionSkins() {
        return sessionSkins;
    }
    
    // SETTERS - SESSION DATA
    
    public void addSessionBench(Bench b) {
        sessionBenches.put(b.getRPath(), b);
    }

    public void addSessionCollection(Collection c) {
        sessionCollections.put(c.getRPath(), c);
    }

    public void addSessionItem(Item i) {
        sessionItems.put(i.getRPath(), i);
    }

    public void addSessionPlaceable(Placeable p) {
        sessionPlaceables.put(p.getRPath(), p);
    }

    public void addSessionSkin(Skin s) {
        sessionSkins.put(s.getRPath(), s);
    }

    public void addSessionRecipe(Recipe r) {
        sessionRecipes.put(r.getRPath(), r);
    }

    public void addSessionCollectionIndex(CollectionIndex c) {
        sessionCollectionIndices.put(c.getRPath(), c);
    }

    public void addSessionGearStyleType(GearStyleType g) {
        sessionGearStyleTypes.put(g.getRPath(), g);
    }

    public void setSessionString(Strings s) {
        sessionStrings = s;
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

    public Map<String, String> getChangedStrings() {
        return changedStrings;
    }

    public Map<String, CollectionIndex> getChangedCollectionIndices() {
        return changedCollectionIndices;
    }

    public Map<String, GearStyleType> getChangedGearStyles() {
        return changedGearStyles;
    }

    public Map<String, Skin> getChangedSkins() {
        return changedSkins;
    }
}
