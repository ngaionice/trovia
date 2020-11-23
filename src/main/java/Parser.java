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

    public void factory(String path, String prefabType) throws IOException {
        String baseString = spaceInserter(byteToString(path));
        switch (prefabType) {
            case "recipe":
                recipeFactory(baseString); // nothing is stored for now
                break;
            case "item":
                itemFactory(baseString);
                break;
            case "item-testing":
                itemTroubleFactory(baseString);
                break;
        }
    }

    public List<String[]> recipeFactory(String baseString) {
        Splitter splitter = new Splitter();
        List<String[]> listOfMaterials = splitter.recipeSplitter(baseString);
        for (String[] item: listOfMaterials) {
            item[0] = hexToAscii(item[0]);
            int quantity = hexToDecimal(item[1]);
            item[1] = quantity == 0 ? "Unlocked automatically" : Integer.toString(quantity);
            System.out.println(item[0] + " - " + item[1]);
        }
        return listOfMaterials;
    }

    public void itemFactory(String baseString) {
        Splitter splitter = new Splitter();
        List<String[]> itemContainers = splitter.generalizedItemSplitter(baseString);
        for (String[] item: itemContainers) {
            item[0] = hexToAscii(item[0]);
            item[1] = hexToAscii(item[1]);
            item[2] = hexToAscii(item[2]);
            item[3] = hexToAscii(item[3]);
            System.out.println(item[0] + " - " + item[1]);
            System.out.println(item[2] + " - " +item[3]);
        }
    }

    public void itemTroubleFactory(String baseString) {
        Splitter splitter = new Splitter();
        List<String[]> itemContainers = splitter.itemSplitterTroubleshooter(baseString);
        for (String[] item: itemContainers) {
            item[0] = hexToAscii(item[0]);
            item[1] = hexToAscii(item[1]);
            item[2] = hexToAscii(item[2]);
            item[3] = hexToAscii(item[3]);
//            System.out.println("name");
            System.out.println(item[0] + " - " + item[1]);
//            System.out.println("desc");
            System.out.println(item[2] + " - " +item[3]);
        }
    }

    public void convertDirectory(String path, String prefabType) throws IOException {
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                System.out.println(child.getPath());
                factory(child.getPath(), prefabType);
            }
        }
    }
}
