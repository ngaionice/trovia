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
        String hexAlphabet = "[6][0-9A-F]|[7][0-9A]";
        String[] firstParse = hexString.split(itemIdentifier);
        List<String> processing = Arrays.asList(firstParse);
        Pattern p = Pattern.compile(hexAlphabet);  // insert your pattern here
        List<String[]> parsedList = new ArrayList<>();
        for (String item: processing.subList(0, processing.size()-1)) {
            Matcher m = p.matcher(item);
            if (m.find()) {
                int position = m.start();
                parsedList.add(item.substring(position).split(" 10 "));
            }
        }
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

    public void factory(String path) throws IOException {
        List<String[]> listOfMaterials = recipeSplitter(spaceInserter(byteToString(path)));
        for (String[] item: listOfMaterials) {
            item[0] = hexToAscii(item[0]);
            System.out.println(item[0]);
        }
    }
}
