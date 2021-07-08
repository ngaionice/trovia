package datamodel.parser.parsestrategies;

import datamodel.objects.Article;
import datamodel.objects.GearStyleType;
import datamodel.parser.Parser;
import datamodel.parser.Regexes;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseGearStyle implements ParseStrategy{
    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {
        Regexes r = new Regexes();

        String rPath = Parser.extractRPath(absPath);
        String type = rPath.substring(rPath.lastIndexOf("/") + 1).replace("weapon_", "");

        Matcher cm = Pattern.compile(r.gearStyleCat).matcher(splitString);
        Matcher em = Pattern.compile(r.gearStyleExtractor).matcher("");

        Map<String, Map<String, String[]>> styles = new HashMap<>();
        String catName;
        Map<String, String[]> currStyles;

        while (cm.find()) {
            catName = Parser.hexToAscii(cm.group(1));
            currStyles = new HashMap<>();
            em.reset(cm.group(3));
            while (em.find()) {
                String blueprint = Parser.hexToAscii(em.group(1));
                String name = em.group(2).equals("00 ") ? null : Parser.hexToAscii(em.group(3));
                String desc = em.group(4).equals("00 ") ? null : Parser.hexToAscii(em.group(5));
                String info = em.group(6).equals("00 ") ? null : Parser.hexToAscii(em.group(6));
                currStyles.put(blueprint, new String[]{name, desc, info});
            }
            styles.put(catName, currStyles);
        }

        if (styles.isEmpty()) {
            throw new ParseException(rPath + ": no styles extracted. This is an issue if the files used are correct.");
        }

        return new GearStyleType(rPath, type, styles);
    }
}
