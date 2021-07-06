package datamodel.parser;

import datamodel.Enums;
import datamodel.objects.Article;
import datamodel.parser.parsestrategies.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Parser {

    public static String hexToAscii(String hexString) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < hexString.length(); i += 3) {
            String str = hexString.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public static int recipeH2D(String hexNumber, String item) throws ParseException {
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
                try {
                    throw new ParseException("Quantity conversion failed at: " + hexToAscii(item));
                } catch (ClassCastException e) {
                    throw new ParseException("Quantity conversion failed at: " + item);
                }
        }
    }

    public static double collectionH2D(String hexNumber) {
        String[] hexPartition = hexNumber.split(" ");
        int first = Integer.parseInt(hexPartition[0], 16);
        int second = Integer.parseInt(hexPartition[1], 16);
        int denom = 67 - second;

        if (first > 128) {
            return 2 * (128 / Math.pow(4, denom)) + 2 * (first - 128) / Math.pow(4, denom);
        } else {
            return 128 / Math.pow(4, denom) + first / Math.pow(4, denom);
        }
    }

    /**
     * Logs the supplied strings to the text file at the path specified. Requires the text file to exist in order to log.
     *
     * @param itemToLog list of strings to be logged
     * @param path      path of the text file to be logged to
     */
    public static void logToFile(List<String> itemToLog, String path) {
        try (FileWriter fw = new FileWriter(path, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (String item : itemToLog) {
                out.println(item);
//                System.out.println(item);
            }
        } catch (IOException e) {
            System.out.println("Logging to" + path + "failed.");
        }
    }

    public String byteToString(String path) throws IOException {
        byte[] array = Files.readAllBytes(Paths.get(path));
        return javax.xml.bind.DatatypeConverter.printHexBinary(array);
    }

    public String insertSpaces(String hexRaw) {
        return hexRaw.replaceAll("(.{2})", "$1 ").trim();
    }

    public Article createObject(String path, Enums.ObjectType itemType) throws IOException, ParseException {
        String splitString = insertSpaces(byteToString(path));
        ParseContext context;
        System.out.println(itemType.toString());
        switch (itemType) {
            case ITEM:
                context = new ParseContext(new ParseItem());
                return context.parse(splitString, path);
            case PLACEABLE:
                context = new ParseContext(new ParsePlaceable());
                return context.parse(splitString, path);
            case RECIPE:
                context = new ParseContext(new ParseRecipe());
                return context.parse(splitString, path);
            case BENCH:
                context = new ParseContext(new ParseBench());
                return context.parse(splitString, path);
            case PROFESSION:
                context = new ParseContext(new ParseProfession());
                return context.parse(splitString, path);
            case COLLECTION:
                context = new ParseContext(new ParseCollection());
                return context.parse(splitString, path);
            case SKIN:
                context = new ParseContext(new ParseSkin());
                return context.parse(splitString, path);
            case STRING:
                context = new ParseContext(new ParseLangPrefab());
                return context.parse(splitString, path);
            default:
                return null;
        }
    }

}
