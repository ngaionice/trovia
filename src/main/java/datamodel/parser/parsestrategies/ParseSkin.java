package datamodel.parser.parsestrategies;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import datamodel.objects.Article;
import datamodel.objects.Skin;
import datamodel.parser.Parser;
import datamodel.parser.Regexes;

public class ParseSkin implements ParseStrategy {
    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {
        try {
            String rPath = Parser.extractRPath(absPath);
            Regexes r = new Regexes();
            Pattern p = Pattern.compile(r.skinInfoExtractor);
            Matcher mt = p.matcher(splitString);
            if (!mt.find()) {
                throw new ParseException(rPath + ": invalid skin structure; if this is not a bomb skin, then this is an issue.");
            }
            int bpLen;
            int nLen = -1;

            String bpExcess = null;
            String nExcess = null;

            try {
                bpLen = Integer.parseInt(mt.group(4), 16);
            } catch (NumberFormatException e) {
                throw new ParseException(rPath + ": failed to extract blueprint.");
            }

            String bpTxt = Parser.hexToAscii(mt.group(5));
            if (bpLen == -1) {
                throw new ParseException(rPath + ": failed to determine blueprint text length.");
            } else if (bpTxt.length() > bpLen) {
                bpExcess = bpTxt.substring(bpLen);
                bpTxt = bpTxt.substring(0, bpLen);
            } else if (bpTxt.length() < bpLen) {
                throw new ParseException(rPath + ": blueprint text longer than calculated.");
            }
            if (!bpTxt.contains(".blueprint")) bpTxt = bpTxt + ".blueprint";

            try {
                nLen = Integer.parseInt(mt.group(8), 16);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }

            String nTxt = bpExcess == null ? Parser.hexToAscii(mt.group(9)) : bpExcess.substring(2) + Parser.hexToAscii("58 " + mt.group(12) + " " + mt.group(9));
            if (nLen == -1) {
                throw new ParseException(rPath + ": failed to determine name length.");
            } else if (nTxt.length() > nLen) {
                nExcess = nTxt.substring(nLen);
                nTxt = nTxt.substring(0, nLen);
            } else if (nTxt.length() < nLen) {
                System.out.println(nLen);
                System.out.println(nTxt);
                throw new ParseException(rPath + ": name longer than calculated.");
            }

            String dTxt = nExcess == null ? Parser.hexToAscii(mt.group(13)) : nExcess.substring(2) + Parser.hexToAscii("68 " + mt.group(12) + " " + mt.group(13));
            dTxt = dTxt.equals("") ? null : dTxt;
            return new Skin(rPath, nTxt, dTxt, bpTxt);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }
}
