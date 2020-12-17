package xyz.trovia.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bench implements Article{

    String name;   // station name
    Map<String[], List<String>> categories; // key: [category #, category path]; value: list of recipe file names
    // note that category # is a string here, and that it starts at 1

    public Bench(String name, Map<String[], List<String>> categories) {
        this.name = name;
        this.categories = categories;
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
}
