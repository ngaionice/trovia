package p.trovia.creator.parsestrategies;

import p.trovia.creator.Parser;
import p.trovia.objects.Article;
import p.trovia.objects.LangFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseLangPrefab implements ParseStrategy {

    @Override
    public Article parseObject(String splitString, String absPath) {

        // instantiate the identifiers and variables
        Markers m = new Markers();
        List<String[]> pairs = new ArrayList<>();
        Pattern p = Pattern.compile(m.alphabetExtended);

        // trim the initial part, which is irrelevant, then split by "$prefab"
        String trimmedString = splitString.substring(splitString.indexOf("24 70"));
        String[] pathsAndStrings = trimmedString.split(m.prefabSpaced);
//        System.out.println(pathsAndStrings.length); // used for troubleshooting

        // split each item into the path and the actual string
        for (String s : pathsAndStrings) {
            String[] itemNameList = s.split(" 18 \\w\\w ");
            String stringPath = "24 70 72 65 66 61 62 73 " + itemNameList[0]; // add the "$prefab" back in since it was split
            Matcher m1 = p.matcher(itemNameList[1]);

            // find the string, convert to ASCII, and put the array into the list, if no string is found, break the loop
            if (m1.find()) {
                String string = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
                pairs.add(new String[]{Parser.hexToAscii(stringPath), Parser.hexToAscii(string)});
            } else {
                System.out.println("Something is problematic, check " + s + " in " + absPath);
                break;
            }
        }

        // extract the path
        String name = absPath.substring(absPath.lastIndexOf("\\")+1, absPath.indexOf(m.endFile));
        String rPath = absPath.substring(absPath.indexOf("language"), absPath.indexOf(m.endFile));
        rPath = rPath.replaceAll("\\\\", "/");

        return new LangFile(name, rPath, pairs);
    }
}
