package p.trovia.objects;

import java.io.Serializable;

public class Recipe implements Article, Serializable {

    // for actual recipes, or manual recipes, e.g. gear upgrade costs, module upgrade costs

    String name;        // e.g. recipe_block_blue_08
    String bench;       // e.g. colorchanger_interactive
    String rPath;       // relative path
    String[][] costs;   // array of string arrays, each sub-array in the format [recipe-specific item path, quantity]
    String[] output;    // the result of crafting the recipe, in the format of [item name path, quantity], if item unlocks automatically, quantity = 0

    public Recipe(String name, String rPath, String[][] costs, String[] output) {
        this.name = name;
        this.rPath = rPath;
        this.costs = costs;
        this.output = output;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getRPath() {
        return rPath;
    }

    public String[][] getCosts() {
        return costs;
    }

    public String[] getOutput() {
        return output;
    }

    public String getBench() {
        return bench;
    }

    public void setBench(String bench) {
        this.bench = bench;
    }
}
