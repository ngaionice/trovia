package datamodel.objects;

import datamodel.Enums;

import java.util.List;
import java.util.Map;

public class Collection implements Article {

    String name;
    String desc;
    String rPath;
    int troveMR;
    int geodeMR;
    List<Enums.Type> types;
    Map<Enums.Property, Double> properties;
    Map<Enums.Buff, Double> buffs;
    String blueprint;

    public Collection(String name, String desc, String rPath, int troveMR, int geodeMR, String blueprint,
                      List<Enums.Type> types,
                      Map<Enums.Property, Double> properties,
                      Map<Enums.Buff, Double> buffs) {
        this.name = name;
        this.desc = desc;
        this.rPath = rPath;
        this.troveMR = troveMR;
        this.geodeMR = geodeMR;
        this.blueprint = blueprint;
        this.types = types;
        this.properties = properties;
        this.buffs = buffs;
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

    public int getTroveMR() {
        return troveMR;
    }

    public void setTroveMR(int troveMR) {
        this.troveMR = troveMR;
    }

    public int getGeodeMR() {
        return geodeMR;
    }

    public void setGeodeMR(int geodeMR) {
        this.geodeMR = geodeMR;
    }

    public List<Enums.Type> getTypes() {
        return types;
    }

    public void setTypes(List<Enums.Type> types) {
        this.types = types;
    }

    public Map<Enums.Property, Double> getProperties() {
        return properties;
    }

    public void setProperties(Map<Enums.Property, Double> properties) {
        this.properties = properties;
    }

    public Map<Enums.Buff, Double> getBuffs() {
        return buffs;
    }

    public void setBuffs(Map<Enums.Buff, Double> buffs) {
        this.buffs = buffs;
    }

    public String getBlueprint() {
        return blueprint;
    }

    public void setBlueprint(String blueprint) {
        this.blueprint = blueprint;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Collection)) return false;
        Collection c = (Collection) o;
        return name.equals(c.getName()) && desc.equals(c.getDesc()) && rPath.equals(c.getRPath()) && troveMR == c.getTroveMR()
                && geodeMR == c.getGeodeMR() && types.equals(c.getTypes()) && properties.equals(c.getProperties()) && buffs.equals(c.getBuffs())
                && blueprint.equals(c.getBlueprint());
    }
}
