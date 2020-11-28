import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        Workflows workflows = new Workflows();
        Parser parser = new Parser();
//        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\parsing\\test_abilities", "item");
//        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\parsing\\test_equipment", "item");
//        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\parsing\\test_loot", "item");
//        parser.prefabConvertDirectory("C:\\Users\\Julian\\Desktop\\parsing\\prefabs");

        // dir paths
        String recipeDirPath = "C:\\Users\\Julian\\Desktop\\parsing\\test_recipes";
        String itemDirPath = "C:\\Users\\Julian\\Desktop\\parsing\\test_item";
        String collDirPath = "C:\\Users\\Julian\\Desktop\\parsing\\test_collections";
        String benchPath = "C:\\Users\\Julian\\Desktop\\parsing\\test_benches";
        String[] recipeRefPaths = new String[] {"C:\\Users\\Julian\\Desktop\\parsing\\test_item", "C:\\Users\\Julian\\Desktop\\parsing\\test_placeable"};
        String testPath = "C:\\Users\\Julian\\Desktop\\parsing\\test_benches\\colorchanger_interactive.binfab";

        // log paths
        String dupLogPath = "C:\\Users\\Julian\\Desktop\\parsing\\duplicates.txt";
        String unmatchedLogPath = "C:\\Users\\Julian\\Desktop\\parsing\\unmatched.txt";
        String incompleteItemPath = "C:\\Users\\Julian\\Desktop\\parsing\\incomplete_items.txt";
        String incompleteCollPath = "C:\\Users\\Julian\\Desktop\\parsing\\incomplete_collections.txt";

        // workflows
//        workflows.recipePathMatch(recipeRefPaths,recipeDirPath, dupLogPath, unmatchedLogPath);
//        workflows.itemProcess(itemDirPath, incompleteItemPath);
//        workflows.itemProcess(collDirPath, incompleteCollPath);

        // DB testing
//        MongoHandler mango = new MongoHandler();

        // factory testing
        parser.convertDirectory(benchPath, "bench");
    }
}
