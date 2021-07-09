package datamodel;

public class Enums {

    public enum Type {
        MOUNT,
        WINGS,
        BOAT,
        DRAGON,
        MAG;

        @Override
        public String toString() {
            switch (this) {
                case MOUNT:
                    return "MOUNT";
                case WINGS:
                    return "WINGS";
                case BOAT:
                    return "BOAT";
                case DRAGON:
                    return "DRAGON";
                case MAG:
                    return "MAG";
                default:
                    throw new IllegalArgumentException();
            }
        }
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
        PR;

        @Override
        public String toString() {
            switch (this) {
                case PD:
                    return "PD";
                case MD:
                    return "MD";
                case MH:
                    return "MH";
                case MH_PCT:
                    return "MH_PCT";
                case CD:
                    return "CD";
                case CH:
                    return "CH";
                case AS:
                    return "AS";
                case MF:
                    return "MF";
                case EN:
                    return "EN";
                case ER:
                    return "ER";
                case ER_PCT:
                    return "ER_PCT";
                case HR:
                    return "HR";
                case HR_PCT:
                    return "HR_PCT";
                case JP:
                    return "JP";
                case LS:
                    return "LS";
                case LT:
                    return "LT";
                case PR:
                    return "PR";
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public enum Property {
        GROUND_MS,
        AIR_MS,
        GLIDE,
        WATER_MS,
        TURN_RATE,
        ACCEL,
        MAG_MS,
        POWER_RANK;

        @Override
        public String toString() {
            switch (this) {
                case GROUND_MS:
                    return "GROUND_MS";
                case AIR_MS:
                    return "AIR_MS";
                case WATER_MS:
                    return "WATER_MS";
                case MAG_MS:
                    return "MAG_MS";
                case ACCEL:
                    return "ACCEL";
                case GLIDE:
                    return "GLIDE";
                case TURN_RATE:
                    return "TURN_RATE";
                case POWER_RANK:
                    return "POWER_RANK";
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public enum ObjectType {
        ITEM,
        BENCH,
        COLLECTION,
        COLL_INDEX,
        GEAR_STYLE,
        STRING,
        PLACEABLE,
        RECIPE,
        PROFESSION,
        SKIN;

        public static ObjectType getType(String input) {
            String inputCleaned = input.toLowerCase();
            if (inputCleaned.contains("item")) return ITEM;
            else if (inputCleaned.contains("bench")) return BENCH;
            else if (inputCleaned.contains("collection")) {
                if (inputCleaned.contains("index") || inputCleaned.contains("indices")) {
                    return COLL_INDEX;
                }
                return COLLECTION;
            }
            else if (inputCleaned.contains("gear style")) return GEAR_STYLE;
            else if (inputCleaned.contains("placeable")) return PLACEABLE;
            else if (inputCleaned.contains("profession")) return PROFESSION;
            else if (inputCleaned.contains("recipe")) return RECIPE;
            else if (inputCleaned.contains("skin")) return SKIN;
            else if (inputCleaned.contains("string")) return STRING;
            else throw new IllegalArgumentException();
        }

        @Override
        public String toString() {
            switch (this) {
                case ITEM:
                    return "Item";
                case BENCH:
                    return "Bench";
                case COLLECTION:
                    return "Collection";
                case COLL_INDEX:
                    return "Collection index";
                case GEAR_STYLE:
                    return "Gear style";
                case PLACEABLE:
                    return "Placeable";
                case PROFESSION:
                    return "Profession";
                case RECIPE:
                    return "Recipe";
                case SKIN:
                    return "Skin";
                case STRING:
                    return "Language File";
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}
