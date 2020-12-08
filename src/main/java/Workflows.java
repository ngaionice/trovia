import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Workflows {
    // workflows that call on the different classes to prepare for data entry into MongoDB
    public Reshaper reshaper = new Reshaper();
    public Parser parser = new Parser();

    /**
     * Takes in the path of the recipe directory and the paths of the directories to be referenced against,
     * and associates the unique paths in the recipes to a name from the references;
     * while logging duplicate matches and unmatched items to separate text files.
     *
     * Returns a Map with the item path in recipes as key, and an array [item path in recipes, path of prefab item name, item name] as value.
     *
     * @param refPaths      an array of directory paths for the references
     * @param recipeDirPath the path of the recipe directory
     * @return a map with the item name's path in recipes as the key, and their associated paths
     * @throws IOException if the specified text file does not exist.
     */
    public Map<String, String[]> matchRecipePaths(String[] refPaths, String recipeDirPath, String dupLogPath, String unmatchedLogPath) throws Exception {

        // gets the recipes and find all the unique item paths
        List<List<String[]>> recipesParsed = parser.convertDirectory(recipeDirPath, "recipe", false);
        List<String> recipeUniquePaths = reshaper.extractUniquePaths(recipesParsed);

        // converts all the reference directories, then merging them into 1 list
        List<String[]> references = new ArrayList<>();
        for (String directory : refPaths) {
            List<List<String[]>> itemList = parser.convertDirectory(directory, "item", false);
            List<String[]> items = reshaper.mergeItemList(itemList);
            references = Stream.concat(references.stream(), items.stream()).collect(Collectors.toList());
        }

        // the path matching part
        List<String[]> matchedPaths = reshaper.matchRecipePaths(references, recipeUniquePaths, unmatchedLogPath);
        return reshaper.mapRecipePaths(matchedPaths, dupLogPath);
    }

    /**
     * Takes in the path of a directory of items/collections/possibly other things, and the path of the text file to log incomplete items to;
     * returns list of maps of strings; each map contains:
     * path_name: the prefab path of the item name
     * path_desc: the prefab path of the item description
     * name_en: the English name of the item
     * desc_en: the English description of the item
     *
     * @param dirPath path of the directory to be processed
     * @param logPath path of the text file used for logging incomplete items
     * @return a list of Maps (of size 50) with keys specified in the main description
     * @throws IOException logging gets interrupted
     */
    public List<Map<String, String>> createItems(String dirPath, String logPath) throws Exception {

        // parse the directory and reshape the items to create a map of items; where the key is the item name's path
        List<List<String[]>> parserOutput = parser.convertDirectory(dirPath, "item", false);
        List<String[]> itemsReshaped = reshaper.mergeItemList(parserOutput);
        Map<String, String[]> mappedItems = reshaper.mapItems(itemsReshaped, logPath);

        // create the list of items
        List<Map<String, String>> itemList = new ArrayList<>();
        for (String[] item: mappedItems.values()) {
            Map<String, String> itemParts = new HashMap<>(50);

            itemParts.put("path_name", item[0]);
            itemParts.put("path_desc", item[2]);
            itemParts.put("name_en", item[1]);
            itemParts.put("desc_en", item[3]);

            itemList.add(itemParts);
        }
        return itemList;
    }

    /**
     * Returns a hashmap with recipe file path as keys, and the recipe's other properties in a list of string arrays.
     *
     * Key: recipe file path
     * Value: list of string arrays, with format
     * list[0]: [file name of the associated bench]
     * list[1+]: [item path (recipe format, item quantity]
     *
     * @param dirPath the path of the directory containing the recipe prefabs
     * @return a hashmap with the recipe file paths as key, and its related properties in a list of string arrays as values
     *
     * path of the bench is currently blank
     * @throws Exception don't remember, need to do more debugging
     */
    public List<List<String[]>> createBaseRecipes(String dirPath) throws Exception {

        // each sub-list contains [file name] in index 0, then the rest are all [item path, item quantity]
        List<List<String[]>> recipeList = parser.convertDirectory(dirPath, "recipe", true);

        // insert blank crafting bench location, as that is currently not available
        for (List<String[]> recipe : recipeList) {
            recipe.add(1, new String[1]);
        }
        System.out.println("Recipe creation complete.");
        return recipeList;
    }

    /** TODO: modify this into a helper function for addRecipeStation
     * Returns a list of string arrays. Each string array has the format:
     * [0]: path of the bench name
     * [1+]: paths of recipes of the bench
     *
     * @param dirOutput output from calling Parser.convertDirectory on a directory with prefabs of benches
     *                  with prefabType set to "bench", and includePath set to true
     * @return a list of sub-lists of string arrays, refer to main description for the format
     */
    public List<List<String[]>> getBenchRecipes(List<List<String[]>> dirOutput) {
        List<List<String[]>> benchRecipes = new ArrayList<>();
        for (List<String[]> bench: dirOutput) {
            List<String[]> reformatBench = new ArrayList<>();
            List<String> recipes = new ArrayList<>();

            // add bench path
            reformatBench.add(bench.get(0));

            // add recipes
            for (int i = 1; i < bench.size(); i++) {
                String[] currArray = bench.get(i);
                int arraySize = currArray.length;
                recipes.addAll(Arrays.asList(currArray).subList(1, arraySize));
            }

            // add the array of recipes to this list
            reformatBench.add(recipes.toArray(new String[0]));

            // add this list to the list of lists of arrays
            benchRecipes.add(reformatBench);
        }
        return benchRecipes;
    }

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
