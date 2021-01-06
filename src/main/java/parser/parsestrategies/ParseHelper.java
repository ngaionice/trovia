package parser.parsestrategies;

import local.Markers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseHelper {

    /**
     * Helper method for parseObject.
     *
     * @param unparsed substring containing category name and recipe names
     * @return a list of strings, with index 0 being the category name and all other indices being recipe file paths
     */
    List<String> parseHelper(String unparsed, String path, String catMarker) throws ParseException {
        Markers m = new Markers();
        List<String> recipes = new ArrayList<>();

        int nameEnd = unparsed.indexOf("BE "); // end character used to signify end of a category name in files
        if (nameEnd != -1) { // no such string, then something probably went wrong
            String name = catMarker + unparsed.substring(0, nameEnd);
            recipes.add(name);
            String rawRecipes = unparsed.substring(nameEnd);
            Pattern re = Pattern.compile(m.recipe);
            Matcher matcher = re.matcher(rawRecipes);
            List<Integer> indices = new ArrayList<>();

            // find all the starting points of recipes
            while (true) {
                if (matcher.find()) {
                    int index = matcher.start();
                    if (!rawRecipes.substring(index-3, index-1).matches(m.alphabetPrefab)) { // ignore recipes that unlock recipes
                        indices.add(index);
                    }
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
                        if (processing.substring(j, j+2).matches(m.alphabetPrefab)) {
                            lastIndex = j + 3;
                        } else {
                            break;
                        }
                    }
                    recipes.set(i, processing.substring(0, lastIndex));
                } else {
                    throw new ParseException("Recipe clean-up failed at "+path+".");
                }
            }

        } else {
            throw new ParseException("No end character was identified at " + path + ".");
        }
        return recipes;
    }

}
