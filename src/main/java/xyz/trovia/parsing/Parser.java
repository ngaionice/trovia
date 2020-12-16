package xyz.trovia.parsing;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {

    public String byteToString(String path) throws IOException {
        byte[] array = Files.readAllBytes(Paths.get(path));
        return javax.xml.bind.DatatypeConverter.printHexBinary(array);
    }

    public String insertSpaces(String hexRaw) {
        String val = "2";
        return hexRaw.replaceAll("(.{" + val + "})", "$1 ").trim();
    }

    public static String hexToAscii(String hexString) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < hexString.length(); i += 3) {
            String str = hexString.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public int hexToDecimal(String hexNumber, String item) {
        String[] hexPartition = hexNumber.split(" ");
        switch (hexPartition.length) {
            case 1:
                int parsing = Integer.parseInt(hexPartition[0], 16);
                if (parsing % 2 == 1) {
                    return (parsing + 1) * -1 / 2;
                } else {
                    return parsing / 2;
                }
            case 2:
                int base2 = Integer.parseInt(hexPartition[0], 16);
                int second2 = (Integer.parseInt(hexPartition[1], 16) - 1) * 64;
                if (base2 % 2 == 1) {
                    return ((base2 + 1) / 2 + second2) * -1;
                } else {
                    return base2 / 2 + second2;
                }
            case 3:
                int base3 = Integer.parseInt(hexPartition[0], 16);
                int second3 = (Integer.parseInt(hexPartition[1], 16) - 1) * 64;
                int third3 = (Integer.parseInt(hexPartition[2], 16) - 1) * 8192;
                if (base3 % 2 == 1) {
                    return ((base3 + 1) / 2 + second3 + third3) * -1;
                } else {
                    return base3 / 2 + second3 + third3;
                }
            default: // more than 3, rip
                System.out.println(item + " - Keep an eye out for these items.");
                return 0;
        }
    }

    /**
     * Reads the file at the path, and parses it based on the specified prefabType.
     *
     * @param path       path of the file
     * @param prefabType type of the file
     * @return a list of string arrays, format and length varies based on prefabType:
     * "recipe": length 2, format: [item path, quantity]
     * "item": length 4, format: [item name's path, item name, item description's path, item description]
     * "bench" length varies, format varies; first array is length 1, contains only the bench's path name; all subsequent arrays follow the format
     * [path of category name, recipe paths...]
     * @throws Exception if "bench" is selected, and no category name was found
     */
    public List<String[]> factory(String path, String prefabType) throws Exception {
        String baseString = insertSpaces(byteToString(path));
        switch (prefabType) {
            case "recipe":
                return convertRecipe(baseString);
            case "item":
                return convertItem(baseString);
            case "item-testing":
                convertTroubleshooting(baseString);
                break;
            case "bench":
                return convertBench(baseString, path);
        }
        return new ArrayList<>();
    }

    public List<String[]> convertRecipe(String baseString) {
        Splitter splitter = new Splitter();
        List<String[]> listOfMaterials = splitter.splitRecipe(baseString);
        for (String[] item : listOfMaterials) {
            item[0] = hexToAscii(item[0]);
            int quantity = hexToDecimal(item[1], item[0]);
            item[1] = quantity == 0 ? Integer.toString(-1) : Integer.toString(quantity);
            String toPrint = item[0] + " - " + item[1];
//            System.out.println(toPrint); // prints item parsed
        }
        return listOfMaterials;
    }

    public List<String[]> convertItem(String baseString) {
        Splitter splitter = new Splitter();
        List<String[]> itemContainers = splitter.splitItemGeneralized(baseString);
        convertToAscii(itemContainers);
        return itemContainers;
    }

    private void convertToAscii(List<String[]> itemContainers) {
        for (String[] item : itemContainers) {
            item[0] = hexToAscii(item[0]);
            item[1] = hexToAscii(item[1]);
            item[2] = hexToAscii(item[2]);
            item[3] = hexToAscii(item[3]);
            String printName = item[0] + " - " + item[1];
            String printDesc = item[2] + " - " + item[3];
//            System.out.println(printName);
//            System.out.println(printDesc);
        }
    }

    public List<String[]> convertBench(String baseString, String path) throws Exception {
        Splitter splitter = new Splitter();
        List<String[]> recipes = splitter.splitBenchRecipes(baseString, path);
        for (int i = 0; i < recipes.size(); i++) {
            recipes.set(i, Arrays.stream(recipes.get(i)).map(Parser::hexToAscii).toArray(String[]::new));
        }
//        for (String[] item : recipes) {
//            for (String string : item) {
//                System.out.println(string);
//            }
//        }
        return recipes;
    }

    public void convertTroubleshooting(String baseString) {
        Splitter splitter = new Splitter();
        List<String[]> itemContainers = splitter.troubleshootSplitItem(baseString);
        convertToAscii(itemContainers);
    }

    /**
     * Takes in a directory path, and parses the files in the directory according to the specified prefab type.
     * <p>
     * Returns a list containing lists of string arrays output by parsing each file.
     *
     * @param path        the path of the directory
     * @param prefabType  the type of prefab in the directory
     * @param includeName whether to include the file name or path in the sub-lists; if true, the first String[] contains the file name
     * @param absPath     whether the file name or path should be included; if true, the absolute path is saved in the first String[]
     * @return a list containing sub-lists of string arrays, which are the output of each parsed file
     * @throws Exception if there are critical file properties missing
     */
    public List<List<String[]>> convertDirectory(String path, String prefabType, boolean includeName, boolean absPath) throws Exception {
        // TODO: move to a Files.walk approach to map item paths

        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        List<List<String[]>> returnList = new ArrayList<>();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String childPath = child.getPath();
//                if (!prefabType.equals("recipe")) {
//                    System.out.println(childPath); // prints path
//                }
                List<String[]> parsedFile = factory(childPath, prefabType);
                if (includeName) {
                    String filePath = absPath ? child.getAbsolutePath() : child.getName();
                    parsedFile.add(0, new String[]{filePath});
                }
                returnList.add(parsedFile);
            }
        }
        return returnList;
    }
}
