import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
     * returns list of sub-lists containing string arrays; each sublist has the following format:
     * [0]: [paths of the name and description]
     * [1]: guide to the rest of the list, size 50, only index 0 of this array is occupied at output
     * [2]: [item name and description in english]
     *
     * @param dirPath path of the directory to be processed
     * @param logPath path of the text file used for logging incomplete items
     * @return a Map with item name's path as key, and [namePath, name, descPath, desc] as value.
     * @throws IOException if the specified text file does not exist.
     */
    public List<List<String[]>> createItems(String dirPath, String logPath) throws Exception {

        // parse the directory and reshape the items to create a map of items; where the key is the item name's path
        List<List<String[]>> parserOutput = parser.convertDirectory(dirPath, "item", false);
        List<String[]> itemsReshaped = reshaper.mergeItemList(parserOutput);
        Map<String, String[]> mappedItems = reshaper.mapItems(itemsReshaped, logPath);

        // create the list of items
        List<List<String[]>> itemList = new ArrayList<>();
        for (String[] item: mappedItems.values()) {
            List<String[]> itemParts = new ArrayList<>();

            // first String[] contains paths of name + desc
            // second String[] is the 'guide', indicates what the other indices contain as it is not fixed yet;
            // setting second String[] size to 10 should cover most things before migration to MongoDB
            itemParts.add(0, new String[]{item[0], item[2]});
            String[] properties = new String[10];
            properties[0] = "English name + desc";
            itemParts.add(1, properties);
            itemParts.add(2, new String[] {item[1], item[3]});

            itemList.add(itemParts);
        }
        return itemList;
    }

    /**
     * Returns a list of sub-lists of string arrays, with each sub-list being a recipe ready to be imported into the database.
     *
     * @param dirPath the path of the directory containing the recipe prefabs
     * @return a list of sub-lists containing string arrays; each sublist has the format
     * list[0] = [recipe file name], list[1] = [file name of the associated bench], list[2+] = [item path (recipe format), item quantity]
     * @throws Exception don't remember, need to do more debugging
     */
    public List<List<String[]>> createRecipes(String dirPath) throws Exception {

        // each sub-list contains [file name] in index 0, then the rest are all [item path, item quant]
        List<List<String[]>> recipeList = parser.convertDirectory(dirPath, "recipe", true);

        // insert blank crafting bench location, as that is currently not available
        String[] blankPath = new String[1];
        for (List<String[]> recipe : recipeList) {
            recipe.add(1, blankPath);
        }
        return recipeList;
    }
}
