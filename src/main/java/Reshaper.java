import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Reshaper { // reshaping the data


    /**
     * Returns one list of string arrays by grouping the list of lists of string arrays provided.
     *
     * Used with Parser.convertDirectory, to merge multiple the output of multiple files into one.
     * Note that if includePath was set to true in Parser.convertDirectory, all the file names will be included as well.
     *
     * @param items output of Parser.convertDirectory
     * @return a list of all the string arrays across the list of sub-lists of string arrays from above input.
     */
    public List<String[]> mergeItemList(List<List<String[]>> items) {
        List<String[]> returnList = new ArrayList<>();
        for (List<String[]> item: items) {
            returnList.addAll(item);
        }
        return returnList;
    }

    /**
     * Logs the supplied strings to the text file at the path specified. Requires the text file to exist in order to log.
     *
     * @param itemToLog list of strings to be logged
     * @param path path of the text file to be logged to
     */
    public void logToFile(List<String> itemToLog, String path) {
        try(FileWriter fw = new FileWriter(path, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            for (String item:itemToLog) {
                out.println(item);
//                System.out.println(item);
            }
        } catch (IOException e) {
            System.out.println("Logging to" + path + "failed.");
        }
    }

    /** Takes a list of item arrays containing their associated properties, and converts them into a Map
     * with the item name's path as the key.
     *
     * Does not map incomplete items, they are instead logged to the text file specified by logPath.
     *
     * @param listOfItems list of item arrays, with the arrays following the format [namePath, name, descPath, desc]
     * @param logPath the path to log incomplete items to
     * @return a Map, with the item names as keys, and the original arrays as values
     */
    public Map<String, String[]> mapItems(List<String[]> listOfItems, String logPath) { // also used for collections
        Map<String, String[]> mapOfItems = new HashMap<>(5000);
        // recall each item is stored in the sequence namePath, name, descPath, desc
        // skip incomplete items
        List<String> incompleteItems = new ArrayList<>();
        for (String[] item: listOfItems) {
            if (!Arrays.asList(item).contains("N/A")) { // the placeholder used in splitter
                mapOfItems.put(item[0], item); // sets name path as key
            } else {
                incompleteItems.add(String.join(" - ", item));
            }
        }
        System.out.println(mapOfItems.size());
        logToFile(incompleteItems, logPath);
        return mapOfItems;
    }

    // recipe reference matching

    /**
     * Takes in output of Parser.convertDirectory on a directory with recipe prefabs, and outputs a list of strings,
     * which is the set of all crafting materials used across all recipes.
     *
     * @param directoryOutput output of Parser.convertDirectory on a directory with recipe prefabs, with includePath set to false
     * @return a list of all unique crafting materials and outputs from the recipes
     */
    public List<String> extractUniquePaths(List<List<String[]>> directoryOutput) {
        List<String> uniqueItems = new ArrayList<>();

        // first join every list of strings[] into 1 array, extracting the first item
        for (List<String[]> recipe: directoryOutput) {
            List<String> recipeItems = new ArrayList<>();
            for (String[] item: recipe) {
                recipeItems.add(item[0].replaceAll("/", "_"));
            }
            uniqueItems.addAll(recipeItems);
        }
        uniqueItems = uniqueItems.stream().distinct().collect(Collectors.toList());
//        for (String item: uniqueItems) {
//            System.out.println(item);
//        }
        return uniqueItems;
    }

    /**
     * Takes in a list of string arrays following the format [item name's path, item name, item desc's path, item desc],
     * a list of strings, which are the item paths used in recipes, and a path to log unmatched items to, and
     * returns a list of string arrays of length 3.
     *
     * Index 0 contains the item path used in recipes;
     * index 1 contains the path of the item that is associated with its other properties;
     * index 2 contains the item's name for debugging purposes.
     *
     * @param references a list of string arrays containing prefab item name/desc and their paths
     * @param items a list of unique paths used in recipe listings
     * @param logPath the path to log unmatched items to
     * @return a list of string arrays in format [recipe name path, prefab name path, item name]
     */
    public List<String[]> matchRecipePaths(List<String[]> references, List<String> items, String logPath) {

        // 3 items in array: path name in recipe, path of the prefab's name, prefab's object name
        // realistically speaking, object name isn't necessary, but it's needed for debugging
        List<String[]> matchedItems = new ArrayList<>();
        List<String> unmatchedItems = new ArrayList<>();

        for (String item: items) {
            boolean notMatched = true;
            for (String[] reference: references) {
                if (reference[0].contains(item)) {
                    matchedItems.add(new String[] {item, reference[0], reference[1]});
                    notMatched = false;
                    break;
                }
            }
            if (notMatched && !item.contains("collections")) {
                // exclude collections, as they aren't/shouldn't be used in crafting recipes;
                // alternatively, only filter out the ones that are not items/placeables (not implemented here, but would be a quick fix)
                unmatchedItems.add(item);
            }
        }

        logToFile(unmatchedItems, logPath);
        return matchedItems;
    }

    /**
     * Takes in a list of string arrays in the format of [recipe item path, prefab item path, item name],
     * and returns a map using recipe item path as key, and the original array as value.
     *
     * Identifies any duplicate matches from matchRecipePaths, and logs them to the text file specified at logPath for manual identification.
     *
     * Used with matchRecipePaths.
     *
     * @param matchedItems output of recipePathMatch
     * @param logPath path
     * @return a Map with item names as keys and string arrays containing associated paths and properties as values
     */
    public Map<String, String[]> mapRecipePaths(List<String[]> matchedItems, String logPath){

        // first need to identify any duplicates; this needs to be done by comparing item names, not paths

        // key: item path in recipes; value: [recipe item path, prefab name path, item name]
        Map<String, String[]> itemMap = new HashMap<>(1024);
        List<String[]> dupes =  new ArrayList<>();

        // the matching part
        for (String[] matchedArray: matchedItems) {
            if (itemMap.containsKey(matchedArray[2])) {
                dupes.add(matchedArray); // adds the duplicated item
                if (!dupes.contains(itemMap.get(matchedArray[2]))) { // if the stored item that was duplicated wasn't in dupes, add it in
                    dupes.add(itemMap.get(matchedArray[2]));
                }
            } else {
                itemMap.put(matchedArray[2], matchedArray);
            }
        }

        // remapping the arrays; the item paths are now keys instead of item names
        Map<String, String[]> pathMap = new HashMap<>(itemMap.size()); // this must be equal or smaller than itemMap, as we are removing dupes
        for (String[] item: itemMap.values()) {
            if (!dupes.contains(item)) {
                pathMap.put(item[0], item);
            } else {
                System.out.println(item[2] + "was removed from the Map due to duplicates.");
            }
        }

        // logging the duplicates
        List<String> toLogger = new ArrayList<>();
        for (String[] item: dupes) {
            toLogger.add(item[2]+ " - " + item[0] +  " - " + item[1]);
        }
        logToFile(toLogger, logPath);
        return pathMap;
    }

}
