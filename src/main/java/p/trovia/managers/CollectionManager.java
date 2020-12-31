package p.trovia.managers;

import p.trovia.objects.Collection;
import p.trovia.objects.CollectionEnums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionManager implements Serializable {

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
        return collectionMap.get(rPath).getName();
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

    public String getRecipe(String rPath) {
        return collectionMap.get(rPath).getRecipe();
    }

    public Integer[] getMastery(String rPath) {
        return new Integer[] {collectionMap.get(rPath).getTroveMR(), collectionMap.get(rPath).getGeodeMR()};
    }

    public int getPowerRank(String rPath) {
        return collectionMap.get(rPath).getPowerRank();
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

    public void setRecipe(String rPath, String recipeRPath) {
        collectionMap.get(rPath).setRecipe(recipeRPath);
        addMap.put(rPath, collectionMap.get(rPath));
    }
}
