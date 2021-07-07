package datamodel.parser.parsestrategies;

import datamodel.objects.Article;
import datamodel.objects.LangFile;
import datamodel.parser.Outliers;
import datamodel.parser.Parser;
import local.Markers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseLangFile implements ParseStrategy{
    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {
        System.out.println("Parsing " + absPath);

        Pattern p = Pattern.compile("BE 03 0[0-9A-F] 0[0-9A-F] 0[0-9A-F] 1E");
        Pattern ep = Pattern.compile("08 ([0-9A-F][0-9A-F] )+?(24 ([0-9A-F][0-9A-F] )+?)18 (00 |([0-9A-F][0-9A-F]) (0[0-9A-F] )?(([0-9A-F][0-9A-F] )+))");
        Matcher m = p.matcher(splitString);
        Markers mk = new Markers();
        Outliers o = new Outliers();

        String name = absPath.substring(absPath.lastIndexOf("\\")+1, absPath.indexOf(mk.endFile));
        String rPath = absPath.substring(absPath.indexOf("language"), absPath.indexOf(mk.endFile));
        rPath = rPath.replaceAll("\\\\", "/");

        Map<String, String> strings = new HashMap<>();

        int currStartIndex = 0;
        String currString;
        Matcher em = ep.matcher("");
        String key;
        while (m.find()) {
            currString = splitString.substring(currStartIndex, m.start());
            em.reset(currString);
            if (!em.find()) {
                if (o.strings.contains(currString)) {
                    em.reset(o.replacements.get(o.strings.indexOf(currString))).find();
                } else {
                    throw new ParseException(absPath + " has an invalid string pattern: " + currString);
                }
            }
            key = Parser.hexToAscii(em.group(2));
            if (!Character.isLetter(key.charAt(1)) && !Character.isDigit(key.charAt(1))) key = key.substring(1);
            strings.put(key, em.group(4).equals("00 ") ? null : Parser.hexToAscii(em.group(7)));
            currStartIndex = m.start() + 18;
        }

        return new LangFile(name, rPath, strings);
    }
}
