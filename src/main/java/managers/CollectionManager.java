package managers;

import objects.Collection;
import objects.CollectionEnums;

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

    public List<CollectionEnums.CollectionType> getTypes(String rPath) {
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

    public List<String[]> getAllNamesAndRPaths() {
        if (!collectionMap.isEmpty()) {
            List<String[]> list = new ArrayList<>();
            for (Collection item: collectionMap.values()) {
                list.add(new String[] {item.getName(), item.getRPath()});
            }
            return list;
        }
        return null;
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
}
