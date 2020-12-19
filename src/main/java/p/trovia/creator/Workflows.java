package p.trovia.creator;

import java.util.*;

public class Workflows {
    // workflows that call on the different classes to prepare for data entry into MongoDB
    public Reshaper reshaper = new Reshaper();
    public Parser parser = new Parser();

    /**
     * Adds the missing bench paths in the recipes from createRecipes.
     *
     * Probably a horribly inefficient method.
     *
     * @param recipes output from createRecipes
     * @param benchRecipes output from getBenchRecipes
     * @return list of sub-lists of string arrays, same format as the output of createRecipes
     */
    public List<List<String[]>> addRecipeStation(List<List<String[]>> recipes, List<List<String[]>> benchRecipes, String logPath) {
        List<List<String[]>> outputRecipes = new ArrayList<>();

        // iterating through each bench
        for (List<String[]> bench: benchRecipes) {
            String benchName = bench.get(0)[0];

            // matching each recipe in each bench
            for (String recipeInBench: bench.get(1)) {

                // iterating through the whole list of recipes, the inefficient part
                for (List<String[]> recipe: recipes) {
                    if (recipe.get(0)[0].contains(recipeInBench)) {
                        recipe.get(1)[0] = benchName;
                        outputRecipes.add(recipe);
                        recipes.remove(recipe); // removes the item to speed things up
                        break;
                    }
                }
            }
        }
        // logging the failed ones
        List<String> log = new ArrayList<>();
        for (List<String[]> item: recipes) {
            log.add(item.get(0)[0]);
        }
        reshaper.logToFile(log, logPath);
        return outputRecipes;
    }

    public void writeRecipesToFile(List<List<String[]>> createBaseRecipesOut, String writePath) {
        List<String> items = new ArrayList<>();
        for (List<String[]> item: createBaseRecipesOut) {
            items.add(item.get(0)[0]);
            if (item.size() > 2) {
                for (int i = 2; i < item.size(); i++) {
                    items.add(item.get(i)[0] + " - " + item.get(i)[1]);
                }
            }
            items.add("\n");
        }
        reshaper.logToFile(items, writePath);
    }


}
