package datamodel.parser.parsestrategies;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import datamodel.objects.Article;
import datamodel.objects.CollectionIndex;
import datamodel.parser.Parser;
import datamodel.parser.Regexes;

import java.util.HashMap;
import java.util.Map;

public class ParseCollectionIndex implements ParseStrategy{
    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {
        try {
            Regexes r = new Regexes();

            String rPath = Parser.extractRPath(absPath);
            if (!rPath.contains("collection_")) throw new ParseException(rPath + ": cannot extract collection type.");
            String type = rPath.substring(rPath.lastIndexOf("\\") + 1).replace("collection_", "");

            Matcher cm = Pattern.compile(r.collIndexCatExtractor).matcher(splitString);
            Matcher im = Pattern.compile(r.collIndexInfoExtractor).matcher("");

            Map<String, String> categories = new HashMap<>();
            Map<String, String> names = new HashMap<>();
            Map<String, Map<String, String>> entries = new HashMap<>();

            while (cm.find()) {
                String key = Parser.hexToAscii(cm.group(1));
                names.put(key, Parser.hexToAscii(cm.group(2)));
                categories.put(key, cm.group(4));
            }

            categories.forEach((k, v) -> {
                Map<String, String> categoryEntries = new HashMap<>();
                im.reset(v);
                while (im.find()) {
                    String val = im.group(2).equals("00 ") ? null : Parser.hexToAscii(im.group(3));
                    if (val != null && val.contains("CollectableTag")) val = val.substring(0, val.length() - 3);
                    categoryEntries.put(Parser.hexToAscii(im.group(1)), val);
                }
                entries.put(k, categoryEntries);
            });

            return new CollectionIndex(rPath, type, names, entries);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }
}
