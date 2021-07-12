package datamodel.objects;

public class Skin implements Article {

    String rPath;
    String name;
    String desc;
    String blueprint;

    public Skin(String rPath, String name, String desc, String blueprint) {
        this.rPath = rPath;
        this.name = name;
        this.desc = desc;
        this.blueprint = blueprint;
    }

    public String getRPath() {
        return rPath;
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

    public String getBlueprint() {
        return blueprint;
    }

    public void setBlueprint(String blueprint) {
        this.blueprint = blueprint;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Skin)) return false;
        Skin s = (Skin) o;
        return name.equals(s.getName()) && desc.equals(s.getDesc()) && rPath.equals(s.getRPath()) && blueprint.equals(s.getBlueprint());
    }
}
