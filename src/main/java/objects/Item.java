package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item implements Article, Serializable {

    String name;                // name, e.g. $prefabs_item_aura_music_01_item_name
    String desc;                // desc, e.g. $prefabs_item_aura_music_01_item_description
    String rPath;               // relative path of the item; used in recipes
//    String blueprint;           // path of the blueprint; not currently planned to be used, but may be useful in the future
    boolean isUnlocker;         // if this item is associated to a corresponding collection
    boolean isCraftable;        // if this item is craftable by a recipe
    boolean isLootbox;          // if this item is a lootbox
    boolean isDecon;            // if this item can be deconstructed into other items
    String[] unlocks;           // if isUnlocker = true, list of collection paths that it unlocks; else is an empty array
    Map<String, Integer> decons;            // if canDecon = true, list of Item.name it decons to; else is an empty array
    Map<String, Integer> lootboxCommon;     // if isLootbox = true, key: rPath of item looted, value: quantity of item looted
    Map<String, Integer> lootboxUncommon;
    Map<String, Integer> lootboxRare;
    List<String> recipe = new ArrayList<>();         // if hasRecipe = true, a list of rPaths of recipes, else is an empty list
    List<String> notes;

    // an Entity; basic unit; note that gear is not included here
    public Item(String name, String desc, String[] unlocks, String rPath, boolean isLootbox) {
        this.name = name;
        this.desc = desc;
        this.rPath = rPath;
//        this.blueprint = blueprint;
        this.isLootbox = isLootbox;

        // if unlocks list was length 0 then it doesn't unlock anything
        if (unlocks.length != 0) {
            this.unlocks = unlocks;
            isUnlocker = true;
        } else {
            isUnlocker = false;
        }
    }

    /**
     * For troubleshooting/item exclusion only. Name is the absolute path of the item.
     * Everything else is set to false or empty.
     *
     * @param name path of the un-instantiated item
     */
    public Item(String name) {
        this.name = name;
        desc = rPath = "";
        isUnlocker = isLootbox = false;
        unlocks = null;
        decons = null;
    }

    // terminology:
    // associated recipe: the recipe that crafts this item; recipes that use this item is NOT an associated recipe

    // design choices - setters: could just call the method and auto set to true; however recipes may get removed

    /**
     * Sets hasRecipe to the input value.
     *
     * @param bool true if this item has an associated recipe, i.e. a recipe that crafts this item
     */
    public void setCraftable(boolean bool) {
        this.isCraftable = bool;
    }

    /**
     * Sets isDecon to the input value.
     *
     * @param bool true if this item can be deconstructed
     */
    public void setDeconnable(boolean bool) {
        this.isDecon = bool;
    }

    /**
     * Sets isLootbox to the input value.
     *
     * @param bool true if this item is a lootbox
     */
    public void setLootbox(boolean bool) {
        this.isLootbox = bool;
    }

    /**
     * Sets recipe to the input string.
     *
     * @param recipe the file name of the recipe associated to this item
     */
    public void addRecipe(String recipe) {
        this.recipe.add(recipe);
    }

    public void addLootboxCommon(String rPath, int quantity) {
        lootboxCommon.put(rPath, quantity);
    }

    public void addLootboxUncommon(String rPath, int quantity) {
        lootboxUncommon.put(rPath, quantity);
    }

    public void addLootboxRare(String rPath, int quantity) {
        lootboxRare.put(rPath, quantity);
    }

    public boolean isUnlocker() {
        return isUnlocker;
    }

    public boolean isLootbox() {
        return isLootbox;
    }

    public boolean isDecon() {
        return isDecon;
    }

    public boolean isCraftable() {
        return isCraftable;
    }

    public List<String> getRecipes() {
        if (recipe == null) {
            return null;
        }
        return recipe;
    }

    public Map<String, Map<String, Integer>> getLootbox() {
        Map<String, Map<String, Integer>> contents = new HashMap<>();
        contents.put("common", this.lootboxCommon);
        contents.put("uncommon", this.lootboxUncommon);
        contents.put("rare", this.lootboxRare);
        if (lootboxCommon == null || lootboxUncommon == null || lootboxRare == null) {
            System.out.println("One or more lootbox rarities do not currently have anything logged.");
        }
        return contents;
    }

    public String[] getUnlocks() {
        return unlocks;
    }

    public void setDecons(Map<String, Integer> decons) {
        this.decons = decons;
    }

    public Map<String, Integer> getDecons() {
        return decons;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getRPath() {
        return rPath;
    }

    /**
     * Add notes to this Item entry. Text should not be directly added to this entry; instead, a key-value pair
     * should be entered to a user-generated LangFile entry (tentatively with relative path "languages/en/custom"),
     * and the key used in the LangFile entry should be input here as the parameter.
     *
     * @param key the key mapped to the corresponding string in LangFile with rPath languages/en/custom
     */
    public void addNotes(String key) {
        if (notes == null) {
            notes = new ArrayList<>();
        }
        notes.add(key);
    }
}
