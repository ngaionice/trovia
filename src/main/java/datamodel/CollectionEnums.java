package datamodel;

import java.io.Serializable;

public class CollectionEnums implements Serializable {

    public enum Type {
        MOUNT,
        WINGS,
        BOAT,
        DRAGON,
        MAG
    }

    public enum Buff {
        PD,
        MD,
        MH,
        MH_PCT,
        CD,
        CH,
        AS,
        MF,
        EN, // max energy
        ER,
        ER_PCT,
        HR,
        HR_PCT,
        JP,
        LS, // lasermancy
        LT, // light
        PR
    }

    public enum Property {
        GROUND_MS,
        AIR_MS,
        GLIDE,
        WATER_MS,
        TURN_RATE,
        ACCEL,
        MAG_MS,
        POWER_RANK
    }
}
