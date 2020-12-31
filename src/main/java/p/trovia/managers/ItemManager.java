package p.trovia.managers;

import p.trovia.objects.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager implements Manager, Serializable {

    // use rPath as the key, as it is unique to each Item
    Map<String, Item> itemMap = new HashMap<>(5000);

    // used to track changes between local version and online database:
    Map<String, Item> addMap = new HashMap<>(1000);
    Map<String, Item> removeMap = new HashMap<>(1000);

    // addMap is used to track new items and changes to existing items
    // removeMap is used to track removed items/original copies of items (relative to last serialized/synchronization state)

    public void addItem(Item item) {

        // first check if item with same key exists; if true, then we add it to removeMap before overwriting it
        if (itemMap.containsKey(item.getRPath())) {

            // if this item is getting overwritten/updated a second time or more, we keep the original version
            if (!removeMap.containsKey(item.getRPath())) {
                removeMap.put(item.getRPath() ,itemMap.get(item.getRPath()));
                // TODO: use a logger to note such an incident
            }
        }

        // add/update the item
        itemMap.put(item.getRPath(), item);
        addMap.put(item.getRPath(), item);
    }

    public void removeItem(String rPath) {

        // as in addItem, if this item was updated before it gets deleted, we keep the original version in removeMap
        // and discard this updated version completely
        if (!removeMap.containsKey(rPath)) {
            removeMap.put(rPath, itemMap.get(rPath));
        }

        // remove the item
        itemMap.remove(rPath);
    }

    // getters

    /**
     * Returns the language file path of this item's name.
     *
     * @param rPath relative path of the item
     * @return language file path of this item's name
     */
    public String getItemName(String rPath) {
        return itemMap.get(rPath).getName();
    }

    /**
     * Returns the language file path of this item's description.
     *
     * @param rPath relative path of the item
     * @return language file path of this item's description
     */
    public String getItemDesc(String rPath) {
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
    public Map<String, Map<String, Integer>> getLootbox(String rPath) {
        if (itemMap.get(rPath).isLootbox()) {
            return itemMap.get(rPath).getLootbox();
        }
        return null;
    }

    /**
     * Returns relative path of the recipe for crafting this item. If no such recipe exists, returns null.
     *
     * @param rPath relative path of the item
     * @return relative path of the recipe
     */
    public String getRecipe(String rPath) {
        return itemMap.get(rPath).getRecipe();
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
     * @return a list of string arrays
     */
    public List<String[]> getAllNamesAndRPaths() {
        List<String[]> list = new ArrayList<>();
        for (Item item: itemMap.values()) {
            list.add(new String[] {item.getName(), item.getRPath()});
        }
        return list;
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
        item.setRecipe(recipeRPath);
        item.setCraftable(true);
        addMap.put(item.getRPath(), item);
    }

    public void addDecon(String rPath, Map<String, Integer> decons) {
        Item item = itemMap.get(rPath);
        item.setDecons(decons);
        item.setDeconnable(true);
        addMap.put(item.getRPath(), item);
    }

    public void addLootBoxCommon(String rPath, List<String[]> loot) {
        Item item = itemMap.get(rPath);
        if (!item.isLootbox()) {
            item.setLootbox(true);
        }
        for (String[] lootable: loot) {
            item.addLootboxCommon(lootable[0], Integer.parseInt(lootable[1]));
        }
        addMap.put(item.getRPath(), item);
    }

    public void addLootBoxUncommon(String rPath, List<String[]> loot) {
        Item item = itemMap.get(rPath);
        if (!item.isLootbox()) {
            item.setLootbox(true);
        }
        for (String[] lootable: loot) {
            item.addLootboxUncommon(lootable[0], Integer.parseInt(lootable[1]));
        }
        addMap.put(item.getRPath(), item);
    }

    public void addLootBoxRare(String rPath, List<String[]> loot) {
        Item item = itemMap.get(rPath);
        if (!item.isLootbox()) {
            item.setLootbox(true);
        }
        for (String[] lootable: loot) {
            item.addLootboxRare(lootable[0], Integer.parseInt(lootable[1]));
        }
        addMap.put(item.getRPath(), item);
    }

    public void addNotes(String rPath, String notesRPath) {
        Item item = itemMap.get(rPath);
        item.addNotes(notesRPath);
        addMap.put(item.getRPath(), item);
    }
}
