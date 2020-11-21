import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {
    public String byteToString(String path) throws IOException {
        byte[] array = Files.readAllBytes(Paths.get(path));
        return javax.xml.bind.DatatypeConverter.printHexBinary(array);
    }

    public String spaceInserter(String hexRaw) {
        String val = "2";
        return hexRaw.replaceAll("(.{" + val + "})", "$1 ").trim();
    }

    public List<String[]> recipeSplitter(String hexString) {
        String itemIdentifier = " 28 00 AE 03 00 01 18 00 28 00 1E 40 00 1E ";
        String hexAlphabet = "[6][0-9A-F]|[7][0-9A]|2F"; // contains backslash
        String[] firstParse = hexString.split(itemIdentifier);
        List<String> processing = Arrays.asList(firstParse);
        Pattern p = Pattern.compile(hexAlphabet);  // insert your pattern here
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
            if (hex.matches(hexAlphabet)) {
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

    public String hexToAscii(String hexString) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < hexString.length(); i += 3) {
            String str = hexString.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public int hexToDecimal(String hexNumber) {
        String[] hexPartition = hexNumber.split(" ");
        switch (hexPartition.length) {
            case 1:
                int parsing = Integer.parseInt(hexPartition[0], 16);
                if (parsing % 2 == 1) {
                    return (parsing+1)*-1/2;
                } else {
                    return parsing/2;
                }
            case 2:
                int base2 = Integer.parseInt(hexPartition[0], 16);
                int second2 = (Integer.parseInt(hexPartition[1], 16)-1)*64;
                if (base2 % 2 == 1) {
                    return ((base2+1)/2+second2)*-1;
                } else {
                    return base2/2+second2;
                }
            case 3:
                int base3 = Integer.parseInt(hexPartition[0], 16);
                int second3 = (Integer.parseInt(hexPartition[1], 16)-1)*64;
                int third3 = (Integer.parseInt(hexPartition[2], 16)-1)*8192;
                if (base3 % 2 == 1) {
                    return ((base3+1)/2+second3+third3)*-1;
                } else {
                    return base3/2+second3+third3;
                }
            default: // more than 3, rip
                System.out.println("Time to do more testing fam.");
                return 0;
        }
    }

    public void factory(String path) throws IOException {
        List<String[]> listOfMaterials = recipeSplitter(spaceInserter(byteToString(path)));
        for (String[] item: listOfMaterials) {
            item[0] = hexToAscii(item[0]);
            int quantity = hexToDecimal(item[1]);
            item[1] = quantity == 0 ? "Unlocked automatically" : Integer.toString(quantity);
            System.out.println(item[0] + " - " + item[1]);
        }
        // return listOfMaterials;
    }

    public void convertDirectory(String path) throws IOException {
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                System.out.println(child.getPath());
                factory(child.getPath());
            }
        }
    }
}
