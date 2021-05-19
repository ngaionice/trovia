package model.managers;

import model.gateways.DatabaseGateway;
import model.objects.Collection;
import model.objects.CollectionEnums;

import java.io.Serializable;
import java.util.*;

public class CollectionManager implements Manager, SearchManager, Serializable {

    // use rPath as the key, as it is unique to each Collection
    Map<String, Collection> collectionMap = new HashMap<>(5000);

    // used to track changes between local version and online database:
    Map<String, Collection> addMap = new HashMap<>(1000);
    Map<String, Collection> removeMap = new HashMap<>(1000);

    // addMap is used to track new and edited collections
    // removeMap is used to track removed collections (relative to last serialized/synchronization state)

    public void addCollection(Collection col) {
        collectionMap.put(col.getRPath(), col);
        addMap.put(col.getRPath(), col);
    }

    public void removeCollection(String rPath) {
        removeMap.put(rPath, collectionMap.get(rPath));
        collectionMap.remove(rPath);
    }

    // getters

    public String getName(String rPath) {
        if (collectionMap.containsKey(rPath)) {
            return collectionMap.get(rPath).getName();
        }
        return null;
    }

    public String getDesc(String rPath) {
        return collectionMap.get(rPath).getDesc();
    }

    public List<CollectionEnums.Type> getTypes(String rPath) {
        return collectionMap.get(rPath).getTypes();
    }

    public Map<CollectionEnums.Property, Double> getProperties(String rPath) {
        return collectionMap.get(rPath).getProperties();
    }

    public Map<CollectionEnums.Buff, Double> getBuffs(String rPath) {
        return collectionMap.get(rPath).getBuffs();
    }

    public List<String> getNotes(String rPath) {
        return collectionMap.get(rPath).getNotes();
    }

    public List<String> getRecipe(String rPath) {
        return collectionMap.get(rPath).getRecipes();
    }

    public Integer[] getMastery(String rPath) {
        return new Integer[] {collectionMap.get(rPath).getTroveMR(), collectionMap.get(rPath).getGeodeMR()};
    }

    public int getPowerRank(String rPath) {
        return collectionMap.get(rPath).getPowerRank();
    }

    public List<String[]> getAllNamesAndRPaths(String type) {
        Map<String, Collection> map = type.equals("all") ? collectionMap : type.equals("new") ? addMap : removeMap;
        if (!map.isEmpty()) {
            List<String[]> list = new ArrayList<>();
            for (Collection item: map.values()) {
                list.add(new String[] {item.getName(), item.getRPath()});
            }
            return list;
        }
        return null;
    }

    public Map<String, Collection> getAllCollections() {
        return collectionMap;
    }

    public Map<String, Collection> getNewCollections() {
        return addMap;
    }

    public Map<String, Collection> getRemovedCollections() {
        return removeMap;
    }

    // setters

    public void setTroveMR(String rPath, int mastery) {
        collectionMap.get(rPath).setTroveMR(mastery);
        addMap.put(rPath, collectionMap.get(rPath));
    }

    public void setGeodeMR(String rPath, int mastery) {
        collectionMap.get(rPath).setGeodeMR(mastery);
        addMap.put(rPath, collectionMap.get(rPath));
    }

    public void setPowerRank(String rPath, int pr) {
        collectionMap.get(rPath).setPowerRank(pr);
        addMap.put(rPath, collectionMap.get(rPath));
    }

    public void addNotes(String rPath, String notesRPath) {
        collectionMap.get(rPath).addNotes(notesRPath);
        addMap.put(rPath, collectionMap.get(rPath));
    }

    public void addRecipe(String rPath, String recipeRPath) {
        System.out.println(collectionMap.get(rPath).getName());
        collectionMap.get(rPath).addRecipe(recipeRPath);
        addMap.put(rPath, collectionMap.get(rPath));
    }

    public void clearNewCollections() {
        addMap.clear();
    }

    public void clearRemovedCollections() {
        removeMap.clear();
    }

    // export
    public void export(DatabaseGateway gateway, boolean exportAll) {
        Map<String, Collection> map = exportAll ? getAllCollections() : getNewCollections();
        gateway.exportCollections(map);
    }
}
