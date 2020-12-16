package xyz.trovia.objects;

public class Recipe {

    // for actual recipes, or manual recipes, e.g. gear upgrade costs, module upgrade costs

    String[][] costs;   // array of string arrays, each sub-array in the format [recipe-specific item path, quantity]
    String[] output;    // the result of crafting the recipe, in the format of [item name path, quantity]

    public Recipe(String[][] costs, String[] output) {
        this.costs = costs;
        this.output = output;
    }

    public String[][] getCosts() {
        return costs;
    }

    public String[] getOutput() {
        return output;
    }
}
