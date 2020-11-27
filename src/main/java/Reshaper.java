import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Reshaper { // reshaping the data


    /**
     * Returns 1 list of string arrays by grouping the list of lists of string arrays provided.
     *
     * Used with Parser.convertDirectory, to merge multiple the output of multiple files into one.
     *
     * @param items output of Parser.convertDirectory
     * @return a list of all the string arrays across the list of lists of string arrays from above input.
     */
    public List<String[]> itemListMerge(List<List<String[]>> items) {
        List<String[]> returnList = new ArrayList<>();
        for (List<String[]> item: items) {
            returnList.addAll(item);
        }
        return returnList;
    }

    /**
     * Logs given strings to the path specified.
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
     * with the item Name as a key.
     *
     * Does not map incomplete items, they are instead logged to the text file specified by logPath.
     *
     * @param listOfItems list of item arrays, with the arrays following the format [namePath, name, descPath, desc]
     * @param logPath the path to log incomplete items to
     * @return a Map, with the item names as keys, and the original arrays as values
     */
    public Map<String, String[]> itemEntryFormat(List<String[]> listOfItems, String logPath) { // also used for collections
        Map<String, String[]> mapOfItems = new HashMap<>(5000);
        // recall each item is stored in the sequence namePath, name, descPath, desc
        // skip incomplete items
        List<String> incompleteItems = new ArrayList<>();
        for (String[] item: listOfItems) {
            if (!Arrays.asList(item).contains("N/A")) { // the placeholder used in splitter
                mapOfItems.put(item[1], item);
            } else {
                incompleteItems.add(String.join(" - ", item));
            }
        }
        System.out.println(mapOfItems.size());
        logToFile(incompleteItems, logPath);
        return mapOfItems;
    }

    // recipe reference matching

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
     * Returns a list of string arrays which contains an item name along with its associated paths in prefabs and recipe listings.
     *
     * Recipe listings which are not matched are logged to the text file specified by logPath.
     *
     * @param references a list of string arrays containing prefab item name/desc and their paths
     * @param items a list of unique paths used in recipe listings
     * @param logPath the path to log unmatched items to
     * @return a list of string arrays in format [prefab name path, recipe name path, item name]
     */
    public List<String[]> recipePathMatch(List<String[]> references, List<String> items, String logPath) {
        // 3 items in array: prefab name, path name in recipe, item name
        List<String[]> matchedItems = new ArrayList<>();
        List<String> unmatchedItems = new ArrayList<>();
        for (String item: items) {
            boolean notMatched = true;
            // possible issues: reference gets matched more than once
            for (String[] reference: references) {
                if (reference[0].contains(item)) {
                    matchedItems.add(new String[] {reference[0], item, reference[1]});
                    notMatched = false;
                    break;
                }
            }
            if (notMatched && !item.contains("collections")) {
                // exclude collections, as they aren't used in crafting recipes; alternatively, only filter out the ones that are not items/placeables
                unmatchedItems.add(item);
            }
        }
        logToFile(unmatchedItems, logPath);
        return matchedItems;
    }

    /**
     * Returns a Map containing item names and their paths; with the names as the key and a string array containing
     * prefab and recipe name paths, as well as item name.
     *
     * Note that if duplicate item names are present, the first occurrence in the list is mapped;
     * all occurrences get logged to the text file specified at logPath.
     *
     * Used with recipePathMatch.
     *
     * @param matchedItems output of recipePathMatch
     * @param logPath path
     * @return a Map with item names as keys and string arrays containing associated paths and properties as values
     */
    public Map<String, String[]> recipePathFormat(List<String[]> matchedItems, String logPath){
        Map<String, String[]> names = new HashMap<>(1024);
        List<String[]> duplicates =  new ArrayList<>();
        for (String[] matchedArray: matchedItems) {
            if (names.containsKey(matchedArray[2])) { // interestingly, paths are never duplicated, only the actual item names, probably overlooking something
                duplicates.add(matchedArray); // adds the duplicated item
                if (!duplicates.contains(names.get(matchedArray[2]))) { // if the stored item that was duplicated wasn't in dups, add it in
                    duplicates.add(names.get(matchedArray[2]));
                }
            } else {
                names.put(matchedArray[2], matchedArray);
            }
        }
        List<String> toLogger = new ArrayList<>();
        for (String[] item: duplicates) {
            toLogger.add(item[1]+ " - " + item[2] +  " - " + item[0]);
        }
        logToFile(toLogger, logPath);
        return names;
    }

}
