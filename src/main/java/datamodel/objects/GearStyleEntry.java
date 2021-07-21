package datamodel.objects;

import java.util.Objects;

public class GearStyleEntry {

    private final String name;
    private final String desc;
    private final String blueprint;
    private final String additionalInfo;

    public GearStyleEntry(String name, String desc, String blueprint, String additionalInfo) {
        this.name = name;
        this.desc = desc;
        this.blueprint = blueprint;
        this.additionalInfo = additionalInfo;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getBlueprint() {
        return blueprint;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GearStyleEntry)) return false;
        GearStyleEntry g = (GearStyleEntry) o;
        return Objects.equals(name, g.getName()) && Objects.equals(desc, g.getDesc()) && Objects.equals(blueprint, g.getBlueprint()) && Objects.equals(additionalInfo, g.getAdditionalInfo());
    }
}
