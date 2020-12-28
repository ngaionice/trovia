package p.trovia.objects;

import java.util.List;
import java.util.Map;

public class Collection implements Article{

    String name;
    String desc;
    String rPath;
    boolean isMount;
    boolean isWings;
    boolean isBoat;
    boolean isDragon;
    boolean isMagRider;
    int troveMR;
    int geodeMR;
    Map<CollectionEnums.Property, Double> properties;  // should not contain mastery info
    Map<CollectionEnums.Buff, Double> buffs;           // for dragons only, else is null

    // for non-dragons
    public Collection(String name, String desc, String rPath, List<CollectionEnums.CollectionType> types, Map<CollectionEnums.Property, Double> properties) {
        this.name = name;
        this.desc = desc;
        this.rPath = rPath;
        if (types.size() != 0) {
            if (types.contains(CollectionEnums.CollectionType.MOUNT)) {
                isMount = true;
            }

            if (types.contains(CollectionEnums.CollectionType.WINGS)) {
                isWings = true;
            }

            if (types.contains(CollectionEnums.CollectionType.BOAT)) {
                isBoat = true;
            }

            if (types.contains(CollectionEnums.CollectionType.MAG)) {
                isMagRider = true;
            }
        }
        this.properties = properties;
    }

    // for dragons
    public Collection(String name, String desc, String rPath, Map<CollectionEnums.Property, Double> properties, Map<CollectionEnums.Buff, Double> buffs, boolean isMagRider) {
        this.name = name;
        this.desc = desc;
        this.rPath = rPath;
        isDragon = true;
        this.buffs = buffs;
        this.properties = properties;
        this.isMount = true;
        this.isWings = true;
        if (isMagRider) {
            this.isMagRider = true;
        }
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String getRPath() {
        return rPath;
    }

    public Map<CollectionEnums.Property, Double> getProperties() {
        return properties;
    }

    public Map<CollectionEnums.Buff, Double> getBuffs() {
        if (buffs == null) {
            System.out.println("This isn't a dragon, so there are no buffs.");
            return null;
        } else {
            return buffs;
        }
    }
}
