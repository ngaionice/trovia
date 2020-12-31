package parser.parsestrategies;

import parser.Parser;
import objects.Article;
import objects.Collection;
import objects.CollectionEnums;
import local.Markers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseCollection implements ParseStrategy{

    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {

        // instantiate markers and variables
        Markers m = new Markers();
        String name = null, desc = null;
        List<CollectionEnums.CollectionType> types = new ArrayList<>();
        Map<CollectionEnums.Property, Double> properties = new HashMap<>(10);

        // obtain relative path
        String rPath = absPath.substring(absPath.indexOf("prefabs\\") + 8, absPath.indexOf(m.endFile));
        rPath = rPath.replaceAll("\\\\", "/");

        // identify name and desc
        int nameDescEnd = splitString.indexOf(m.endNameDesc);
        int nameDescStart = splitString.indexOf(m.prefab);

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

        // parse the description
        int descStart = dirty.substring(1).indexOf(m.prefab); // shift by 1 to avoid finding the same marker
        if (descStart != -1) {
            String descHex = dirty.substring(1).substring(descStart);
            desc = Parser.hexToAscii(descHex);
        }

        // identify abilities/properties

        // - identify boat/mount/wings/mag rider properties

        // mount
        if (splitString.contains(m.groundSpeed)) {
            int indexG = splitString.indexOf(m.groundSpeed);
            properties.put(CollectionEnums.Property.GROUND_MS, Parser.collectionH2D(splitString.substring(indexG-6, indexG-1)));
            types.add(CollectionEnums.CollectionType.MOUNT);
        }

        // wings
        if (splitString.contains(m.airSpeed) || splitString.contains(m.airSpeedA)) {
            int indexA = splitString.contains(m.airSpeed) ? splitString.indexOf(m.airSpeed) : splitString.indexOf(m.airSpeedA);
            properties.put(CollectionEnums.Property.AIR_MS, Parser.collectionH2D(splitString.substring(indexA-6, indexA-1)));
            types.add(CollectionEnums.CollectionType.WINGS);

            int indexGlide = splitString.indexOf(m.glide);
            properties.put(CollectionEnums.Property.GLIDE, Parser.collectionH2D(splitString.substring(indexGlide-6, indexGlide-1)));
        }

        // mag rider
        if (splitString.contains(m.mag)) {
            int indexM = splitString.indexOf(m.mag);
            properties.put(CollectionEnums.Property.MAG_MS, Parser.collectionH2D(splitString.substring(indexM+3, indexM+9)));
            types.add(CollectionEnums.CollectionType.MAG);
        }

        // boat
        if (splitString.contains(m.waterSpeed)) {
            types.add(CollectionEnums.CollectionType.BOAT);

            int indexW = splitString.indexOf(m.waterSpeed);
            properties.put(CollectionEnums.Property.WATER_MS, Parser.collectionH2D(splitString.substring(indexW-6, indexW-1)));

            int indexT = splitString.indexOf(m.turnRate);
            properties.put(CollectionEnums.Property.TURN_RATE, Parser.collectionH2D(splitString.substring(indexT-6, indexT-1)));

            int indexAc = splitString.indexOf("24", indexT + m.turnRate.length());
            properties.put(CollectionEnums.Property.ACCEL, Parser.collectionH2D(splitString.substring(indexAc + 9, indexAc + 15)));
        }

        // dragon
        Map<CollectionEnums.Buff, Double> dragonBuffs = parseDragon(splitString, m);
        if (!dragonBuffs.isEmpty()) {
            types.add(CollectionEnums.CollectionType.DRAGON);
        }

        // power rank
        if (splitString.contains(m.powerRank)) {
            properties.put(CollectionEnums.Property.POWER_RANK, 30.0);
        }

        // form the collection object and return
        if (types.contains(CollectionEnums.CollectionType.DRAGON)) {
            boolean isMagRider = properties.containsKey(CollectionEnums.Property.MAG_MS);
            return new Collection(name, desc, rPath, properties, dragonBuffs, isMagRider);
        }
        return new Collection(name, desc, rPath, types, properties);
    }


    private Map<CollectionEnums.Buff, Double> parseDragon(String splitString, Markers m) {

        Map<CollectionEnums.Buff, Double> buffs = new HashMap<>(10);

        // identify max health
        if (splitString.contains(m.maxHealth)) {
            statExtract(splitString, m, buffs, m.maxHealth, true, CollectionEnums.Buff.MH_PCT, CollectionEnums.Buff.MH);

            if (splitString.contains(m.maxHealth2)) {
                statExtract(splitString, m, buffs, m.maxHealth2, true, CollectionEnums.Buff.MH_PCT, CollectionEnums.Buff.MH);
            }

        }

        // identify hp regen
        if (splitString.contains(m.hpRegen)) {
            statExtract(splitString, m, buffs, m.hpRegen, true, CollectionEnums.Buff.HR_PCT, CollectionEnums.Buff.HR);
        } else if (splitString.contains(m.hpRegen2)) {
            statExtract(splitString, m, buffs, m.hpRegen2, true, CollectionEnums.Buff.HR_PCT, CollectionEnums.Buff.HR);
        }

        // identify magic damage
        if (splitString.contains(m.magicDamage)) {
            statExtract(splitString, m, buffs, m.magicDamage, false, CollectionEnums.Buff.MD, null);
        } else if (splitString.contains(m.magicDamage2)) {
            statExtract(splitString, m, buffs, m.magicDamage2, false, CollectionEnums.Buff.MD, null);
        } else if (splitString.contains(m.magicDamage3)) {
            statExtract(splitString, m, buffs, m.magicDamage3, false, CollectionEnums.Buff.MD, null);
        }

        // identify physical damage
        if (splitString.contains(m.physDamage)) {
            statExtract(splitString, m, buffs, m.physDamage, false, CollectionEnums.Buff.PD, null);
        } else if (splitString.contains(m.physDamage2)) {
            statExtract(splitString, m, buffs, m.physDamage2, false, CollectionEnums.Buff.PD, null);
        }

        // identify crit damage
        if (splitString.contains(m.critDamage)) {
            statExtract(splitString, m, buffs, m.critDamage, false, CollectionEnums.Buff.CD, null);
        } else if (splitString.contains(m.critDamage2)) {
            statExtract(splitString, m, buffs, m.critDamage2, false, CollectionEnums.Buff.CD, null);
        }

        // identify crit hit chance
        if (splitString.contains(m.critHit)) {
            statExtract(splitString, m, buffs, m.critHit, false, CollectionEnums.Buff.CH, null);
        } else if (splitString.contains(m.critHit2)) {
            statExtract(splitString, m, buffs, m.critHit2, false, CollectionEnums.Buff.CH, null);
        }

        // identify max energy
        if (splitString.contains(m.maxEnergy)) {
            statExtract(splitString, m, buffs, m.maxEnergy, false, CollectionEnums.Buff.EN, null);
        }

        // identify energy regen
        if (splitString.contains(m.energyRegen)) {
            statExtract(splitString, m, buffs, m.energyRegen, true, CollectionEnums.Buff.ER_PCT, CollectionEnums.Buff.ER);
        }

        // identify misc stats
        if (splitString.contains(m.light)) {
            statExtract(splitString, m, buffs, m.light, false, CollectionEnums.Buff.LT, null);
        }

        if (splitString.contains(m.jump)) {
            statExtract(splitString, m, buffs, m.jump, false, CollectionEnums.Buff.JP, null);
        }

        if (splitString.contains(m.laser)) {
            statExtract(splitString, m, buffs, m.laser, false, CollectionEnums.Buff.LS, null);
        }

        if (splitString.contains(m.attackSpeed)) {
            statExtract(splitString, m, buffs, m.attackSpeed, false, CollectionEnums.Buff.AS, null);
        }

        if (splitString.contains(m.magicFind)) {
            statExtract(splitString, m, buffs, m.magicFind, false, CollectionEnums.Buff.MF, null);
        } else if (splitString.contains(m.magicFind2)) {
            statExtract(splitString, m, buffs, m.magicFind2, false, CollectionEnums.Buff.MF, null);
        }

        return buffs;
    }

    private void statExtract(String splitString, Markers m, Map<CollectionEnums.Buff, Double> buffs, String type, boolean options, CollectionEnums.Buff stat1, CollectionEnums.Buff stat2) {

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
