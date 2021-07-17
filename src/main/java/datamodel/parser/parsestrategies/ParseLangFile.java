package datamodel.parser.parsestrategies;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import datamodel.objects.Article;
import datamodel.objects.LangFile;
import datamodel.parser.Markers;
import datamodel.parser.Outliers;
import datamodel.parser.Parser;
import datamodel.parser.Regexes;

import java.util.HashMap;
import java.util.Map;

public class ParseLangFile implements ParseStrategy {
    @Override
    public Article parseObject(String splitString, String absPath, boolean useRPath) throws ParseException {
        try {
            Regexes r = new Regexes();
            Pattern p = Pattern.compile(r.langSplitter);
            Pattern ep = Pattern.compile(r.langExtractor);
            Matcher m = p.matcher(splitString);
            Markers mk = new Markers();
            Outliers o = new Outliers();

            String name = absPath.substring(absPath.lastIndexOf("\\") + 1, absPath.indexOf(mk.endFile));
            String rPath = useRPath ? Parser.extractRPath(absPath) : absPath.replace("\\", "/");

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
                        throw new ParseException(rPath + " has an unrecognized invalid string pattern: " + currString);
                    }
                }
                key = Parser.hexToAscii(em.group(2));
                if (!Character.isLetter(key.charAt(1)) && !Character.isDigit(key.charAt(1))) key = key.substring(1);
                strings.put(key, em.group(4).equals("00 ") ? null : Parser.hexToAscii(em.group(7)));
                currStartIndex = m.start() + 18;
            }

            return new LangFile(name, rPath, strings);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }
}
