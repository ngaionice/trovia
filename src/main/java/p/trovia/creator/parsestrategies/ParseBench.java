package p.trovia.creator.parsestrategies;

import p.trovia.creator.Parser;
import p.trovia.objects.Article;
import p.trovia.objects.Bench;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseBench implements ParseStrategy {

    public Article parseObject(String splitString, String absPath) {
        List<List<String>> categoryList = new ArrayList<>();
        String categorySplit = "24 70 72 65 66 61 62 73 "; // $prefab

        // first identify the number of categories; $prefabs show up at least twice for 0 categories; +1 for each additional
        String temp = splitString.replace(categorySplit, "");
        int occ = (splitString.length() - temp.length()) / categorySplit.length();

        // only 1 category
        if (occ == 2) {
            int rangeStart = splitString.indexOf(categorySplit);
            int rangeEnd = splitString.substring(rangeStart + 24).indexOf(categorySplit)+rangeStart;
            String substring = splitString.substring(rangeStart, rangeEnd);
            String dirtyStationName = splitString.substring(rangeEnd);
            if (dirtyStationName.contains("20")) {
                String stationNameUntrimmed = dirtyStationName.substring(0, dirtyStationName.indexOf("20"));
                String stationNameTrimmed = stationNameUntrimmed.substring(stationNameUntrimmed.indexOf("24 70 72"));
                categoryList.add(Collections.singletonList(Parser.hexToAscii(stationNameTrimmed)));
            } else {
                System.out.println("No crafting station name was found for "+absPath+".");
            }
            categoryList.add(parseHelper(substring, absPath));
        }

        // more than 1 category
        else if (occ > 2) {
            String[] substrings = splitString.split(categorySplit);
            String dirtyStationName = substrings[substrings.length-1];
            if (dirtyStationName.contains("20")) {
                String stationName = "24 70 72 65 66 61 62 73 " + dirtyStationName.substring(0, dirtyStationName.indexOf("20"));
                categoryList.add(Collections.singletonList(Parser.hexToAscii(stationName)));
            } else {
                System.out.println("No crafting station name was found for "+absPath+".");
            }
            for (int i = 1; i < substrings.length-1; i++) {
                categoryList.add(parseHelper(substrings[i], absPath));
            }
        }

        // non-standard format, not handled by this function
        else {
            System.out.println("Less than 2 '$prefab's were found for" + absPath + ".");
            return null;
        }

        // creating the Bench object
        String name = categoryList.get(0).get(0);
        Map<String[], List<String>> categories = new HashMap<>(250);
        for (int i = 1; i < categoryList.size(); i++) {

            List<String> currCatHex = categoryList.get(i);
            List<String> currCat = new ArrayList<>();
            for (String catHex : currCatHex) {
                currCat.add(Parser.hexToAscii(catHex));
            }

            String[] categoryName = new String[] {Integer.toString(i), currCat.get(0)};
            List<String> recipes = currCat.subList(1,currCat.size());

            categories.put(categoryName, recipes);
        }
        return new Bench(name, categories);
    }

    /**
     * Helper method for parseObject.
     *
     * @param unparsed substring containing category name and recipe names
     * @return a list of strings, with index 0 being the category name and all other indices being recipe file paths
     */
    private List<String> parseHelper(String unparsed, String path) {
        List<String> recipes = new ArrayList<>();
        String recipeText = "72 65 63 69 70 65 "; // recipe in hex
        String hexAlphabetPrefab = "[6][0-9A-F]|[7][0-9A]|5F|[3][0-9]"; // lowercase letters, underscore and digits

        int nameEnd = unparsed.indexOf("BE "); // end character used to signify end of a category name in files
        if (nameEnd != -1) { // no such string, then something probably went wrong
            String name = "24 70 72 65 66 61 62 73 " + unparsed.substring(0, nameEnd);
            recipes.add(name);
            String rawRecipes = unparsed.substring(nameEnd);
            Pattern re = Pattern.compile(recipeText);
            Matcher m = re.matcher(rawRecipes);
            List<Integer> indices = new ArrayList<>();

            // find all the starting points of recipes
            while (true) {
                if (m.find()) {
                    indices.add(m.start());
                } else {
                    break;
                }
            }

            // extract the recipes
            for (int i = 0; i < indices.size(); i++) {
                if (i+1 < indices.size()) {
                    recipes.add(rawRecipes.substring(indices.get(i), indices.get(i+1)));
                } else {
                    recipes.add(rawRecipes.substring(indices.get(i))); // last recipe
                }
            }

            // clean up the recipes
            for (int i = 1; i < recipes.size(); i++) {
                String processing = recipes.get(i);
                if (processing.length()-6 > 0) {
                    processing = processing.substring(0, processing.length()-6); // removing the guaranteed 2 characters that are useless
                    int lastIndex = 0;
                    for (int j = 0; j < processing.length(); j += 3) {
                        if (processing.substring(j, j+2).matches(hexAlphabetPrefab)) {
                            lastIndex = j + 3;
                        } else {
                            break;
                        }
                    }
                    recipes.set(i, processing.substring(0, lastIndex));
                } else {
                    System.out.println("Recipe clean-up failed at "+path+".");
                }
            }

        } else {
            System.out.println("No end character was identified at " + path + ".");
        }
        return recipes;
    }
}
