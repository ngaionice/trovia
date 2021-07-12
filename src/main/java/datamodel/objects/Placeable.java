package datamodel.objects;

public class Placeable implements Article {

    String name;
    String desc;
    String rPath;
    String blueprint;
    boolean tradable;

    public Placeable(String name, String desc, String rPath, String blueprint, boolean isTradable) {
        this.name = name;
        this.desc = desc;
        this.rPath = rPath;
        this.blueprint = blueprint;
        this.tradable = isTradable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;

    }

    public String getRPath() {
        return rPath;
    }

    public boolean getTradable() {
        return tradable;
    }

    public void setTradable(boolean tradable) {
        this.tradable = tradable;
    }

    public String getBlueprint() {
        return blueprint;
    }

    public void setBlueprint(String blueprint) {
        this.blueprint = blueprint;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Placeable)) return false;
        Placeable p = (Placeable) o;
        return name.equals(p.getName()) && desc.equals(p.getDesc()) && rPath.equals(p.getRPath())
                && blueprint.equals(p.getBlueprint()) && tradable == p.getTradable();
    }
}
