package p.trovia.objects;

import java.io.Serializable;

public class Gear implements Article, Serializable {

    // used for actual gear, as well as geode modules
    String name;
    String desc;            // description of gear
    String rPath;
    String[] recipes;       // path of recipes, should be ordered
    String[] stats;         // description of functionality; for modules they can be yoinked, gear stats may have to be calculated

    public Gear(String name, String desc, String rPath, String[] stats) {
        this.name = name;
        this.desc = desc;
        this.rPath = rPath;
        this.stats = stats;
    }

    public void setRecipes(String[] recipes) {
        this.recipes = recipes;
    }

    public void setStats(String[] stats) {
        this.stats = stats;
    }

    public String[] getRecipes() {
        return recipes;
    }

    @Override
    public String getRPath() {
        return rPath;
    }

    public String[] getStats() {
        return stats;
    }
}
