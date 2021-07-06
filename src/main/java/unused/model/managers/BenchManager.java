package unused.model.managers;

import unused.model.gateways.DatabaseGateway;
import unused.model.objects.Bench;

import java.io.Serializable;
import java.util.*;

public class BenchManager implements Manager, SearchManager, Serializable {

    // use rPath as the key, as it is unique to each Bench
    Map<String, Bench> benchMap = new HashMap<>(5000);

    // used to track changes between local version and online database:
    Map<String, Bench> addMap = new HashMap<>(1000);
    Map<String, Bench> removeMap = new HashMap<>(1000);

    // addMap is used to track new and edited benches
    // removeMap is used to track removed benches (relative to last serialized/synchronization state)

    public void addBench(Bench bench) {
        benchMap.put(bench.getRPath(), bench);
        addMap.put(bench.getRPath(), bench);
    }

    public void removeBench(String rPath) {
        removeMap.put(rPath, benchMap.get(rPath));
        benchMap.remove(rPath);
    }

    // getters

    public String getName(String rPath) {
        if (benchMap.containsKey(rPath)) {
            return benchMap.get(rPath).getName();
        }
        return null;
    }

    public List<String> getAllRecipes(String rPath) {
        return benchMap.get(rPath).getAllRecipes();
    }

    public Map<String[], List<String>> getAllRecipesByCategory(String rPath) {
        return benchMap.get(rPath).getAllRecipesByCategory();
    }

    public List<String[]> getAllNamesAndRPaths(String type) {
        Map<String, Bench> map = type.equals("all") ? benchMap : type.equals("new") ? addMap : removeMap;
        if (!map.isEmpty()) {
            List<String[]> list = new ArrayList<>();
            for (Bench item: map.values()) {
                list.add(new String[] {item.getName(), item.getRPath()});
            }
            return list;
        }
        return null;
    }

    /**
     * Returns the associated profession name for the specified bench, if applicable.
     * Returns null otherwise.
     *
     * @param rPath the relative path of the bench
     * @return the associated profession name
     */
    public String getProfessionName(String rPath) {
        if (benchMap.get(rPath).isProfession()) {
            return benchMap.get(rPath).getProfessionName();
        }
        return null;
    }

    public Map<String, Bench> getAllBenches() {
        return benchMap;
    }

    public Map<String, Bench> getNewBenches() {
        return addMap;
    }

    public Map<String, Bench> getRemovedBenches() {
        return removeMap;
    }

    // setters

    /**
     * Sets the bench name identifier of the specified bench. Used for professions.
     *
     * @param rPath the relative path of the bench
     * @param name the identifier of the name (in a LangFile)
     */
    public void setName(String rPath, String name) {
        benchMap.get(rPath).setName(name);
    }

    public void clearNewBenches() {
        addMap.clear();
    }

    public void clearRemovedBenches() {
        removeMap.clear();
    }

    // export
    public void export(DatabaseGateway gateway, boolean exportAll) {
        Map<String, Bench> map = exportAll ? getAllBenches() : getNewBenches();
        gateway.exportBenches(map);
    }

}
