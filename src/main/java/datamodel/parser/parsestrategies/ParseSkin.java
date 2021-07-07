package datamodel.parser.parsestrategies;

import datamodel.objects.Article;
import datamodel.objects.Skin;
import datamodel.parser.Parser;
import local.Markers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseSkin implements ParseStrategy {
    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {
        Markers m = new Markers();
        String rPath = absPath.substring(absPath.indexOf("prefabs\\") + 8, absPath.indexOf(m.endFile))
                .replaceAll("\\\\", "/");
        Pattern p = Pattern.compile("(08 1E 1E 08 BE 03 2E 00 |24 ([0-9A-F][0-9A-F] ){5})08 48 (([0-9A-F][0-9A-F]) (([0-9A-F][0-9A-F] )+))58 (([0-9A-F][0-9A-F]) (([0-9A-F][0-9A-F] )+))68 (([0-9A-F][0-9A-F]) (([0-9A-F][0-9A-F] )*))1E$");
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
    }
}
