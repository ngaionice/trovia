package datamodel.parser;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import datamodel.Enums;
import datamodel.objects.Article;
import datamodel.parser.parsestrategies.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

    public static String extractRPath(String absPath) throws ParseException {
        String rPath;
        if (absPath.contains("prefabs\\")) {
            rPath = absPath.substring(absPath.indexOf("prefabs\\") + 8, absPath.indexOf(".binfab"));
        } else if (absPath.contains("language")) {
            rPath = absPath.substring(absPath.indexOf("language"), absPath.indexOf(".binfab"));
        } else {
            throw new ParseException(absPath + " could not be used to form a valid relative path.");
        }
        return rPath.replaceAll("\\\\", "/");
    }

    public String byteToString(String path) throws IOException {
        byte[] array = Files.readAllBytes(Paths.get(path));
        return javax.xml.bind.DatatypeConverter.printHexBinary(array);
    }

    public String insertSpaces(String hexRaw) {
        return hexRaw.replaceAll("(.{2})", "$1 ").trim();
    }

    public Article createObject(String path, Enums.ObjectType itemType, boolean useRPath) throws IOException, ParseException {
        String splitString = insertSpaces(byteToString(path));
        ParseContext context;
        switch (itemType) {
            case BENCH:
                context = new ParseContext(new ParseBench());
                return context.parse(splitString, path, useRPath);
            case COLLECTION:
                context = new ParseContext(new ParseCollection());
                return context.parse(splitString, path, useRPath);
            case COLL_INDEX:
                context = new ParseContext(new ParseCollectionIndex());
                return context.parse(splitString, path, useRPath);
            case GEAR_STYLE:
                context = new ParseContext(new ParseGearStyle());
                return context.parse(splitString, path, useRPath);
            case ITEM:
                context = new ParseContext(new ParseItem());
                return context.parse(splitString, path, useRPath);
            case PLACEABLE:
                context = new ParseContext(new ParsePlaceable());
                return context.parse(splitString, path, useRPath);
            case PROFESSION:
                context = new ParseContext(new ParseProfession());
                return context.parse(splitString, path, useRPath);
            case RECIPE:
                context = new ParseContext(new ParseRecipe());
                return context.parse(splitString, path, useRPath);
            case SKIN:
                context = new ParseContext(new ParseSkin());
                return context.parse(splitString, path, useRPath);
            case STRING:
                context = new ParseContext(new ParseLangFile());
                return context.parse(splitString, path, useRPath);
            default:
                return null;
        }
    }

    public Map<String, String> getObjectBlueprintMappingFromDir(String dirPath) throws IOException {
        Map<String, String> map = new HashMap<>();

        String[] fileNames = new String[]{"blocks", "plants", "gardening", "signs", "torches", "trophies"};
        for (String name : fileNames) {
            Map<String, String> result = getOBMappingFromFile(dirPath + "\\" + name + ".binfab", name.equals("plants") || name.equals("gardening"));
            if (result != null) result.forEach(map::put);
        }
        return map;
    }

    public Map<String, String> getOBMappingFromFile(String filePath, boolean useAltPosition) throws IOException {
        Regexes r = new Regexes();
        Matcher m = Pattern.compile(r.bpMappingExtractor).matcher("");

        if (!new File(filePath).exists()) return null;
        Map<String, String> map = new HashMap<>();

        String splitString = insertSpaces(byteToString(filePath));

        String splitter = "80 3F 1E ";
        String[] segments = splitString.split(splitter);

        for (String str : segments) {
            if (m.reset(str).find()) {
                String bp = useAltPosition ? Parser.hexToAscii(m.group(4)) : m.group(5).equals("00 ") ? null : Parser.hexToAscii(m.group(7));
                // basic filter to filter out unwanted junk from gardening.binfab
                if (bp != null && (Character.isLetter(bp.charAt(0)) || Character.isDigit(bp.charAt(0))))
                    map.put(Parser.hexToAscii(m.group(2)), bp);
            }
        }
        return map;
    }
}
