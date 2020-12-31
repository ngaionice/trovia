package p.trovia.managers;

import p.trovia.objects.Recipe;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RecipeManager implements Manager, Serializable {

    // use rPath as the key, as it is unique to each Recipe
    Map<String, Recipe> recipeMap = new HashMap<>(5000);

    // used to track changes between local version and online database:
    Map<String, Recipe> addMap = new HashMap<>(1000);
    Map<String, Recipe> removeMap = new HashMap<>(1000);

    // addMap is used to track new and edited recipes
    // removeMap is used to track removed recipes (relative to last serialized/synchronization state)

    public void addRecipe(Recipe recipe) {
        recipeMap.put(recipe.getRPath(), recipe);
        addMap.put(recipe.getRPath(), recipe);
    }

    public void removeRecipe(String rPath) {
        removeMap.put(rPath, recipeMap.get(rPath));
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
        addMap.put(rPath, recipeMap.get(rPath));
    }
}
