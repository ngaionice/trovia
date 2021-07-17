package datamodel.parser.parsestrategies;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import datamodel.objects.Article;
import datamodel.objects.Placeable;
import datamodel.parser.Parser;
import datamodel.parser.Regexes;

public class ParsePlaceable implements ParseStrategy {

    @Override
    public Article parseObject(String splitString, String absPath, boolean useRPath) throws ParseException {
        try {
            Regexes r = new Regexes();

            String rPath = useRPath ? Parser.extractRPath(absPath) : absPath.replace("\\", "/");

            String name, desc;
            String blueprint = null;

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

            Matcher bm = bp.matcher(splitString);
            if (bm.find()) {
                blueprint = Parser.hexToAscii(bm.group(2)).replace(".blueprint", "");
            }

            String remaining = splitString.substring(ndm.end());

            // check for tradability
            Matcher tm = Pattern.compile(r.tradableExtractor).matcher(remaining);

            boolean tradable;
            if (!tm.find()) {
                throw new ParseException(rPath + ": failed to determine tradability.");
            }
            tradable = tm.group(1).equals("02");

            return new Placeable(name, desc, rPath, blueprint, tradable);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }
}
