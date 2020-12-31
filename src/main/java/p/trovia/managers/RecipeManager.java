package p.trovia.managers;

import p.trovia.objects.Recipe;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RecipeManager implements Serializable {

    // use rPath as the key, as it is unique to each Recipe
    Map<String, Recipe> recipeMap = new HashMap<>(5000);

    // used to track changes between local version and online database:
    Map<String, Recipe> addMap = new HashMap<>(1000);
    Map<String, Recipe> removeMap = new HashMap<>(1000);

    // addMap is used to track new recipes and changes to existing recipes
    // removeMap is used to track removed recipes and original versions of recipes (relative to last serialized/synchronization state)

    public void addRecipe(Recipe recipe) {

        // first check if recipe with same key exists; if true, then we add it to removeMap before overwriting it
        if (recipeMap.containsKey(recipe.getRPath())) {

            // if this recipe is getting overwritten/updated a second time or more, we keep the original version
            if (!removeMap.containsKey(recipe.getRPath())) {
                removeMap.put(recipe.getRPath() ,recipeMap.get(recipe.getRPath()));
                // TODO: use a logger to note such an incident
            }
        }

        // add the recipe
        recipeMap.put(recipe.getRPath(), recipe);
        addMap.put(recipe.getRPath(), recipe);
    }

    public void removeRecipe(String rPath) {

        // as in addRecipe, if this item was updated before it gets deleted, we keep the original version in removeMap
        // and discard this updated version completely
        if (!removeMap.containsKey(rPath)) {
            removeMap.put(rPath, recipeMap.get(rPath));
        }

        // remove the recipe
        recipeMap.remove(rPath);
    }

    // getters

    public String getName(String rPath) {
        return recipeMap.get(rPath).getName();
    }

    public String[][] getCosts(String rPath) {
        return recipeMap.get(rPath).getCosts();
    }

    public String[] getOutput(String rPath) {
        return recipeMap.get(rPath).getOutput();
    }

    public String getBench(String rPath) {
        return recipeMap.get(rPath).getBench();
    }

    // setters

    public void setBench(String rPath, String benchRPath) {
        recipeMap.get(rPath).setBench(benchRPath);
    }
}
