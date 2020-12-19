package p.trovia.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item implements Article {

    String name;                // name
    String desc;                // desc
    String recPath;             // relative path of the item; used in recipes
//    String blueprint;           // path of the blueprint; not currently planned to be used, but may be useful in the future
    boolean isUnlocker;         // if this item is associated to a corresponding collection
    boolean isCraftable = false;// if this item is craftable by a recipe
    boolean isLootbox;          // if this item is a lootbox
    boolean isDecon = false;    // if this item can be deconstructed into other items
    String[] unlocks;           // if isUnlocker = true, list of collection paths that it unlocks; else is an empty array
    String[] decons;            // if canDecon = true, list of Item.name it decons to; else is an empty array
    List<String[]> lootboxCommon = new ArrayList<>();     // if isLootbox = true, list of arrays in format [Item.name, quantity]
    List<String[]> lootboxUncommon = new ArrayList<>();
    List<String[]> lootboxRare = new ArrayList<>();
    String recipe = "";         // if hasRecipe = true, file name of the recipe, else is an empty string

    // an Entity; basic unit; note that gear is not included here
    public Item(String name, String desc, String[] unlocks, String recPath, boolean isLootbox) {
        this.name = name;
        this.desc = desc;
        this.recPath = recPath;
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
        desc = recPath = "";
        isUnlocker = isLootbox = false;
        unlocks = decons = new String[0];
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
     * Sets canDecon to the input value.
     *
     * @param bool true if this item can be deconstructed
     */
    public void setDecon(boolean bool) {
        this.isDecon = bool;
    }

    /**
     * Sets recipe to the input string.
     *
     * @param recipe the file name of the recipe associated to this item
     */
    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public void addLootboxCommon(String[] item) {
        lootboxCommon.add(item);
    }

    public void addLootboxUncommon(String[] item) {
        lootboxUncommon.add(item);
    }

    public void addLootboxRare(String[] item) {
        lootboxRare.add(item);
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

    public String getRecipe() {
        return recipe;
    }

    public Map<String, List<String[]>> getLootbox() {
        Map<String, List<String[]>> contents = new HashMap<>();
        contents.put("common", this.lootboxCommon);
        contents.put("uncommon", this.lootboxUncommon);
        contents.put("rare", this.lootboxRare);
        return contents;
    }

    public String[] getUnlocks() {
        return unlocks;
    }

    public String[] getDecons() {
        return decons;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getRecPath() {
        return recPath;
    }

//    public String getBlueprint() {
//        return blueprint;
//    }
}
