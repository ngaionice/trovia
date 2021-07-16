package datamodel.parser.parsestrategies;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import datamodel.objects.Article;
import datamodel.objects.Item;
import datamodel.parser.Markers;
import datamodel.parser.Parser;
import datamodel.parser.Regexes;

import java.util.ArrayList;
import java.util.List;

public class ParseItem implements ParseStrategy {

    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {
        try {
            // instantiate the stuff needed to parse
            Markers m = new Markers();
            Regexes r = new Regexes();

            // instantiate the variables
            String name, desc, rPath;
            String[] unlocks = new String[0];

            // extract relative path
            rPath = Parser.extractRPath(absPath);

            // identify name and desc paths
            Pattern ndp = Pattern.compile(r.nameDescExtractor);
            Pattern bp = Pattern.compile(r.blueprintExtractor);

            int ndEnd = splitString.indexOf("68 00 80");
            if (ndEnd == -1) {
                throw new ParseException(rPath + " did not have an end marker.");
            }

            Matcher ndm = ndp.matcher(splitString.substring(0, ndEnd + 8));
            if (!ndm.find()) {
                throw new ParseException(rPath + " did not match the pattern for name and description; does it satisfy the assumptions?");
            }
            int nLen = Integer.parseInt(ndm.group(1), 16);
            name = Parser.hexToAscii(ndm.group(2).length() <= 3 * nLen ? ndm.group(2) : ndm.group(2).substring(0, 3 * nLen));

            if (ndm.group(5).equals("00 ")) {
                desc = null;
            } else {
                int dLen = Integer.parseInt(ndm.group(6), 16);
                desc = Parser.hexToAscii(ndm.group(7).length() <= 3 * dLen ? ndm.group(7) : ndm.group(7).substring(0, 3 * dLen));
            }

            String remaining = splitString.substring(ndm.end());

            // check for tradability
            Matcher tm = Pattern.compile(r.tradableExtractor).matcher(remaining);

            boolean tradable;
            if (!tm.find()) {
                throw new ParseException(rPath + ": failed to determine tradability.");
            }
            tradable = tm.group(1).equals("02");

            // identify collections unlocked by this item
            if (remaining.contains(m.collection)) {
                List<String> collection = new ArrayList<>();

                // locate all the markers
                int colIndex = remaining.indexOf(m.collection);
                String currString = remaining.substring(colIndex);

                while (currString.contains(m.collection)) {
                    int index = currString.indexOf(" 28 00");
                    collection.add(Parser.hexToAscii(currString.substring(0, index)));

                    // go to next collection
                    if (currString.substring(index).contains(m.collection)) {
                        currString = currString.substring(currString.indexOf(m.collection, index));
                    } else {
                        break;
                    }
                }
                unlocks = collection.toArray(new String[0]);
            }

            // identify blueprint
            String blueprint = null;

            Matcher bm = bp.matcher(splitString);
            if (bm.find()) {
                blueprint = Parser.hexToAscii(bm.group(2)).replace(".blueprint", "");
            }

            // identify if lootbox exists
            boolean lootbox = splitString.contains(m.lootbox);

            // identify if this item has decay
            boolean decay = splitString.contains(m.decay);

            return new Item(name, desc, rPath, unlocks, blueprint, tradable, lootbox, decay);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }
}
