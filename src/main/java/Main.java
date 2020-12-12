import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Workflows workflows = new Workflows();
        Parser parser = new Parser();
        Variables var = new Variables();

        // workflows
//        List<List<String[]>> recipes = workflows.createBaseRecipes(recipeDirPath);
//        List<List<String[]>> benches = workflows.getBenchRecipes(parser.convertDirectory(benchPath, "bench", true));
//        List<List<String[]>> completeRecipes = workflows.addRecipeStation(recipes, benches, recipeNoBenchPath);

//        workflows.recipeTest(completeRecipes);
        List<Map<String, String>> itemList =  workflows.createItems(var.itemDirPath, var.incompleteItemPath);
//        workflows.createItems(var.collDirPath, var.incompleteCollPath);
//        List<List<String[]>> newRecipes = workflows.createBaseRecipes(var.newRecipesPath);
//        workflows.writeRecipesToFile(newRecipes, var.newRecipesFile);
        // DB testing
        Exporter mango = new Exporter();
//        mango.importRecipes(completeRecipes);

    }
}
