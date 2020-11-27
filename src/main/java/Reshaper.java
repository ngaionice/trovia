import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reshaper {
    // reshaping the data by comparing strings to names; i.e. making things accessible to normal users

    // recipes - extract the common items from output from convertDirectory
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

    public List<String[]> itemListMerge(List<List<String[]>> items) {
        List<String[]> returnList = new ArrayList<>();
        for (List<String[]> item: items) {
            returnList.addAll(item);
        }
        return returnList;
    }

    // TODO: clean up duplicate code
    public List<String[]> itemMatch(List<String[]> references, List<String> items) {
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
                // exclude collections, as they aren't used in crafting recipes; alternatively, only filter out the ones that are not items
                unmatchedItems.add(item);
            }
        }
        try(FileWriter fwUnmatched = new FileWriter("C:\\Users\\Julian\\Desktop\\parsing\\unmatched.txt", true);
            BufferedWriter bw = new BufferedWriter(fwUnmatched);
            PrintWriter out = new PrintWriter(bw))
        {
            for (String item:unmatchedItems) {
                out.println("No match for "+ item);
                System.out.println("No match for "+ item);
            }
        } catch (IOException e) {
            System.out.println("Logging to unmatched.txt failed.");
        }
//        for (String[] matchedArray: matchedItems) {
//            System.out.println(matchedArray[2]);
//        }
        // TODO: there is probably a better solution for this but brain dead
        List<String[]> duplicates = new ArrayList<>();
        List<String> names =  new ArrayList<>();
        for (String[] matchedArray: matchedItems) {
            if (names.contains(matchedArray[2])) {
                duplicates.add(matchedArray);
            } else {
                names.add(matchedArray[2]);
            }
        }
        try(FileWriter fw = new FileWriter("C:\\Users\\Julian\\Desktop\\parsing\\duplicates.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            for (String[] item:duplicates) {
                out.println(item[0]+ " - " + item[1] +  " - " + item[2]);
                System.out.println(item[0]+ " - " + item[1] +  " - " + item[2]);
            }
        } catch (IOException e) {
            System.out.println("Logging to duplicates.txt failed.");
        }
        return matchedItems;
    }

    public List<String[]> referenceMerge(List<String[]> item1, List<String[]> item2) {
        return Stream.concat(item1.stream(), item2.stream()).collect(Collectors.toList());
    }
}
