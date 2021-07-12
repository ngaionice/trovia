package datamodel.objects;

import java.util.Map;

public class GearStyleType implements Article {

    String rPath;
    String type;
    Map<String, Map<String, String[]>> styles;

    public GearStyleType(String rPath, String type, Map<String, Map<String, String[]>> styles) {
        this.rPath = rPath;
        this.type = type;
        this.styles = styles;
    }

    public void upsertStyle(String category, String blueprint, String[] data) {
        if (data.length == 3) {
            styles.get(category).put(blueprint, data);
        }
    }

    public void updateBlueprint(String category, String oldBlueprint, String newBlueprint) {
        String[] value = styles.get(category).get(oldBlueprint);
        styles.remove(oldBlueprint);
        styles.get(category).put(newBlueprint, value);
    }

    public String getRPath() {
        return rPath;
    }

    public String getType() {
        return type;
    }

    public Map<String, Map<String, String[]>> getStyles() {
        return styles;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStyles(Map<String, Map<String, String[]>> styles) {
        this.styles = styles;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GearStyleType)) return false;
        GearStyleType g = (GearStyleType) o;
        return rPath.equals(g.getRPath()) && type.equals(g.getType()) && styles.equals(g.getStyles());
    }
}
