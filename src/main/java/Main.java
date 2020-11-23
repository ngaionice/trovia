import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\test_abilities", "item");
        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\test_collections", "item");
        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\test_equipment", "item");
        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\test_item", "item");
        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\test_loot", "item");
        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\test_placeable", "item");
        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\test_recipes", "recipe");
    }
}
