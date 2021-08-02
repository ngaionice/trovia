package datamodel;

import datamodel.objects.*;
import datamodel.objects.Collection;
import datamodel.parser.Parser;
import datamodel.parser.parsestrategies.ParseException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.io.IOException;
import java.util.*;

public class DataModel {

    Parser parser = new Parser();
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

    ObservableMap<String, Bench> changedBenches = FXCollections.observableHashMap();
    ObservableMap<String, Collection> changedCollections = FXCollections.observableHashMap();
    ObservableMap<String, CollectionIndex> changedCollectionIndices = FXCollections.observableHashMap();
    ObservableMap<String, GearStyleType> changedGearStyleTypes = FXCollections.observableHashMap();
    ObservableMap<String, Item> changedItems = FXCollections.observableHashMap();
    ObservableMap<String, Placeable> changedPlaceables = FXCollections.observableHashMap();
    ObservableMap<String, Recipe> changedRecipes = FXCollections.observableHashMap();
    ObservableMap<String, Skin> changedSkins = FXCollections.observableHashMap();
    ObservableMap<String, String> changedStrings = FXCollections.observableHashMap();

    List<String> mergedBenchPaths = new ArrayList<>();
    List<String> mergedCollectionPaths = new ArrayList<>();
    List<String> mergedCollectionIndexPaths = new ArrayList<>();
    List<String> mergedGearStyleTypePaths = new ArrayList<>();
    List<String> mergedItemPaths = new ArrayList<>();
    List<String> mergedPlaceablePaths = new ArrayList<>();
    List<String> mergedRecipePaths = new ArrayList<>();
    List<String> mergedSkinPaths = new ArrayList<>();
    List<String> mergedStringIds = new ArrayList<>();

    public void createBlueprintMapping(String dirPath) throws IOException {
        blueprintMap = parser.getObjectBlueprintMappingFromDir(dirPath);
    }

    // CREATING OBJECTS

    public void createObject(String absPath, Enums.ObjectType type, boolean useRPath) throws IOException, ParseException {
        Enums.ObjectType inputType = type == Enums.ObjectType.PROFESSION ? Enums.ObjectType.BENCH : type;
        if (type == Enums.ObjectType.PLACEABLE) {
            Placeable p = (Placeable) parser.createObject(absPath, type, useRPath);
            if (p.getBlueprint() == null && blueprintMap != null && blueprintMap.containsKey(p.getRPath())) {
                p.setBlueprint(blueprintMap.get(p.getRPath()));
            }
            addArticleToChanges(p, type, false);
        } else {
            addArticleToChanges(parser.createObject(absPath, type, useRPath), inputType, false);
        }
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

    public Map<String, GearStyleType> getSessionGearStyleTypes() {
        return sessionGearStyleTypes;
    }

    public Map<String, Skin> getSessionSkins() {
        return sessionSkins;
    }

    // SETTERS

    public void addArticleToSession(Article a, Enums.ObjectType type) {
        switch (type) {
            case BENCH:
                sessionBenches.put(a.getRPath(), (Bench) a);
                break;
            case COLLECTION:
                sessionCollections.put(a.getRPath(), (Collection) a);
                break;
            case COLL_INDEX:
                sessionCollectionIndices.put(a.getRPath(), (CollectionIndex) a);
                break;
            case GEAR_STYLE:
                sessionGearStyleTypes.put(a.getRPath(), (GearStyleType) a);
                break;
            case ITEM:
                sessionItems.put(a.getRPath(), (Item) a);
                break;
            case PLACEABLE:
                sessionPlaceables.put(a.getRPath(), (Placeable) a);
                break;
            case RECIPE:
                sessionRecipes.put(a.getRPath(), (Recipe) a);
                break;
            case SKIN:
                sessionSkins.put(a.getRPath(), (Skin) a);
                break;
            case LANG_FILE:
                LangFile l = (LangFile) a;
                l.getStrings().forEach((k, v) -> sessionStrings.upsertString(k, v));
                break;
            default:
                throw new IllegalArgumentException("No such type: " + type);
        }
    }

    public void addArticleToChanges(Article a, Enums.ObjectType type, boolean forceAdd) {
        String rPath = type != Enums.ObjectType.LANG_FILE ? a.getRPath() : null;
        switch (type) {
            case BENCH:
                if (!sessionBenches.containsKey(rPath) || !sessionBenches.get(rPath).equals(a) || forceAdd)
                    changedBenches.put(rPath, (Bench) a);
                break;
            case COLLECTION:
                if (!sessionCollections.containsKey(rPath) || !sessionCollections.get(rPath).equals(a) || forceAdd)
                    changedCollections.put(rPath, (Collection) a);
                break;
            case COLL_INDEX:
                if (!sessionCollectionIndices.containsKey(rPath) || !sessionCollectionIndices.get(rPath).equals(a) || forceAdd)
                    changedCollectionIndices.put(rPath, (CollectionIndex) a);
                break;
            case GEAR_STYLE:
                if (!sessionGearStyleTypes.containsKey(rPath) || !sessionGearStyleTypes.get(rPath).equals(a) || forceAdd)
                    changedGearStyleTypes.put(rPath, (GearStyleType) a);
                break;
            case ITEM:
                if (!sessionItems.containsKey(rPath) || !sessionItems.get(rPath).equals(a) || forceAdd)
                    changedItems.put(a.getRPath(), (Item) a);
                break;
            case PLACEABLE:
                if (!sessionPlaceables.containsKey(rPath) || !sessionPlaceables.get(rPath).equals(a) || forceAdd)
                    changedPlaceables.put(rPath, (Placeable) a);
                break;
            case RECIPE:
                if (!sessionRecipes.containsKey(rPath) || !sessionRecipes.get(rPath).equals(a) || forceAdd)
                    changedRecipes.put(rPath, (Recipe) a);
                break;
            case SKIN:
                if (!sessionSkins.containsKey(rPath) || !sessionSkins.get(rPath).equals(a) || forceAdd)
                    changedSkins.put(rPath, (Skin) a);
                break;
            case LANG_FILE:
                LangFile l = (LangFile) a;
                l.getStrings().forEach((k, v) -> {
                    if (!sessionStrings.hasString(k) || !Objects.equals(sessionStrings.getString(k), v) || forceAdd)
                        changedStrings.put(k, v);
                });
                break;
        }
    }

    /**
     * Removes an Article from the data.
     *
     * @param identifier        The identifier of the entity
     * @param type              All ObjectTypes are valid besides PROFESSION and LANG_FILE. Inputting them results in no action.
     * @param removeFromChanges If true, removes from the changed data; else removes from session data.
     */
    public void removeArticle(String identifier, Enums.ObjectType type, boolean removeFromChanges) {
        switch (type) {
            case BENCH:
                if (removeFromChanges) changedBenches.remove(identifier);
                else sessionBenches.remove(identifier);
                break;
            case COLLECTION:
                if (removeFromChanges) changedCollections.remove(identifier);
                else sessionCollections.remove(identifier);
                break;
            case COLL_INDEX:
                if (removeFromChanges) changedCollectionIndices.remove(identifier);
                else sessionCollectionIndices.remove(identifier);
                break;
            case GEAR_STYLE:
                if (removeFromChanges) changedGearStyleTypes.remove(identifier);
                else sessionGearStyleTypes.remove(identifier);
                break;
            case ITEM:
                if (removeFromChanges) changedItems.remove(identifier);
                else sessionItems.remove(identifier);
                break;
            case PLACEABLE:
                if (removeFromChanges) changedPlaceables.remove(identifier);
                else sessionPlaceables.remove(identifier);
                break;
            case RECIPE:
                if (removeFromChanges) changedRecipes.remove(identifier);
                else sessionRecipes.remove(identifier);
                break;
            case SKIN:
                if (removeFromChanges) changedSkins.remove(identifier);
                else sessionSkins.remove(identifier);
                break;
        }
    }

    public void setSessionString(Strings s) {
        sessionStrings = s;
    }

    // GETTERS - CHANGED DATA

    public ObservableMap<String, Bench> getChangedBenches() {
        return changedBenches;
    }

    public ObservableMap<String, Collection> getChangedCollections() {
        return changedCollections;
    }

    public ObservableMap<String, Item> getChangedItems() {
        return changedItems;
    }

    public ObservableMap<String, Placeable> getChangedPlaceables() {
        return changedPlaceables;
    }

    public ObservableMap<String, Recipe> getChangedRecipes() {
        return changedRecipes;
    }

    public ObservableMap<String, String> getChangedStrings() {
        return changedStrings;
    }

    public ObservableMap<String, CollectionIndex> getChangedCollectionIndices() {
        return changedCollectionIndices;
    }

    public ObservableMap<String, GearStyleType> getChangedGearStyleTypes() {
        return changedGearStyleTypes;
    }

    public ObservableMap<String, Skin> getChangedSkins() {
        return changedSkins;
    }

    public boolean hasNoUnmergedChanges() {
        return changedBenches.isEmpty() && changedCollections.isEmpty() && changedItems.isEmpty() &&
                changedPlaceables.isEmpty() && changedRecipes.isEmpty() && changedStrings.isEmpty() &&
                changedCollectionIndices.isEmpty() && changedGearStyleTypes.isEmpty() && changedSkins.isEmpty();
    }

    public boolean hasNoMergedPaths() {
        return mergedBenchPaths.isEmpty() && mergedCollectionPaths.isEmpty() && mergedCollectionIndexPaths.isEmpty() &&
                mergedGearStyleTypePaths.isEmpty() && mergedItemPaths.isEmpty() && mergedPlaceablePaths.isEmpty() &&
                mergedRecipePaths.isEmpty() && mergedSkinPaths.isEmpty() && mergedStringIds.isEmpty();
    }

    // GETTERS - MERGED DATA

    public List<String> getMergedPaths(Enums.ObjectType type) {
        switch (type) {
            case BENCH:
                return mergedBenchPaths;
            case COLLECTION:
                return mergedCollectionPaths;
            case COLL_INDEX:
                return mergedCollectionIndexPaths;
            case GEAR_STYLE:
                return mergedGearStyleTypePaths;
            case ITEM:
                return mergedItemPaths;
            case PLACEABLE:
                return mergedPlaceablePaths;
            case RECIPE:
                return mergedRecipePaths;
            case SKIN:
                return mergedSkinPaths;
            case LANG_FILE:
                return mergedStringIds;
            default:
                throw new IllegalArgumentException("No such type: " + type);
        }
    }

    // SETTERS - MERGED DATA

    public void addMergedPath(String path, Enums.ObjectType type) {
        switch (type) {
            case BENCH:
                this.mergedBenchPaths.add(path);
                break;
            case COLLECTION:
                this.mergedCollectionPaths.add(path);
                break;
            case COLL_INDEX:
                this.mergedCollectionIndexPaths.add(path);
                break;
            case GEAR_STYLE:
                this.mergedGearStyleTypePaths.add(path);
                break;
            case ITEM:
                this.mergedItemPaths.add(path);
                break;
            case PLACEABLE:
                this.mergedPlaceablePaths.add(path);
                break;
            case RECIPE:
                this.mergedRecipePaths.add(path);
                break;
            case SKIN:
                this.mergedSkinPaths.add(path);
                break;
            case LANG_FILE:
                this.mergedStringIds.add(path);
                break;
            default:
                throw new IllegalArgumentException("No such type: " + type);
        }
    }
}
