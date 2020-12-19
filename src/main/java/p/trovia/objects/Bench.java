package p.trovia.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Bench implements Article{

    String name;   // station name; e.g. $prefabs_placeable_crafting_holiday_snowfest_interactive_craftingstation_name
    Map<String[], List<String>> categories; // key: [category #, category path]; value: list of recipe file names
    boolean profession;
    String professionName;

    // e.g.
    // key: [$prefabs_placeable_crafting_crafting_gardening_harvest_interactive_craftingcategory_5_name, 5]
    // value: [recipe_item_mount_ball_mushroom]

    // note that category # is a string here, and that it starts at 1

    public Bench(String name, Map<String[], List<String>> categories) {
        this.name = name;
        this.categories = categories;
    }

    /**
     * Constructor to be used for professions, i.e. the file is from ./professions
     *
     * Profession files don't have the station name in the files, so they have to be added in separately.
     *
     * @param categories categories in the bench
     * @param professionName file name of the profession; e.g. gardening
     */
    public Bench(Map<String[], List<String>> categories, String professionName) {
        this.categories = categories;
        this.profession = true;
        this.professionName = professionName;
    }

    /**
     * Update/insert an existing category specified by category with the value specified by recipes.
     *
     * @param category the category of the recipes to be overwritten/inserted
     * @param recipes  the new recipes' file names
     */
    public void upsertCategory(String[] category, List<String> recipes) {
        categories.put(category, recipes);
    }

    /**
     * Return the path of the name of the bench.
     *
     * @return the path of the name of the bench
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a list of all recipe file names in this bench.
     *
     * @return a list of all recipe file names
     */
    public List<String> getAllRecipes() {
        List<String> recipes = new ArrayList<>();
        for (List<String> list: categories.values()) {
            recipes.addAll(list);
        }
        return recipes;
    }

    /**
     * Returns a list of all recipe file names in the specified category.
     *
     * Returns null if there is no such category.
     *
     * @param category the string of the category, e.g. prefabs_placeable_..._craftingcategory_0_name
     * @return a list of all recipe file names in the category
     */
    public List<String> getCategoryRecipes(String category) {
        for (String[] curr: categories.keySet()) {
            if (curr[1].equals(category)) {
                return categories.get(curr);
            }
        }
        return null;
    }

    public boolean getProfession() {
        return profession;
    }

    public void setName(String name) {
        this.name = name;
    }
}
