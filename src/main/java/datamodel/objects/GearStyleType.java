package datamodel.objects;

import java.util.Map;

public class GearStyleType implements Article {

    String rPath;
    String type;
    Map<String, Map<String, GearStyleEntry>> styles;

    public GearStyleType(String rPath, String type, Map<String, Map<String, GearStyleEntry>> styles) {
        this.rPath = rPath;
        this.type = type;
        this.styles = styles;
    }

    public String getRPath() {
        return rPath;
    }

    public String getType() {
        return type;
    }

    public Map<String, Map<String, GearStyleEntry>> getStyles() {
        return styles;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GearStyleType)) return false;
        GearStyleType g = (GearStyleType) o;
        return rPath.equals(g.getRPath()) && type.equals(g.getType()) && styles.equals(g.getStyles());
    }
}
