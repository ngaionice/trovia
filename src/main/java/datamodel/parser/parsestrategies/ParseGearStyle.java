package datamodel.parser.parsestrategies;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import datamodel.objects.Article;
import datamodel.objects.GearStyleType;
import datamodel.parser.Parser;
import datamodel.parser.Regexes;

import java.util.HashMap;
import java.util.Map;

public class ParseGearStyle implements ParseStrategy{
    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {
        Regexes r = new Regexes();

        String rPath = Parser.extractRPath(absPath);
        String type = rPath.substring(rPath.lastIndexOf("/") + 1).replace("weapon_", "");

        Matcher cm = Pattern.compile(r.gearStyleCatExtractor).matcher(splitString);
        Matcher em = Pattern.compile(r.gearStyleInfoExtractor).matcher("");

        Map<String, String> categories = new HashMap<>();
        Map<String, Map<String, String[]>> styles = new HashMap<>();

        while (cm.find()) {
            categories.put(Parser.hexToAscii(cm.group(1)), cm.group(3));
        }

        categories.forEach((k,v) -> {
            em.reset(v);
            Map<String, String[]> currStyles = new HashMap<>();
            while (em.find()) {
                String blueprint = Parser.hexToAscii(em.group(1));
                String name = em.group(2).equals("00 ") ? null : Parser.hexToAscii(em.group(3));
                String desc = em.group(4).equals("00 ") ? null : Parser.hexToAscii(em.group(5));
                String info = em.group(6).equals("00 ") ? null : Parser.hexToAscii(em.group(7));
                currStyles.put(blueprint, new String[]{name, desc, info});
            }
            styles.put(k, currStyles);
        });

        if (styles.isEmpty()) {
            throw new ParseException(rPath + ": no styles extracted. This is an issue if the files used are correct.");
        }

        return new GearStyleType(rPath, type, styles);
    }
}
