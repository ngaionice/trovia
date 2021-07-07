package datamodel.parser.parsestrategies;

import datamodel.objects.Collection;
import datamodel.parser.Parser;
import datamodel.objects.Article;
import datamodel.Enums;
import local.Markers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseCollection implements ParseStrategy{

    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {

        // debugging
//        System.out.println("Parsing file " + absPath);
        if (absPath.contains("dev_")) {
            throw new ParseException(absPath + " is a dev mount and is likely to be buggy, skipping it.");
        }

        // instantiate markers and variables
        Markers m = new Markers();
        String name = null, desc = null;
        List<Enums.Type> types = new ArrayList<>();
        Map<Enums.Property, Double> properties = new HashMap<>(10);

        // obtain relative path
        String rPath = absPath.substring(absPath.indexOf("prefabs\\") + 8, absPath.indexOf(m.endFile));
        rPath = rPath.replaceAll("\\\\", "/");

//        System.out.println("Identified relative path.");

        // identify name and desc
        int nameDescEnd = splitString.indexOf(m.endNameDesc);
        int nameDescStart = splitString.indexOf(m.prefab);

//        System.out.println("Identified name and desc.");

        // if either don't exist, then we don't know how to parse yet
        if (nameDescStart == -1 || nameDescEnd == -1) {
            throw new ParseException("Collection creation at " + absPath + " failed as no name nor desc were identified.");
        }

        String dirty = splitString.substring(nameDescStart, nameDescEnd);

        // parse the name
        for (int i = 0; i < dirty.length(); i += 3) {
            if (!dirty.substring(i, i+2).matches(m.alphabetPrefab)) {
                String nameHex = dirty.substring(0, i);
                name = Parser.hexToAscii(nameHex);
                break;
            }
        }

//        System.out.println("Name parsed.");

        // parse the description
        int descStart = dirty.substring(1).indexOf(m.prefab); // shift by 1 to avoid finding the same marker
        if (descStart != -1) {
            String descHex = dirty.substring(1).substring(descStart);
            desc = Parser.hexToAscii(descHex);
        }

//        System.out.println("Desc parsed.");

        // identify abilities/properties

        // - identify boat/mount/wings/mag rider properties

        // mount
        if (splitString.contains(m.groundSpeed)) {
            int indexG = splitString.indexOf(m.groundSpeed);
            properties.put(Enums.Property.GROUND_MS, Parser.collectionH2D(splitString.substring(indexG-6, indexG-1)));
            types.add(Enums.Type.MOUNT);
        }

//        System.out.println("Identified mount property.");

        // wings
        if (splitString.contains(m.airSpeed) || splitString.contains(m.airSpeedA)) {
            int indexA = splitString.contains(m.airSpeed) ? splitString.indexOf(m.airSpeed) : splitString.indexOf(m.airSpeedA);
            int indexGlide = splitString.indexOf(m.glide);

            if (indexA != -1 && indexGlide != -1) {
                properties.put(Enums.Property.AIR_MS, Parser.collectionH2D(splitString.substring(indexA-6, indexA-1)));
                types.add(Enums.Type.WINGS);

                properties.put(Enums.Property.GLIDE, Parser.collectionH2D(splitString.substring(indexGlide-6, indexGlide-1)));
            } else {
                throw new ParseException("Incomplete wings property at this object.");
            }
        }

//        System.out.println("Identified wings property.");

        // mag rider
        if (splitString.contains(m.mag)) {
            int indexM = splitString.indexOf(m.mag);
            properties.put(Enums.Property.MAG_MS, Parser.collectionH2D(splitString.substring(indexM+3, indexM+9)));
            types.add(Enums.Type.MAG);
        }

//        System.out.println("Identified mag property.");

        // boat
        if (splitString.contains(m.waterSpeed)) {
            types.add(Enums.Type.BOAT);

            int indexW = splitString.indexOf(m.waterSpeed);
            properties.put(Enums.Property.WATER_MS, Parser.collectionH2D(splitString.substring(indexW-6, indexW-1)));

            int indexT = splitString.indexOf(m.turnRate);
            properties.put(Enums.Property.TURN_RATE, Parser.collectionH2D(splitString.substring(indexT-6, indexT-1)));

            int indexAc = splitString.indexOf("24", indexT + m.turnRate.length());
            properties.put(Enums.Property.ACCEL, Parser.collectionH2D(splitString.substring(indexAc + 9, indexAc + 15)));
        }

//        System.out.println("Identified boat property.");

        // dragon
        Map<Enums.Buff, Double> dragonBuffs = parseBuffs(splitString, m);
        if (!dragonBuffs.isEmpty() && types.contains(Enums.Type.MOUNT)) {
            types.add(Enums.Type.DRAGON);
        }

//        System.out.println("Identified dragon property.");

        // power rank
        if (splitString.contains(m.powerRank)) {
            dragonBuffs.put(Enums.Buff.PR, 30.0);
        }

//        System.out.println("Identified PR property.");

        // old stuff below
//        if (types.contains(CollectionEnums.Type.DRAGON)) {
//            boolean isMagRider = properties.containsKey(CollectionEnums.Property.MAG_MS);
//            return new Collection(name, desc, rPath, properties, dragonBuffs, isMagRider);
//        } else if (!dragonBuffs.isEmpty()) {
//            return new Collection(name, desc, rPath, types, properties, dragonBuffs);
//        }
//        return new Collection(name, desc, rPath, types, properties);
        return new Collection(name, desc, rPath, 0, 0, types, properties, dragonBuffs);
    }


    private Map<Enums.Buff, Double> parseBuffs(String splitString, Markers m) {

        Map<Enums.Buff, Double> buffs = new HashMap<>(10);

        // identify max health
        if (splitString.contains(m.maxHealth)) {
            statExtract(splitString, m, buffs, m.maxHealth, true, Enums.Buff.MH_PCT, Enums.Buff.MH);

            if (splitString.contains(m.maxHealth2)) {
                statExtract(splitString, m, buffs, m.maxHealth2, true, Enums.Buff.MH_PCT, Enums.Buff.MH);
            }

        }

        // identify hp regen
        if (splitString.contains(m.hpRegen)) {
            statExtract(splitString, m, buffs, m.hpRegen, true, Enums.Buff.HR_PCT, Enums.Buff.HR);
        } else if (splitString.contains(m.hpRegen2)) {
            statExtract(splitString, m, buffs, m.hpRegen2, true, Enums.Buff.HR_PCT, Enums.Buff.HR);
        }

        // identify magic damage
        if (splitString.contains(m.magicDamage)) {
            statExtract(splitString, m, buffs, m.magicDamage, false, Enums.Buff.MD, null);
        } else if (splitString.contains(m.magicDamage2)) {
            statExtract(splitString, m, buffs, m.magicDamage2, false, Enums.Buff.MD, null);
        } else if (splitString.contains(m.magicDamage3)) {
            statExtract(splitString, m, buffs, m.magicDamage3, false, Enums.Buff.MD, null);
        }

        // identify physical damage
        if (splitString.contains(m.physDamage)) {
            statExtract(splitString, m, buffs, m.physDamage, false, Enums.Buff.PD, null);
        } else if (splitString.contains(m.physDamage2)) {
            statExtract(splitString, m, buffs, m.physDamage2, false, Enums.Buff.PD, null);
        }

        // identify crit damage
        if (splitString.contains(m.critDamage)) {
            statExtract(splitString, m, buffs, m.critDamage, false, Enums.Buff.CD, null);
        } else if (splitString.contains(m.critDamage2)) {
            statExtract(splitString, m, buffs, m.critDamage2, false, Enums.Buff.CD, null);
        }

        // identify crit hit chance
        if (splitString.contains(m.critHit)) {
            statExtract(splitString, m, buffs, m.critHit, false, Enums.Buff.CH, null);
        } else if (splitString.contains(m.critHit2)) {
            statExtract(splitString, m, buffs, m.critHit2, false, Enums.Buff.CH, null);
        }

        // identify max energy
        if (splitString.contains(m.maxEnergy)) {
            statExtract(splitString, m, buffs, m.maxEnergy, false, Enums.Buff.EN, null);
        }

        // identify energy regen
        if (splitString.contains(m.energyRegen)) {
            statExtract(splitString, m, buffs, m.energyRegen, true, Enums.Buff.ER_PCT, Enums.Buff.ER);
        }

        // identify misc stats
        if (splitString.contains(m.light)) {
            statExtract(splitString, m, buffs, m.light, false, Enums.Buff.LT, null);
        }

        if (splitString.contains(m.jump)) {
            statExtract(splitString, m, buffs, m.jump, false, Enums.Buff.JP, null);
        }

        if (splitString.contains(m.laser)) {
            statExtract(splitString, m, buffs, m.laser, false, Enums.Buff.LS, null);
        }

        if (splitString.contains(m.attackSpeed)) {
            statExtract(splitString, m, buffs, m.attackSpeed, false, Enums.Buff.AS, null);
        }

        if (splitString.contains(m.magicFind)) {
            statExtract(splitString, m, buffs, m.magicFind, false, Enums.Buff.MF, null);
        } else if (splitString.contains(m.magicFind2)) {
            statExtract(splitString, m, buffs, m.magicFind2, false, Enums.Buff.MF, null);
        }

        return buffs;
    }

    private void statExtract(String splitString, Markers m, Map<Enums.Buff, Double> buffs, String type, boolean options, Enums.Buff stat1, Enums.Buff stat2) {

        int indexProp = splitString.indexOf(type) - 12;

        int index = splitString.lastIndexOf("24", indexProp);

        String stat = splitString.substring(index + 3, index + 14);   // excludes the trailing whitespace

        if (options) {
            if (stat.substring(0, 5).equals(m.percent)) {
                buffs.put(stat1, Parser.collectionH2D(stat.substring(6)));
            } else {
                buffs.put(stat2, Parser.collectionH2D(stat.substring(6)));
            }
        } else {
            buffs.put(stat1, Parser.collectionH2D(stat.substring(6)));
        }

    }
    // parsing collection files:

    // done:
    // identify relative path for item matching
    // identify name & description
    // identify ground/air move speed/glide for mounts
    // identify speed, turning rate, acceleration for boats
    // identify power rank and stat boosts if applicable (dragons/badges) -> if stats are from badges, these won't show up for the dragon
    // add empty slot for trove, geode mr

    // not done
    // identify the blueprint; needed to match image later
    // no straightforward way found atm, might be an extension for later

    // blueprints:

    // tomes are not functional
    // sails don't seem to have a specified blueprint

    // C_MT marker seems to be a possible option, but is only applicable to mounts

    // C_C_ for pets folder seems to be ok; though need to skip any files with npc in name

    //



}
