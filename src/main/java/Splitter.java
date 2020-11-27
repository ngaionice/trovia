import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Splitter {

    static String hexAlphabetBase = "[6][0-9A-F]|[7][0-9A]|2F"; // contains slash
    static String hexAlphabetExtended = "0A|[4][1-9A-F]|[5][0-9A]|[6][0-9A-F]|[7][0-9A]|2[017CE]|3[AF]|5[CF]";
    // period, space, newline, exclaim/question marks, backslash, underscore, maybe some more that i forgot

    public List<String[]> recipeSplitter(String hexString) {
        String itemIdentifier = " 28 00 AE 03 00 01 18 00 28 00 1E 40 00 1E ";
        String[] firstParse = hexString.split(itemIdentifier);
        List<String> processing = Arrays.asList(firstParse);
        Pattern p = Pattern.compile(hexAlphabetBase);
        List<String[]> parsedList = new ArrayList<>();
        int lastIndex = processing.get(processing.size()-1).contains("2F") ? processing.size() : processing.size()-1;
        for (String item: processing.subList(0, lastIndex)) {
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
        // need to solve the issue of the last string
        String lastString = parsedList.get(lastIndex-1)[0];
        String[] lastStringArray = lastString.split(" ");
        int lastValidCharacter = 0;
        for (String hex: lastStringArray) {
            if (hex.matches(hexAlphabetBase)) {
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
        return parsedList;
    }

    public List<String[]> generalizedItemSplitter(String hexString) {
        String itemIdentifier = " 24 70 72 65 66 61 62 73 ";
//        String subHexString = hexString.substring(hexString.indexOf("24 70")); // old identifier
        String subHexString = hexString.substring(hexString.indexOf("24 70")+24);
        String[] firstParse = subHexString.split(itemIdentifier);
//        System.out.println(firstParse.length); // used for troubleshooting
        // each item is stored in 2 indices; name and description
        List<String[]> itemList = new ArrayList<>();
        Pattern p = Pattern.compile(hexAlphabetExtended);
        String noName = "4E 6F 20 6E 61 6D 65 20 61 73 73 6F 63 69 61 74 65 64 2E";
        String noDesc = "4E 6F 20 64 65 73 63 20 61 73 73 6F 63 69 61 74 65 64 2E";
        String noPath = "4E 2F 41";
        String description = "64 65 73 63";
        String name = "6E 61 6D 65";
        for (int i = 0; i < firstParse.length; i++) {
            // case 1: both are available
            if (firstParse[i].contains(name) && i+1 < firstParse.length && firstParse[i+1].contains(description)) {
                String[] itemNameList = firstParse[i].split(" 18 \\w\\w " );
                String[] itemDescList = firstParse[i+1].split(" 18 \\w\\w ");
                String itemNamePath = "70 72 65 66 61 62 73 "+itemNameList[0];
                String itemDescPath = "70 72 65 66 61 62 73 "+itemDescList[0];
                Matcher m1 = p.matcher(itemNameList[1]);
                Matcher m2 = p.matcher(itemDescList[1]);
                // last 2 conditions check if a blank name/description was provided, which would crash the program if not caught
                if (m1.find() && m2.find() && !itemNameList[1].substring(0,2).contains("BE") && !itemDescList[1].substring(0,2).contains("BE")) {
                    String itemName = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
                    String itemDesc = itemDescList[1].substring(m2.start(), itemDescList[1].indexOf(" BE"));
                    itemList.add(new String[] {itemNamePath, itemName, itemDescPath, itemDesc});
                    i++;
                } else {
                    System.out.println("Something is problematic");
                    break;
                }
            }
            // case 2: only name
            else if (firstParse[i].contains(name) && (i+1 >= firstParse.length || !firstParse[i+1].contains(description))) {
                String[] itemNameList = firstParse[i].split(" 18 \\w\\w " );
                String itemNamePath = "70 72 65 66 61 62 73 "+itemNameList[0];
                Matcher m1 = p.matcher(itemNameList[1]);
                if (m1.find()) {
                    String itemName = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
                    itemList.add(new String[] {itemNamePath, itemName, noPath, noDesc});
                } else {
                    System.out.println("Something went wrong while matching name.");
                    break;
                }
            }
            // else: only desc is available
            else {
                String[] itemDescList = firstParse[i].split(" 18 \\w\\w ");
                String itemDescPath = "70 72 65 66 61 62 73 "+itemDescList[0];
                Matcher m2 = p.matcher(itemDescList[1]);
                if (m2.find()) {
                    String itemDesc = itemDescList[1].substring(m2.start(), itemDescList[1].indexOf(" BE"));
                    itemList.add(new String[] {noPath, noName, itemDescPath, itemDesc});
                } else {
                    System.out.println("Something went wrong while matching desc.");
                    break;
                }
            }
        }
        return itemList;
    }

    public List<String[]> itemSplitterTroubleshooter(String hexString) {
        String itemIdentifier = " 24 70 72 65 66 61 62 73 ";
        String subHexString = hexString.substring(hexString.indexOf("24 70"));
        String[] firstParse = subHexString.split(itemIdentifier);
//        System.out.println(firstParse.length); // used for troubleshooting
        // each item is stored in 2 indices; name and description
        List<String[]> itemList = new ArrayList<>();
        Pattern p = Pattern.compile(hexAlphabetExtended);
        for (String s : firstParse) {
            String[] itemNameList = s.split(" 18 \\w\\w ");
            String itemNamePath = "70 72 65 66 61 62 73 " + itemNameList[0];
            Matcher m1 = p.matcher(itemNameList[1]);
            if (m1.find()) {
                String itemName = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
                itemList.add(new String[]{itemNamePath, itemName, "4E 2F 41", "4E 2F 41"});
            } else {
                System.out.println("Something is problematic");
                break;
            }
        }
        return itemList;
    }

//    public List<String[]> itemSplitter(String hexString) { // old, redundant now
//        String itemIdentifier = " 24 70 72 65 66 61 62 73 ";
//        String subHexString = hexString.substring(hexString.indexOf("24"));
//        String[] firstParse = subHexString.split(itemIdentifier);
//        System.out.println(firstParse.length);
//        // each item is stored in 2 indices; name and description
//        List<String[]> itemList = new ArrayList<>();
//        Pattern p = Pattern.compile(hexAlphabetExtended);
//        for (int i = 0; i < firstParse.length; i+=2) {
//            // special daily token case
//            if (firstParse[i].contains("5F 69 74 65 6D 5F 63 72 61 66 74 69 6E 67 5F 64 61 69 6C 79 63 6F 69 6E 5F 6A 75 6E 65 5F 69 74 65 6D 5F 6E 61 6D 65")) {
//                String[] itemDescList = firstParse[i+19].split(" 18 \\w\\w ");
//                Matcher m2 = p.matcher(itemDescList[1]);
//                String itemDesc;
//                if (m2.find()) {
//                    itemDesc = itemDescList[1].substring(m2.start(), itemDescList[1].indexOf(" BE"));
//                } else {
//                    itemDesc = "4E 6F 20 64 65 73 63 20 61 73 73 6F 63 69 61 74 65 64 2E"; // No desc associated.
//                }
//                String itemDescPath = "70 72 65 66 61 62 73 "+itemDescList[0];
//                for (String item:Arrays.copyOfRange(firstParse, i, i+19)) {
//                    String[] itemNameList = item.split(" 18 \\w\\w " );
//                    String itemNamePath = itemNameList[0];
//                    Matcher m1 = p.matcher(itemNameList[1]);
//                    String itemName;
//                    if (m1.find()) {
//                        itemName = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
//                    } else {
//                        itemName = "4E 6F 20 6E 61 6D 65 20 61 73 73 6F 63 69 61 74 65 64 2E"; // No name associated.
//                    }
//                    itemList.add(new String[] {itemNamePath, itemName, itemDescPath, itemDesc});
//                }
//                i+=18;
//                continue;
//            }
//            // special double description dragon coin case
//            if (firstParse[i].contains("63 6F 6E 74 72 6F 6C 6C 65 72")) {
//                i -= 1;
//                continue;
//            }
//            // special avarem item case
//            if (firstParse[i].contains("63 72 61 66 74 69 6E 67 5F 61 76 61 72 65 6D")) {
//                i += 6;
//                // this also skips a random unrelated titan soul description that goes with these items.
//                continue;
//            }
//            String[] itemNameList = firstParse[i].split(" 18 \\w\\w " );
//            String[] itemDescList = firstParse[i+1].split(" 18 \\w\\w ");
//            String itemNamePath = "70 72 65 66 61 62 73 "+itemNameList[0];
//            String itemDescPath = "70 72 65 66 61 62 73 "+itemDescList[0];
//            Matcher m1 = p.matcher(itemNameList[1]);
//            Matcher m2 = p.matcher(itemDescList[1]);
//            if (m1.find() && m2.find()) {
//                String itemName = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
//                String itemDesc = itemDescList[1].substring(m2.start(), itemDescList[1].indexOf(" BE"));
//                itemList.add(new String[] {itemNamePath, itemName, itemDescPath, itemDesc});
//            } else {
//                System.out.println("Something is problematic");
//                break;
//            }
//        }
//        return itemList;
//    }

    //    // new approach; match files to path instead of attempting to form items myself; match the natural file structure
//    public List<String[]> prefabMatch(String hexString, String path) {
//        String itemIdentifier = " 24 70 72 65 66 61 62 73 "; // $prefabs
//        String subHexString = hexString.substring(hexString.indexOf("24 70")+24);
//        String[] firstParse = subHexString.split(itemIdentifier);
//        if (firstParse.length == 1) {
//            parseFail(path);
//            return new ArrayList<>();
//        }
//        // each item is stored in 2 indices; path and name/description
//        List<String[]> itemList = new ArrayList<>();
//        Pattern p = Pattern.compile(hexAlphabetExtended);
//        Parser parser = new Parser();
//        String noData = "4E 6F 20 64 61 74 61 20 61 73 73 6F 63 69 61 74 65 64 2E";
//        String description = "64 65 73 63";
//        String name = "6E 61 6D 65";
//        for (int i = 0; i < firstParse.length; i++) {
//            if (firstParse[i].contains(name) || firstParse[i].contains(description)) {
//                String[] item = firstParse[i].split(" 18 \\w\\w " );
//                String itemPath = "70 72 65 66 61 62 73 "+item[0];
//                Matcher m1 = p.matcher(item[1]);
//                if (m1.find() && !item[1].substring(0,2).contains("BE")) {
//                    String itemText = item[1].substring(m1.start(), item[1].indexOf(" BE"));
//                    itemList.add(new String[] {itemPath, itemText});
//                    i++;
//                }
//            } else {
//                parseFail(path);
//            }
//        }
//        return itemList;
//    }

//    private void parseFail(String path) {
//        try {
//            Path writePath = Paths.get("C:\\Users\\Julian\\Desktop\\parsing\\logged.txt");
//            Files.write(writePath, Arrays.asList(path + "was not added to the list"), StandardCharsets.UTF_8,
//                    Files.exists(writePath) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
//        } catch (IOException e) {
//            System.out.println("Could not write to file.");
//        }
//    }
}
