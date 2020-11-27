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
     * @param refPaths an array of directory paths for the references
     * @param recipeDirPath the path of the recipe directory
     * @return a map with the item name as the key, and their associated paths
     * @throws IOException if the specified text file does not exist.
     */
    public Map<String, String[]> recipePathMatch(String[] refPaths, String recipeDirPath, String dupLogPath, String unmatchedLogPath) throws IOException {
        List<List<String[]>> recipesParsed = parser.convertDirectory(recipeDirPath, "recipe");
        List<String> recipeUniquePaths = reshaper.extractUniquePaths(recipesParsed);
        List<String[]> references = new ArrayList<>();
        for (String directory: refPaths) {
            List<List<String[]>> itemList = parser.convertDirectory(directory, "item");
            List<String[]> items = reshaper.itemListMerge(itemList);
            references = Stream.concat(references.stream(), items.stream()).collect(Collectors.toList());
        }
        List<String[]> matchedPaths = reshaper.recipePathMatch(references,recipeUniquePaths, unmatchedLogPath);
        return reshaper.recipePathFormat(matchedPaths, dupLogPath);
    }

    /**
     * Takes in the path of a directory of items/collections/possibly other things, and the path to log incomplete items to,
     * and returns a Map with item name as key, and [namePath, name, descPath, desc] as value.
     *
     * @param dirPath path of the directory to be processed
     * @param logPath path of the text file used for logging incomplete items
     * @return a Map with item name as key, and [namePath, name, descPath, desc] as value.
     * @throws IOException if the specified text file does not exist.
     */
    public Map<String, String[]> itemProcess(String dirPath, String logPath) throws IOException {
        List<List<String[]>> parserOutput = parser.convertDirectory(dirPath, "item");
        List<String[]> itemsReshaped = reshaper.itemListMerge(parserOutput);
        return reshaper.itemEntryFormat(itemsReshaped, logPath);
    }
}
