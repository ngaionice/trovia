package p.trovia.creator.parsestrategies;

import p.trovia.creator.Parser;
import p.trovia.objects.Article;
import p.trovia.objects.Bench;

import java.util.*;

public class ParseProfession implements ParseStrategy{

    @Override
    public Article parseObject(String splitString, String absPath) {

        // instantiate the necessary variables
        List<List<String>> categoryList = new ArrayList<>();
        ParseHelper helper =  new ParseHelper();
        String catMarker = "24 43 72 61 66 74 69 6E 67 "; // $Crafting

        // getting recipes
        String[] substrings = splitString.split(catMarker);
        for (int i = 1; i < substrings.length-1; i++) {
            categoryList.add(helper.parseHelper(substrings[i], absPath, "24 43 72 61 66 74 69 6E 67 "));
        }

        // cleaning up the category names, and putting the converted stuff into the map
        Map<String[], List<String>> categories = new HashMap<>(20);
        for (int i = 0; i < categoryList.size(); i++) {
            List<String> currCat = categoryList.get(i);
            currCat.set(0,currCat.get(0).substring(0, currCat.get(0).indexOf("10")-1)); // cleans up the name

            for (int j = 0; j <currCat.size(); j++) {
                currCat.set(j, Parser.hexToAscii(currCat.get(j)));
            }

            categories.put(new String[] {currCat.get(0), Integer.toString(i)}, currCat.subList(1, currCat.size()));
        }

        // creating the Bench object
        String path = absPath.substring(absPath.lastIndexOf("\\")+1, absPath.indexOf(".binfab"));

        return new Bench(categories, path);
    }
}
