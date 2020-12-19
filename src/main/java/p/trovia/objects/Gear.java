package p.trovia.objects;

public class Gear {

    // used for actual gear, as well as geode modules
    String name;
    String desc;            // description of gear
    String[] recipes;       // path of recipes, should be ordered
    String[] stats;         // description of functionality; for modules they can be yoinked, gear stats may have to be calculated

    public Gear(String name, String desc, String[] stats) {
        this.name = name;
        this.desc = desc;
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

    public String[] getStats() {
        return stats;
    }
}
