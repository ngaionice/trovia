package parser.parsestrategies;

import parser.Parser;
import objects.Item;
import objects.Article;
import local.Markers;

import java.util.ArrayList;
import java.util.List;

public class ParseItem implements ParseStrategy {

    /**
     * Returns a list containing a single item obtained by parsing the input hex string.
     *
     * Hex string has to be from a file in prefab/item.
     *
     * @param splitString a hex string with spaces inserted every 2 characters, which is from a file in prefab/item
     * @return an xyz.trovia.objects.Item object in a list, where the item is formed from the input string
     */
    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {

        System.out.println("Parsing " + absPath);

        // instantiate the stuff needed to parse
        Markers m = new Markers();
        String prefabMarker = "24 70 72 65 66 61 62 "; // $prefabs; changed from $prefabs_item for more flexibility
        String recPathStartMarker = "item\\";
        String recPathEndMarker = ".binfab";

        // instantiate the variables
        String name = null, desc = null, rPath = null;
        String[] unlocks = new String[0];

        // identify name and desc paths
        if (splitString.contains(prefabMarker)) {

            // if there is no name/desc end marker, which it should, then return an empty item
            if (!splitString.contains(" 68 00 80")) {
                System.out.println("Item creation at " + absPath + " failed. Neither name nor description was found.");
                throw new ParseException("Item creation at " + absPath + " failed. Neither name nor description was found.");
            }

            // otherwise, parse the string
            int descEnd = splitString.indexOf(m.endNameDesc);
            String nameAndDesc = splitString.substring(splitString.indexOf(prefabMarker), descEnd);

            // parse the name
            for (int i = 0; i < nameAndDesc.length(); i += 3) {
                if (!nameAndDesc.substring(i, i+2).matches(m.alphabetPrefab)) {
                    String nameHex = nameAndDesc.substring(0, i);
                    name = Parser.hexToAscii(nameHex);
                    break;
                }
            }

            // parse the description
            int descStart = nameAndDesc.substring(1).indexOf(prefabMarker); // shift by 1 to avoid finding the same marker
            if (descStart != -1) {
                String descHex = nameAndDesc.substring(1).substring(descStart);
                desc = Parser.hexToAscii(descHex);
            }

        } else {
            System.out.println("Item creation at " + absPath + " failed.");
            throw new ParseException("Item creation at " + absPath + " failed.");
        }

        // subset relative path
        int rPathStart = absPath.indexOf(recPathStartMarker);
        int rPathEnd = absPath.indexOf(recPathEndMarker);

        if (rPathStart != -1 && rPathEnd != -1) {
            rPath = absPath.substring(rPathStart, rPathEnd);
            rPath = rPath.replaceAll("\\\\", "/");
        }

        // identify if collection exists
        if (splitString.contains(m.collection)) {
            List<String> collection = new ArrayList<>();

            // locate all the markers
            int colIndex = splitString.indexOf(m.collection);
            String currString = splitString.substring(colIndex);

            while (currString.contains(m.collection)) {
                int index = currString.indexOf(" 28 00");
                collection.add(Parser.hexToAscii(currString.substring(0, index)));
                System.out.println(Parser.hexToAscii(currString.substring(0, index)));

                // go to next collection
                if (currString.substring(index).contains(m.collection)) {
                    currString = currString.substring(currString.indexOf(m.collection, index));
                } else {
                    break;
                }
            }

            unlocks = collection.toArray(new String[0]);
        }

        // identify if lootbox exists
        boolean lootbox = false;
        if (splitString.contains(m.lootbox)) {
            lootbox = true;
        }

        Item item;
        if (name != null && rPath != null) {
            item = new Item(name, desc, unlocks, rPath, lootbox);
        } else {
            throw new ParseException("Item creation at " + absPath + " failed. Parsing of either name or desc failed.");
        }
        return item;
    }

    // creating a new item:
    // read binary file, insert spaces
    // identify name and description paths, if they exist -> just stop here if does not exist
    // description breaker: 68 00 80

    // identify if they unlock things - optional, note that they can unlock more than 1
    // string to identify: 63 6F 6C 6C 65 63 74 69 6F 6E 73 2F - collections/

    // identify if they are lootboxes
    // string to identify: 4C 6F 6F 74 54 61 62 6C 65 - LootTable

    // identify blueprint, may be useful?
    // string to identify: 1E 4A - need to add offset (+15 is the start of the blueprint)
    // string blueprint ends in: 2E 62 6C 75 65 70 72 69 6E 74 - .blueprint
    // not all files contain blueprints, strangely enough
}
