package model.parser.parsestrategies;

import model.parser.Parser;
import model.objects.Article;
import model.objects.Bench;
import local.Markers;

import java.util.*;

public class ParseBench implements ParseStrategy {

    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {
        Markers m = new Markers();
        List<List<String>> categoryList = new ArrayList<>();
        ParseHelper helper = new ParseHelper();

        // first identify the number of categories; $prefabs show up at least twice for 0 categories; +1 for each additional
        String temp = splitString.replace(m.prefab, "");
        int occ = (splitString.length() - temp.length()) / m.prefab.length();

        // only 1 category
        if (occ == 2) {
            int rangeStart = splitString.indexOf(m.prefab);
            int rangeEnd = splitString.substring(rangeStart + 24).indexOf(m.prefab)+rangeStart;

            String substring = splitString.substring(rangeStart, rangeEnd);
            String dirtyStationName = splitString.substring(rangeEnd);

            if (dirtyStationName.contains("20")) {
                String stationNameUntrimmed = dirtyStationName.substring(0, dirtyStationName.indexOf("20"));
                if (!stationNameUntrimmed.contains("24 70 72")) {
                    throw new ParseException("Bench name at " + absPath + " has a non-standard format.");
                }
                String stationNameTrimmed = stationNameUntrimmed.substring(stationNameUntrimmed.indexOf("24 70 72"));
                categoryList.add(Collections.singletonList(Parser.hexToAscii(stationNameTrimmed)));
            } else {
                System.out.println("No crafting station name was found for "+absPath+".");
            }

            categoryList.add(helper.parseHelper(substring, absPath, m.prefab));

        }

        // more than 1 category
        else if (occ > 2) {
            String[] substrings = splitString.split(m.prefab);
            String dirtyStationName = substrings[substrings.length-1];
            if (dirtyStationName.contains("20")) {
                String stationName = m.prefab + dirtyStationName.substring(0, dirtyStationName.indexOf("20"));
                categoryList.add(Collections.singletonList(Parser.hexToAscii(stationName)));
            } else {
                System.out.println("No crafting station name was found for "+absPath+".");
            }
            for (int i = 1; i < substrings.length-1; i++) {
                categoryList.add(helper.parseHelper(substrings[i], absPath, m.prefab));
            }
        }

        // non-standard format, not handled by this function
        else {
            throw new ParseException("Less than 2 '$prefab's were found for" + absPath + ".");
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

            String[] categoryName = new String[] {currCat.get(0), Integer.toString(i)};
            List<String> recipes = new ArrayList<>(currCat.subList(1,currCat.size()));

            categories.put(categoryName, recipes);
        }

        String rPath = absPath.substring(absPath.indexOf("prefabs\\")+8, absPath.indexOf(m.endFile));
        rPath = rPath.replaceAll("\\\\", "/");

        return new Bench(name, rPath, categories);
    }


}
