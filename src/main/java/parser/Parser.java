package parser;

import parser.parsestrategies.*;
import objects.Article;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public static int recipeH2D(String hexNumber, String item) {
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

    public static double collectionH2D(String hexNumber) {
        String[] hexPartition = hexNumber.split(" ");
        int first = Integer.parseInt(hexPartition[0], 16);
        int second = Integer.parseInt(hexPartition[1], 16);
        int denom = 67 - second;

        if (first > 128) {
            return 2*(128/Math.pow(4, denom)) + 2*(first-128)/Math.pow(4, denom);
        } else {
            return 128/Math.pow(4, denom) + first/Math.pow(4, denom);
        }
    }

//    /**
//     * Takes in a directory path, and parses the files in the directory according to the specified prefab type.
//     * <p>
//     * Returns a list containing lists of string arrays output by parsing each file.
//     *
//     * @param path        the path of the directory
//     * @param prefabType  the type of prefab in the directory
//     * @param includeName whether to include the file name or path in the sub-lists; if true, the first String[] contains the file name
//     * @param absPath     whether the file name or path should be included; if true, the absolute path is saved in the first String[]
//     * @return a list containing sub-lists of string arrays, which are the output of each parsed file
//     * @throws Exception if there are critical file properties missing
//     */
//    public List<List<String[]>> convertDirectory(String path, String prefabType, boolean includeName, boolean absPath) throws Exception {
        // TODO: move to a Files.walk approach to map item paths
//
//        File dir = new File(path);
//        File[] directoryListing = dir.listFiles();
//        List<List<String[]>> returnList = new ArrayList<>();
//        if (directoryListing != null) {
//            for (File child : directoryListing) {
//                String childPath = child.getPath();
////                if (!prefabType.equals("recipe")) {
////                    System.out.println(childPath); // prints path
////                }
//                List<String[]> parsedFile = factory(childPath, prefabType);
//                if (includeName) {
//                    String filePath = absPath ? child.getAbsolutePath() : child.getName();
//                    parsedFile.add(0, new String[]{filePath});
//                }
//                returnList.add(parsedFile);
//            }
//        }
//        return returnList;
//    }

    public Article createObject(String path, String itemType) throws IOException, ParseException {
        String splitString = insertSpaces(byteToString(path));
        ParseContext context;
        switch (itemType) {
            case "item":
                context = new ParseContext(new ParseItem());
                return context.parse(splitString, path);
            case "recipe":
                context = new ParseContext(new ParseRecipe());
                return context.parse(splitString, path);
            case "bench":
                context = new ParseContext(new ParseBench());
                return context.parse(splitString, path);
            case "profession":
                context = new ParseContext(new ParseProfession());
                return context.parse(splitString, path);
            case "collection":
                context = new ParseContext(new ParseCollection());
                return context.parse(splitString, path);
            case "lang-en-prefab":
                context = new ParseContext(new ParseLangPrefab());
                return context.parse(splitString, path);
            default:
                return null;
        }
    }

    /**
     * Logs the supplied strings to the text file at the path specified. Requires the text file to exist in order to log.
     *
     * @param itemToLog list of strings to be logged
     * @param path path of the text file to be logged to
     */
    public static void logToFile(List<String> itemToLog, String path) {
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
}
