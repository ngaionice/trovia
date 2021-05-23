package datamodel.parser.parsestrategies;

import datamodel.objects.ObservableRecipe;
import datamodel.parser.Parser;
import datamodel.objects.Article;
import local.Markers;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseRecipe implements ParseStrategy {

    @Override
    public Article parseObject(String splitString, String absPath) {

        Markers m = new Markers();

        // extract path and rPath
        String path = absPath.substring(absPath.lastIndexOf("\\")+1, absPath.indexOf(m.endFile));
        String rPath = absPath.substring(absPath.indexOf("prefabs\\")+8, absPath.indexOf(m.endFile));
        rPath = rPath.replaceAll("\\\\", "/");

        // instantiate identifiers
        String itemIdentifier = " 28 00 AE 03 00 01 18 00 28 00 1E 40 00 1E ";
        Pattern p = Pattern.compile(m.alphabetRecipeRPath);

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
            Matcher ma = p.matcher(item);
            if (ma.find()) {
                int position = ma.start();
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
            if (hex.matches(m.alphabetRecipeRPathExtended)) {
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
        Pattern pLC = Pattern.compile(m.alphabetLowerCase);

        Map<String, Integer> costs = new HashMap<>();
        Map<String, Integer> product = new HashMap<>();
        for (int i = 0; i < parsedList.size() - 1; i++) {
            String[] item = parsedList.get(i);
            // clean up stray matching characters that are not lowercase letters, as any rPath must start with lowercase
            Matcher mLC = pLC.matcher(item[0]);
            if (mLC.find()) {
                item[0] = item[0].substring(mLC.start());
            }
            costs.put(Parser.hexToAscii(item[0]), Parser.recipeH2D(item[1], item[0]));
        }
        String[] lastEntry = parsedList.get(parsedList.size() - 1);
        product.put(Parser.hexToAscii(lastEntry[0]), Parser.recipeH2D(lastEntry[1], lastEntry[0]));

        return new ObservableRecipe(path, rPath, costs, product);
    }
}
