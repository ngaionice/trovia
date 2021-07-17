package datamodel.parser.parsestrategies;

import datamodel.Enums;
import datamodel.objects.Article;
import datamodel.objects.Collection;
import datamodel.parser.Markers;
import datamodel.parser.Parser;
import datamodel.parser.Regexes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

public class ParseCollection implements ParseStrategy {

    @Override
    public Article parseObject(String splitString, String absPath, boolean useRPath) throws ParseException {
        try {
            // obtain relative path
            String rPath = useRPath ? Parser.extractRPath(absPath) : absPath.replace("\\", "/");

            // filter out things we don't want to read
            if (absPath.contains("\\dev_")) {
                throw new ParseException(rPath + " is a dev mount and is likely to be buggy.");
            } else if (absPath.contains("\\cannon_")) {
                throw new ParseException(rPath + " is a cannon and thus not a collection.");
            } else if (absPath.contains("_bobber.binfab") || absPath.contains("_cannon.binfab")) {
                throw new ParseException(rPath + " is part of a fishing rod or a cannon, and thus not a collection.");
            } else if (absPath.contains("_explosion.binfab") || absPath.contains("_projectile.binfab")) {
                throw new ParseException(rPath + " is an explosion or a projectile, and thus not a collection.");
            }

            // instantiate markers
            Markers m = new Markers();
            Regexes r = new Regexes();

            // instantiate variables
            String name, desc;
            List<Enums.Type> types = new ArrayList<>();
            Map<Enums.Property, Double> properties = new HashMap<>(10);

            // identify name and desc paths
            Pattern ndp = Pattern.compile(r.nameDescExtractor);
            Pattern bp = Pattern.compile(r.blueprintExtractor);

            int ndEnd = splitString.indexOf("68 00 80");
            if (ndEnd == -1) {
                throw new ParseException(rPath + " did not have an end marker.");
            }

            Matcher ndm = ndp.matcher(splitString.substring(0, ndEnd + 8));
            if (!ndm.find()) {
                throw new ParseException(rPath + " did not match the pattern for name and description; does it satisfy the assumptions?");
            }
            int nLen = Integer.parseInt(ndm.group(1), 16);
            name = Parser.hexToAscii(ndm.group(2).length() <= 3 * nLen ? ndm.group(2) : ndm.group(2).substring(0, 3 * nLen));

            if (ndm.group(5).equals("00 ")) {
                desc = null;
            } else {
                int dLen = Integer.parseInt(ndm.group(6), 16);
                desc = Parser.hexToAscii(ndm.group(7).length() <= 3 * dLen ? ndm.group(7) : ndm.group(7).substring(0, 3 * dLen));
            }

            // blueprint extraction
            String blueprint = null;

            Matcher bm = bp.matcher(splitString);
            if (bm.find()) {
                blueprint = Parser.hexToAscii(bm.group(2)).replace(".blueprint", "");
            }

            // identify abilities/properties

            // mount
            if (splitString.contains(m.groundSpeed)) {
                int indexG = splitString.indexOf(m.groundSpeed);
                properties.put(Enums.Property.GROUND_MS, Parser.collectionH2D(splitString.substring(indexG - 6, indexG - 1)));
                types.add(Enums.Type.MOUNT);
            }

            // wings
            if (splitString.contains(m.airSpeed) || splitString.contains(m.airSpeedA)) {
                int indexA = splitString.contains(m.airSpeed) ? splitString.indexOf(m.airSpeed) : splitString.indexOf(m.airSpeedA);
                int indexGlide = splitString.indexOf(m.glide);

                if (indexA != -1 && indexGlide != -1) {
                    properties.put(Enums.Property.AIR_MS, Parser.collectionH2D(splitString.substring(indexA - 6, indexA - 1)));
                    types.add(Enums.Type.WINGS);

                    properties.put(Enums.Property.GLIDE, Parser.collectionH2D(splitString.substring(indexGlide - 6, indexGlide - 1)));
                } else {
                    throw new ParseException(rPath + ": incomplete wings property identified.");
                }
            }

            // mag rider
            if (splitString.contains(m.mag)) {
                int indexM = splitString.indexOf(m.mag);
                properties.put(Enums.Property.MAG_MS, Parser.collectionH2D(splitString.substring(indexM + 3, indexM + 9)));
                types.add(Enums.Type.MAG);
            }

            // boat
            if (splitString.contains(m.waterSpeed)) {
                types.add(Enums.Type.BOAT);

                int indexW = splitString.indexOf(m.waterSpeed);
                properties.put(Enums.Property.WATER_MS, Parser.collectionH2D(splitString.substring(indexW - 6, indexW - 1)));

                int indexT = splitString.indexOf(m.turnRate);
                properties.put(Enums.Property.TURN_RATE, Parser.collectionH2D(splitString.substring(indexT - 6, indexT - 1)));

                int indexAc = splitString.indexOf("24", indexT + m.turnRate.length());
                properties.put(Enums.Property.ACCEL, Parser.collectionH2D(splitString.substring(indexAc + 9, indexAc + 15)));
            }

            // dragon
            Map<Enums.Buff, Double> dragonBuffs = parseBuffs(splitString, m);
            if (!dragonBuffs.isEmpty() && types.contains(Enums.Type.MOUNT)) {
                types.add(Enums.Type.DRAGON);
            }

            // power rank
            if (splitString.contains(m.powerRank)) {
                dragonBuffs.put(Enums.Buff.PR, 30.0);
            }

            return new Collection(name, desc, rPath, 0, 0, blueprint, types, properties, dragonBuffs);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
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
}
