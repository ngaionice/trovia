package xyz.trovia.creator;

import xyz.trovia.objects.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParseItem implements ParseStrategy{

    /**
     * Returns a list containing a single item obtained by parsing the input hex string.
     *
     * Hex string has to be from a file in prefab/item.
     *
     * @param splitString a hex string with spaces inserted every 2 characters, which is from a file in prefab/item
     * @return an xyz.trovia.objects.Item object in a list, where the item is formed from the input string
     */
    public List<Item> parse(String splitString, String absPath) {

        // instantiate the stuff needed to parse
        String prefabMarker = "24 70 72 65 66 61 62 73 5F 69 74 65 6D"; // $prefabs_item
        String nameDescBreakMarker = " 68 00 80";
        String collectionMarker = "63 6F 6C 6C 65 63 74 69 6F 6E 73 2F"; // collections/
        String lootboxMarker = "4C 6F 6F 74 54 61 62 6C 65"; // LootTable
        String hexAlphabetPrefab = "[6][0-9A-F]|[7][0-9A]|5F|[3][0-9]"; // for identifying name and desc; lowercase alphabet, digits, underscore
        String hexAlphabetColl = "[6][0-9A-F]|[7][0-9A]|5F|[3][0-9]|2F"; // same as above, + forward slash
        String recPathStartMarker = "item/";
        String recPathEndMarker = ".binfab";

        // instantiate the variables
        String name = null, desc = null, recPath = null;
        String[] unlocks = new String[0];

        // identify name and desc paths
        if (splitString.contains(prefabMarker)) {

            // if there is no name/desc end marker, which it should, then return an empty item
            if (!splitString.contains(" 68 00 80")) {
                System.out.println("Item creation at " + absPath + " failed. Neither name nor description was found.");
                return Collections.singletonList(new Item(absPath));
            }

            // otherwise, parse the string
            int descEnd = splitString.indexOf(nameDescBreakMarker);
            String nameAndDesc = splitString.substring(splitString.indexOf(prefabMarker) + 3, descEnd); // removes the $

            // parse the name
            for (int i = 0; i < nameAndDesc.length(); i += 3) {
                if (!nameAndDesc.substring(i, i+2).matches(hexAlphabetPrefab)) {
                    String nameHex = nameAndDesc.substring(0, i);
                    name = Parser.hexToAscii(nameHex);
                    break;
                }
            }

            // parse the description
            int descStart = nameAndDesc.substring(1).indexOf(prefabMarker);
            descEnd = nameAndDesc.indexOf(nameDescBreakMarker);
            if (descStart != -1) {
                String descHex = nameAndDesc.substring(descStart + 3, descEnd); // removes the $
                desc = Parser.hexToAscii(descHex);
            }

        } else {
            System.out.println("Item creation at " + absPath + " failed.");
            return Collections.singletonList(new Item(absPath));
        }

        // subset recipe path
        int recPathStart = absPath.indexOf(recPathStartMarker);
        int recPathEnd = absPath.indexOf(recPathEndMarker);

        if (recPathStart != -1 && recPathEnd != -1) {
            recPath = absPath.substring(recPathStart, recPathEnd);
        }

        // identify if collection exists
        if (splitString.contains(collectionMarker)) {
            List<String> collection = new ArrayList<>();

            // locate all the markers
            int colIndex = splitString.indexOf(collectionMarker);
            String currString = splitString.substring(colIndex);

            while (currString.contains(collectionMarker)) {

                // parse string
                for (int i = 0; i < currString.length(); i += 3) {
                    if (!currString.substring(i, i+2).matches(hexAlphabetColl)) {
                        collection.add(Parser.hexToAscii(currString.substring(0, i)));
                        currString = currString.substring(colIndex + i); // chop off the previous part
                        break;
                    }
                }
            }
            unlocks = collection.toArray(new String[0]);
        }

        // identify if lootbox exists
        boolean lootbox = false;
        if (splitString.contains(lootboxMarker)) {
            lootbox = true;
        }

        Item item;
        if (name != null && recPath != null) {
            item = new Item(name, desc, unlocks, recPath, lootbox);
        } else {
            System.out.println("Item creation at " + absPath + " failed. Parsing of either name or desc failed.");
            item = new Item(absPath);
        }
        return Collections.singletonList(item);
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
