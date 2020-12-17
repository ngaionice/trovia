package xyz.trovia.creator.parsestrategies;

import xyz.trovia.creator.Parser;
import xyz.trovia.creator.parsestrategies.ParseStrategy;
import xyz.trovia.objects.Article;
import xyz.trovia.objects.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseRecipe implements ParseStrategy {

    public Article parseObject(String splitString, String absPath) {

        // extract path
        String path = absPath.substring(absPath.lastIndexOf("\\")+1, absPath.indexOf(".binfab"));

        // instantiate identifiers
        String hexAlphabetPrefab = "[6][0-9A-F]|[7][0-9A]|5F|[3][0-9]"; // for identifying name and desc; lowercase alphabet, digits, underscore
        String itemIdentifier = " 28 00 AE 03 00 01 18 00 28 00 1E 40 00 1E ";
        Pattern p = Pattern.compile(hexAlphabetPrefab);

        // instantiate variables
        List<String[]> parsedList = new ArrayList<>();

        // split recipe into chunks by item
        List<String> hexStringList = Arrays.asList(splitString.split(itemIdentifier));

        // some strings do not contain the identifier "2F", so we need cases
        int lastIndex = hexStringList.get(hexStringList.size()-1).contains("2F") ?
                hexStringList.size() :
                hexStringList.size()-1;

        // process each chunk one by one
        for (String item: hexStringList.subList(0, lastIndex)) {
            Matcher m = p.matcher(item);
            if (m.find()) {
                int position = m.start();
                String[] currItem = item.substring(position).split(" 10 ");
                if (currItem.length == 1) {
                    currItem = new String[]{currItem[0], "00"};
                }
                parsedList.add(currItem);
            }
        }

        // process the last chunk separately, due to unique properties
        String lastString = parsedList.get(lastIndex-1)[0];
        String[] lastStringArray = lastString.split(" ");
        int lastValidCharacter = 0;
        for (String hex: lastStringArray) {
            if (hex.matches(hexAlphabetPrefab)) {
                lastValidCharacter++;
            } else {
                break;
            }
        }
        StringBuilder newLastString = new StringBuilder();
        for (int i = 0; i < lastValidCharacter; i++) {
            newLastString.append(lastStringArray[i]);
            newLastString.append(" ");
        }
        parsedList.get(lastIndex-1)[0] = newLastString.toString();

        // convert the hex strings/numbers to ascii/decimals
        for (String[] item : parsedList) {
            item[0] = Parser.hexToAscii(item[0]);
            item[1] = Integer.toString(Parser.hexToDecimal(item[1], item[0]));
        }

        // create the object
        String[] product = parsedList.get(lastIndex-1);
        parsedList.remove(lastIndex-1);
        String[][] costs = parsedList.toArray(new String[0][0]);

        return new Recipe(path, costs, product);
    }
}
