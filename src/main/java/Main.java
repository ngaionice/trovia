import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\test_item", "item");
//        parser.factory("C:\\Users\\Julian\\Desktop\\test\\prefabs_item_crafting.binfab", "item");
    }
}
