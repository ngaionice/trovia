package datamodel.parser.parsestrategies;

import datamodel.objects.Bench;
import datamodel.parser.Parser;
import datamodel.objects.Article;
import datamodel.parser.Markers;

import java.util.*;

public class ParseProfession implements ParseStrategy {

    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {
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
            Map<String[], List<String>> categories = new HashMap<>(20);
            for (int i = 0; i < categoryList.size(); i++) {
                List<String> currCat = categoryList.get(i);
                currCat.set(0, currCat.get(0).substring(0, currCat.get(0).indexOf("10") - 1)); // cleans up the name

                for (int j = 0; j < currCat.size(); j++) {
                    currCat.set(j, Parser.hexToAscii(currCat.get(j)));
                }

                categories.put(new String[]{currCat.get(0), Integer.toString(i)}, new ArrayList<>(currCat.subList(1, currCat.size())));
            }

            // creating the Bench object
            String path = absPath.substring(absPath.lastIndexOf("\\") + 1, absPath.indexOf(m.endFile));
            String rPath = Parser.extractRPath(absPath);

            return new Bench(path, rPath, categories);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

}
