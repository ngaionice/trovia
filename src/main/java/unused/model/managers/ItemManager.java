package unused.model.managers;

import unused.model.gateways.DatabaseGateway;
import unused.model.objects.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager implements Manager, SearchManager, Serializable {

    // use rPath as the key, as it is unique to each Item
    Map<String, Item> itemMap = new HashMap<>(5000);

    // used to track changes between local version and online database:
    Map<String, Item> addMap = new HashMap<>(1000);
    Map<String, Item> removeMap = new HashMap<>(1000);

    // addMap is used to track new items and edited items
    // removeMap is used to track removed items (relative to last serialized/synchronization state)

    public void addItem(Item item) {
        itemMap.put(item.getRPath(), item);
        addMap.put(item.getRPath(), item);
    }

    public void removeItem(String rPath) {
        removeMap.put(rPath, itemMap.get(rPath));
        itemMap.remove(rPath);
    }

    // getters

    /**
     * Returns the language file path of this item's name.
     *
     * @param rPath relative path of the item
     * @return language file path of this item's name
     */
    public String getName(String rPath) {
        if (itemMap.containsKey(rPath)) {
            return itemMap.get(rPath).getName();
        }
        return null;
    }

    /**
     * Returns the language file path of this item's description.
     *
     * @param rPath relative path of the item
     * @return language file path of this item's description
     */
    public String getDesc(String rPath) {
//        if ()
        return itemMap.get(rPath).getDesc();
    }

    /**
     * Returns an array of relative paths of Collections that get unlocked upon consumption of this item.
     * Returns null if item consumption does not unlock anything/ cannot be consumed.
     *
     * @param rPath relative path of this item
     * @return an array of relative paths of Collections, or null
     */
    public String[] getUnlocks(String rPath) {
        if (itemMap.get(rPath).isUnlocker()) {
            return itemMap.get(rPath).getUnlocks();
        }
        return null;
    }

    /**
     * Returns an array of relative paths of Items/Collections that get unlocked upon deconstruction of of this item.
     * Returns null if item cannot be deconstructed.
     *
     * @param rPath relative path of this item
     * @return an array of relative paths of Items/Collections, or null
     */
    public Map<String, Integer> getDecons(String rPath) {
        if (itemMap.get(rPath).isDecon()) {
            return itemMap.get(rPath).getDecons();
        }
        return null;
    }

    /**
     * Returns a map of maps, with each sub-map representing a rarity, and containing the lootable items and their quantities.
     * Returns null if the item is not a lootbox.
     *
     * @param rPath relative path of the item
     * @return a map of maps
     */
    public Map<String, Map<String, String>> getLootbox(String rPath) {
        if (itemMap.get(rPath).isLootbox()) {
            return itemMap.get(rPath).getLootbox();
        }
        return null;
    }

    /**
     * Returns relative paths of the recipe for crafting this item. If no such recipe exists, returns null.
     *
     * @param rPath relative path of the item
     * @return relative path of the recipe
     */
    public List<String> getRecipe(String rPath) {
        return itemMap.get(rPath).getRecipes();
    }

    /**
     * Returns a map with the properties of this item. Keys include:
     * "isUnlocker", "isCraftable", "isLootbox", "isDecon"
     *
     * @param rPath relative path of the item
     * @return a map containing properties of this item
     */
    public Map<String, Boolean> getProperties(String rPath) {
        Item item = itemMap.get(rPath);
        Map<String, Boolean> map = new HashMap<>(4);
        map.put("isUnlocker", item.isUnlocker());
        map.put("isCraftable", item.isCraftable());
        map.put("isLootbox", item.isLootbox());
        map.put("isDecon", item.isDecon());
        return map;
    }

    /**
     * Returns a list of string arrays containing names and relative paths of all stored items.
     *
     * Array format: [language file path of name, relative path]
     *
     * @param type "all", "new", "removed"
     * @return a list of string arrays
     */
    public List<String[]> getAllNamesAndRPaths(String type) {
        Map<String, Item> map = type.equals("all") ? itemMap : type.equals("new") ? addMap : removeMap;
        if (!map.isEmpty()) {
            List<String[]> list = new ArrayList<>();
            for (Item item: map.values()) {
                list.add(new String[] {item.getName(), item.getRPath()});
            }
            return list;
        }
        return null;
    }

    public Map<String, Item> getAllItems() {
        return itemMap;
    }

    public Map<String, Item> getNewItems() {
        return addMap;
    }

    public Map<String, Item> getRemovedItems() {
        return removeMap;
    }

    // setters

    /**
     * Adds the input recipe relative path to the Item, and marks the item as craftable.
     *
     * @param rPath relative path of the item
     * @param recipeRPath relative path of the recipe
     */
    public void addRecipe(String rPath, String recipeRPath) {
        Item item = itemMap.get(rPath);
        item.addRecipe(recipeRPath);
        item.setCraftable(true);
        addMap.put(item.getRPath(), item);
    }

    public void setDecon(String rPath, Map<String, Integer> decons) {
        Item item = itemMap.get(rPath);
        item.setDecons(decons);
        item.setDeconnable(true);
        addMap.put(item.getRPath(), item);
    }

    public void addLootBoxCommon(String rPath, List<String[]> loot) {
        Item item = itemMap.get(rPath);
        for (String[] lootable: loot) {
            item.addLootboxCommon(lootable[0], lootable[1]);
        }
        addMap.put(item.getRPath(), item);
    }

    public void addLootBoxUncommon(String rPath, List<String[]> loot) {
        Item item = itemMap.get(rPath);
        for (String[] lootable: loot) {
            item.addLootboxUncommon(lootable[0], lootable[1]);
        }
        addMap.put(item.getRPath(), item);
    }

    public void addLootBoxRare(String rPath, List<String[]> loot) {
        Item item = itemMap.get(rPath);
        for (String[] lootable: loot) {
            item.addLootboxRare(lootable[0], lootable[1]);
        }
        addMap.put(item.getRPath(), item);
    }

    public void addNotes(String rPath, String notesRPath) {
        Item item = itemMap.get(rPath);
        item.addNotes(notesRPath);
        addMap.put(item.getRPath(), item);
    }

    public List<String> getNotes(String rPath) {
        return itemMap.get(rPath).getNotes();
    }

    public void clearNewItems() {
        addMap.clear();
    }

    public void clearRemovedItems() {
        removeMap.clear();
    }

    // export
    public void export(DatabaseGateway gateway, boolean exportAll) {
        Map<String, Item> map = exportAll ? getAllItems() : getNewItems();
        gateway.exportItems(map);
    }
}
