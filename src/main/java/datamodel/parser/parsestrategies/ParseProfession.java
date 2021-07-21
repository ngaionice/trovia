package datamodel.parser.parsestrategies;

import datamodel.objects.Bench;
import datamodel.parser.Parser;
import datamodel.objects.Article;
import datamodel.parser.Markers;

import java.util.*;

public class ParseProfession implements ParseStrategy {

    @Override
    public Article parseObject(String splitString, String absPath, boolean useRPath) throws ParseException {
        try {
            Markers m = new Markers();

            // instantiate the necessary variables
            List<List<String>> categoryList = new ArrayList<>();
            ParseHelper helper = new ParseHelper();

            // getting recipes
            String[] substrings = splitString.split(m.crafting);
            for (int i = 1; i < substrings.length - 1; i++) {
                categoryList.add(helper.parseHelper(substrings[i], absPath, m.crafting));
            }

            // cleaning up the category names, and putting the converted stuff into the map
            Map<String, List<String>> categories = new HashMap<>(20);
            Map<String, Integer> order = new HashMap<>();
            for (int i = 0; i < categoryList.size(); i++) {
                List<String> currCat = categoryList.get(i);
                currCat.set(0, currCat.get(0).substring(0, currCat.get(0).indexOf("10") - 1)); // cleans up the name

                for (int j = 0; j < currCat.size(); j++) {
                    currCat.set(j, Parser.hexToAscii(currCat.get(j)));
                }
                String categoryName = currCat.get(0);
                order.put(categoryName, i);
                categories.put(categoryName, new ArrayList<>(currCat.subList(1, currCat.size())));
            }

            // creating the Bench object
            String path = absPath.substring(absPath.lastIndexOf("\\") + 1, absPath.indexOf(m.endFile));
            String rPath = useRPath ? Parser.extractRPath(absPath) : absPath.replace("\\", "/");

            return new Bench(path, rPath, order, categories);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

}
