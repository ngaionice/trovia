import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        Reshaper reshape = new Reshaper();
//        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\parsing\\test_abilities", "item");
//        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\parsing\\test_collections", "item");
//        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\parsing\\test_equipment", "item");
//        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\parsing\\test_loot", "item");
//        parser.prefabConvertDirectory("C:\\Users\\Julian\\Desktop\\parsing\\prefabs");

        List<List<String[]>> recipes = parser.convertDirectory("C:\\Users\\Julian\\Desktop\\parsing\\test_recipes", "recipe");
        List<List<String[]>> items = parser.convertDirectory("C:\\Users\\Julian\\Desktop\\parsing\\test_item", "item");
        List<List<String[]>> placeables = parser.convertDirectory("C:\\Users\\Julian\\Desktop\\parsing\\test_placeable", "item");

        List<String> paths = reshape.extractUniquePaths(recipes);
        List<String[]> itemsMerged = reshape.itemListMerge(items);
        List<String[]> placeablesMerged = reshape.itemListMerge(placeables);

        List<String[]> allMerged = reshape.referenceMerge(placeablesMerged, itemsMerged);
        reshape.itemMatch(allMerged, paths);
    }
}
