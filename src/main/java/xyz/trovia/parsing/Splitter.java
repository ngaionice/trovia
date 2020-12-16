package xyz.trovia.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Splitter {

    String hexAlphabetBase = "[6][0-9A-F]|[7][0-9A]|2F"; // contains slash
    String hexAlphabetExtended = "0A|[4][1-9A-F]|[5][0-9A]|[6][0-9A-F]|[7][0-9A]|2[017CE]|3[AF]|5[CF]";
    // period, space, newline, exclaim/question marks, backslash, underscore, maybe some more that i forgot
    String hexAlphabetPrefab = "[6][0-9A-F]|[7][0-9A]|5F|[3][0-9]"; // lowercase letters, underscore and digits
    Pattern p = Pattern.compile(hexAlphabetPrefab);

    /**
     * Takes in a hex string with spaces inserted by Parser.insertSpaces, then returns a list of string arrays,
     * with each array in the format of [item name path, item quantity]
     *
     * @param hexString a hex string with spaces every 2 characters
     * @return a list of string arrays, each array has the format of [item name path, item quantity]
     */
    public List<String[]> splitRecipe(String hexString) {
        String itemIdentifier = " 28 00 AE 03 00 01 18 00 28 00 1E 40 00 1E ";
        String[] firstParse = hexString.split(itemIdentifier);
        List<String> processing = Arrays.asList(firstParse);
        Pattern p = Pattern.compile(hexAlphabetBase);
        List<String[]> parsedList = new ArrayList<>();
        int lastIndex = processing.get(processing.size()-1).contains("2F") ? processing.size() : processing.size()-1;
        for (String item: processing.subList(0, lastIndex)) {
            Matcher m = p.matcher(item);
            if (m.find()) {
                int position = m.start();
                String[] currItem = item.substring(position).split(" 10 ");
                if (currItem.length == 1) {
                    currItem = new String[]{currItem[0], "00"};
                }
                parsedList.add(currItem);
            }
        }
        String lastString = parsedList.get(lastIndex-1)[0];
        String[] lastStringArray = lastString.split(" ");
        int lastValidCharacter = 0;
        for (String hex: lastStringArray) {
            if (hex.matches(hexAlphabetBase)) {
                lastValidCharacter++;
            } else {
                break;
            }
        }
        StringBuilder newLastString = new StringBuilder();
        for (int i = 0; i < lastValidCharacter; i++) {
            newLastString.append(lastStringArray[i]);
            newLastString.append(" ");
        }
        parsedList.get(lastIndex-1)[0] = newLastString.toString();
        return parsedList;
    }

    /**
     * Takes in a hex string with spaces inserted, and returns a string array containing an item's name and description,
     * as well as their corresponding paths.
     *
     * If name || description was not found for an object, inserts N/A into name/description path (in hex),
     * along with a message stating that no name/description was found for that item.
     *
     * @param hexString a hex string (either belonging to item or collection) with spaces inserted every 2 characters
     * @return a string array in the format [path of name, name, path of description, description]
     */
    public List<String[]> splitItemGeneralized(String hexString) {
        String itemIdentifier = " 24 70 72 65 66 61 62 73 "; // $prefabs
        String subHexString = hexString.substring(hexString.indexOf("24 70")+24);
        String[] firstParse = subHexString.split(itemIdentifier);
        // each item is stored in 2 indices; name and description
        List<String[]> itemList = new ArrayList<>();
        Pattern p = Pattern.compile(hexAlphabetExtended);
        String noName = "4E 6F 20 6E 61 6D 65 20 61 73 73 6F 63 69 61 74 65 64 2E";
        String noDesc = "4E 6F 20 64 65 73 63 20 61 73 73 6F 63 69 61 74 65 64 2E";
        String noPath = "4E 2F 41";
        String description = "64 65 73 63";
        String name = "6E 61 6D 65";
        for (int i = 0; i < firstParse.length; i++) {

            // case 1: both are available
            if (firstParse[i].contains(name) && i+1 < firstParse.length && firstParse[i+1].contains(description)) {
                String[] itemNameList = firstParse[i].split(" 18 \\w\\w " );
                String[] itemDescList = firstParse[i+1].split(" 18 \\w\\w ");
                String itemNamePath = "70 72 65 66 61 62 73 "+itemNameList[0];
                String itemDescPath = "70 72 65 66 61 62 73 "+itemDescList[0];
                Matcher m1 = p.matcher(itemNameList[1]);
                Matcher m2 = p.matcher(itemDescList[1]);
                // last 2 conditions check if a blank name/description was provided, which would crash the program if not caught
                if (m1.find() && m2.find() && !itemNameList[1].substring(0,2).contains("BE") && !itemDescList[1].substring(0,2).contains("BE")) {
                    String itemName = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
                    String itemDesc = itemDescList[1].substring(m2.start(), itemDescList[1].indexOf(" BE"));
                    itemList.add(new String[] {itemNamePath, itemName, itemDescPath, itemDesc});
                    i++;
                } else {
                    System.out.println("Something is problematic");
                    break;
                }
            }

            // case 2: only name
            else if (firstParse[i].contains(name) && (i+1 >= firstParse.length || !firstParse[i+1].contains(description))) {
                String[] itemNameList = firstParse[i].split(" 18 \\w\\w " );
                String itemNamePath = "70 72 65 66 61 62 73 "+itemNameList[0];
                Matcher m1 = p.matcher(itemNameList[1]);
                if (m1.find()) {
                    String itemName = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
                    itemList.add(new String[] {itemNamePath, itemName, noPath, noDesc});
                } else {
                    System.out.println("Something went wrong while matching name.");
                    break;
                }
            }

            // else: only description is available
            else {
                String[] itemDescList = firstParse[i].split(" 18 \\w\\w ");
                String itemDescPath = "70 72 65 66 61 62 73 "+itemDescList[0];
                Matcher m2 = p.matcher(itemDescList[1]);
                if (m2.find()) {
                    String itemDesc = itemDescList[1].substring(m2.start(), itemDescList[1].indexOf(" BE"));
                    itemList.add(new String[] {noPath, noName, itemDescPath, itemDesc});
                } else {
                    System.out.println("Something went wrong while matching desc.");
                    break;
                }
            }
        }
        return itemList;
    }

    /**
     * Takes in a hex string from placeable/crafting that contains the suffix _interactive in the file name,
     * and with spaces inserted by Parser.spaceInserter
     *
     * @param hexString the string to be parsed; contains everything in the file
     * @param path the path of the file, for logging purposes
     * @return a list of string arrays, with the first array containing only the name of the crafting station,
     * and all subsequent arrays following the format [category name, recipes...],
     */
    public List<String[]> splitBenchRecipes(String hexString, String path) throws Exception {
        List<String[]> itemList = new ArrayList<>();
        String categorySplit = "24 70 72 65 66 61 62 73 "; // $prefab in hex

        // first identify the number of categories; $prefabs show up at least twice for 0 categories; +1 for each additional
        String temp = hexString.replace(categorySplit, "");
        int occ = (hexString.length() - temp.length()) / categorySplit.length();

        if (occ == 2) {
            int rangeStart = hexString.indexOf(categorySplit);
            int rangeEnd = hexString.substring(rangeStart + 24).indexOf(categorySplit)+rangeStart;
            String substring = hexString.substring(rangeStart, rangeEnd);
            String dirtyStationName = hexString.substring(rangeEnd);
            if (dirtyStationName.contains("20")) {
                String stationName = dirtyStationName.substring(0, dirtyStationName.indexOf("20"));
                itemList.add(new String[] {stationName});
            } else {
                throw new Exception("No crafting station name was found for "+path+".");
            }
            itemList.add(splitBenchRecipesHelper(substring, path));
        } else if (occ > 2) {
            String[] substrings = hexString.split(categorySplit);
            String dirtyStationName = substrings[substrings.length-1];
            if (dirtyStationName.contains("20")) {
                String stationName = "24 70 72 65 66 61 62 73 " + dirtyStationName.substring(0, dirtyStationName.indexOf("20"));
                itemList.add(new String[] {stationName});
            } else {
                throw new Exception("No crafting station name was found for "+path+".");
            }
            for (int i = 1; i < substrings.length-1; i++) {
                itemList.add(splitBenchRecipesHelper(substrings[i], path));
            }
        } else {
            throw new Exception("Less than 2 '$prefab's were found for" + path + ".");
        }
        return itemList;
    }

    /**
     * Helper method for recipeBenchSplitter.
     *
     * @param unparsed substring containing category name and recipe names
     * @return a String array with string[0] being the category name and all other indices containing recipe file paths
     */
    private String[] splitBenchRecipesHelper(String unparsed, String path) {
        List<String> recipes = new ArrayList<>();
        String recipeText = "72 65 63 69 70 65 "; // recipe in hex

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
        return recipes.toArray(new String[0]);
    }


    public List<String[]> troubleshootSplitItem(String hexString) {
        String itemIdentifier = " 24 70 72 65 66 61 62 73 ";
        String subHexString = hexString.substring(hexString.indexOf("24 70"));
        String[] firstParse = subHexString.split(itemIdentifier);
//        System.out.println(firstParse.length); // used for troubleshooting
        List<String[]> itemList = new ArrayList<>();
        Pattern p = Pattern.compile(hexAlphabetExtended);
        for (String s : firstParse) {
            String[] itemNameList = s.split(" 18 \\w\\w ");
            String itemNamePath = "70 72 65 66 61 62 73 " + itemNameList[0];
            Matcher m1 = p.matcher(itemNameList[1]);
            if (m1.find()) {
                String itemName = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
                itemList.add(new String[]{itemNamePath, itemName, "4E 2F 41", "4E 2F 41"});
            } else {
                System.out.println("Something is problematic, check" + hexString);
                break;
            }
        }
        return itemList;
    }

//    public List<String[]> itemSplitter(String hexString) { // old, redundant now
//        String itemIdentifier = " 24 70 72 65 66 61 62 73 ";
//        String subHexString = hexString.substring(hexString.indexOf("24"));
//        String[] firstParse = subHexString.split(itemIdentifier);
//        System.out.println(firstParse.length);
//        // each item is stored in 2 indices; name and description
//        List<String[]> itemList = new ArrayList<>();
//        Pattern p = Pattern.compile(hexAlphabetExtended);
//        for (int i = 0; i < firstParse.length; i+=2) {
//            // special daily token case
//            if (firstParse[i].contains("5F 69 74 65 6D 5F 63 72 61 66 74 69 6E 67 5F 64 61 69 6C 79 63 6F 69 6E 5F 6A 75 6E 65 5F 69 74 65 6D 5F 6E 61 6D 65")) {
//                String[] itemDescList = firstParse[i+19].split(" 18 \\w\\w ");
//                Matcher m2 = p.matcher(itemDescList[1]);
//                String itemDesc;
//                if (m2.find()) {
//                    itemDesc = itemDescList[1].substring(m2.start(), itemDescList[1].indexOf(" BE"));
//                } else {
//                    itemDesc = "4E 6F 20 64 65 73 63 20 61 73 73 6F 63 69 61 74 65 64 2E"; // No desc associated.
//                }
//                String itemDescPath = "70 72 65 66 61 62 73 "+itemDescList[0];
//                for (String item:Arrays.copyOfRange(firstParse, i, i+19)) {
//                    String[] itemNameList = item.split(" 18 \\w\\w " );
//                    String itemNamePath = itemNameList[0];
//                    Matcher m1 = p.matcher(itemNameList[1]);
//                    String itemName;
//                    if (m1.find()) {
//                        itemName = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
//                    } else {
//                        itemName = "4E 6F 20 6E 61 6D 65 20 61 73 73 6F 63 69 61 74 65 64 2E"; // No name associated.
//                    }
//                    itemList.add(new String[] {itemNamePath, itemName, itemDescPath, itemDesc});
//                }
//                i+=18;
//                continue;
//            }
//            // special double description dragon coin case
//            if (firstParse[i].contains("63 6F 6E 74 72 6F 6C 6C 65 72")) {
//                i -= 1;
//                continue;
//            }
//            // special avarem item case
//            if (firstParse[i].contains("63 72 61 66 74 69 6E 67 5F 61 76 61 72 65 6D")) {
//                i += 6;
//                // this also skips a random unrelated titan soul description that goes with these items.
//                continue;
//            }
//            String[] itemNameList = firstParse[i].split(" 18 \\w\\w " );
//            String[] itemDescList = firstParse[i+1].split(" 18 \\w\\w ");
//            String itemNamePath = "70 72 65 66 61 62 73 "+itemNameList[0];
//            String itemDescPath = "70 72 65 66 61 62 73 "+itemDescList[0];
//            Matcher m1 = p.matcher(itemNameList[1]);
//            Matcher m2 = p.matcher(itemDescList[1]);
//            if (m1.find() && m2.find()) {
//                String itemName = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
//                String itemDesc = itemDescList[1].substring(m2.start(), itemDescList[1].indexOf(" BE"));
//                itemList.add(new String[] {itemNamePath, itemName, itemDescPath, itemDesc});
//            } else {
//                System.out.println("Something is problematic");
//                break;
//            }
//        }
//        return itemList;
//    }

    //    // new approach; match files to path instead of attempting to form items myself; match the natural file structure
//    public List<String[]> prefabMatch(String hexString, String path) {
//        String itemIdentifier = " 24 70 72 65 66 61 62 73 "; // $prefabs
//        String subHexString = hexString.substring(hexString.indexOf("24 70")+24);
//        String[] firstParse = subHexString.split(itemIdentifier);
//        if (firstParse.length == 1) {
//            parseFail(path);
//            return new ArrayList<>();
//        }
//        // each item is stored in 2 indices; path and name/description
//        List<String[]> itemList = new ArrayList<>();
//        Pattern p = Pattern.compile(hexAlphabetExtended);
//        Parser parser = new Parser();
//        String noData = "4E 6F 20 64 61 74 61 20 61 73 73 6F 63 69 61 74 65 64 2E";
//        String description = "64 65 73 63";
//        String name = "6E 61 6D 65";
//        for (int i = 0; i < firstParse.length; i++) {
//            if (firstParse[i].contains(name) || firstParse[i].contains(description)) {
//                String[] item = firstParse[i].split(" 18 \\w\\w " );
//                String itemPath = "70 72 65 66 61 62 73 "+item[0];
//                Matcher m1 = p.matcher(item[1]);
//                if (m1.find() && !item[1].substring(0,2).contains("BE")) {
//                    String itemText = item[1].substring(m1.start(), item[1].indexOf(" BE"));
//                    itemList.add(new String[] {itemPath, itemText});
//                    i++;
//                }
//            } else {
//                parseFail(path);
//            }
//        }
//        return itemList;
//    }

//    private void parseFail(String path) {
//        try {
//            Path writePath = Paths.get("C:\\Users\\Julian\\Desktop\\parsing\\logged.txt");
//            Files.write(writePath, Arrays.asList(path + "was not added to the list"), StandardCharsets.UTF_8,
//                    Files.exists(writePath) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
//        } catch (IOException e) {
//            System.out.println("Could not write to file.");
//        }
//    }
}
