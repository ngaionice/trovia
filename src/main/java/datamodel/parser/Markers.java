package datamodel.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Markers {

    // general
    public String alphabetPrefab =      "[6][1-9A-F]|[7][0-9A]|5F|[3][0-9]|24"; // for identifying name and desc; lowercase alphabet, digits, underscore, and dollar sign
    public String alphabetCollection =  "[6][1-9A-F]|[7][0-9A]|5F|[3][0-9]|2[4F]"; // same as above, + forward slash
    public String alphabetRecipeRPath = "[6][1-9A-F]|[7][0-9A]|5F|[3][0-9]|2F"; // same as above, without dollar sign
    public String alphabetRecipeRPathExtended = "[4][1-9A-F]|[5][0-9A]|[6][1-9A-F]|[7][0-9A]|5F|[3][0-9]|2F";
    public String alphabetRecipe =      "[6][1-9A-F]|[7][0-9A]|5[0-9AF]|[3][0-9]|4[1-9A-F]|"; // lowercase alphabet, digits and underscore
    public String alphabetLowerCase =   "[6][1-9A-F]|[7][0-9A]";  // lowercase alphabet only
    public String alphabetBothCases =   "[4][1-9A-F]|[5][0-9A]|[6][1-9A-F]|[7][0-9A]";
    public String alphabetExtended = "0A|[4][1-9A-F]|[5][0-9A]|[6][0-9A-F]|[7][0-9A]|2[017CE]|3[AF]|5[CF]";
    public String endNameDesc = " 68 00 80";
    public String endFile = ".binfab";

    // name markers
    public String namePrefix = "62 (\\w\\w\\s){3,4} ";
    public String prefab = "24 70 72 65 66 61 62 73 ";                 // $prefabs
    public String prefabSpaced = " 24 70 72 65 66 61 62 73 ";          // $prefabs (with space in the front)
    public String recipe = "72 65 63 69 70 65 ";                       // recipe
    public String crafting = "24 43 72 61 66 74 69 6E 67 ";            // $Crafting

    // item markers
    public String lootbox = "4C 6F 6F 74 54 61 62 6C 65";              // LootTable
    public String collection = "63 6F 6C 6C 65 63 74 69 6F 6E 73 2F";  // collections/
    public String decay = "71 75 61 6E 74 69 74 79 64 65 63 61 79";    // quantitydecay

    // item: blueprint filters
    public List<String> itemBpFilters = new ArrayList<>(Arrays.asList("quantitydecay", "recall_begin", "AP_r_hand"));

    // collection markers

    public String groundSpeed = "38 10 67 72 6F 75 6E 64 5F 6D 6F 76 65 73 70 65 65 64 46"; // 8ground_movespeedF
    public String airSpeed = "38 0E 77 69 6E 67 5F 6D 6F 76 65 73 70 65 65 64 46";          // 8wing_movespeedF
    public String glide = "38 0F 67 6C 69 64 65 5F 6D 6F 76 65 73 70 65 65 64 46";          // 8glide_movespeedF
    public String airSpeedA = "38 1C 79 65 6C 6C 6F 77 5F 64 72 61 67 6F 6E 5F 77 69 6E 67 5F 6D 6F 76 65 73 70 65 65 64 46"; // 8yellow_dragon_wing_movespeedF

    public String waterSpeed = "38 00 46 00 00 00 00 00 00 00 00 50 06 1E 14 00 28 10";
    public String turnRate =   "38 00 46 00 00 00 00 00 00 00 00 50 06 1E 24 00 26 10";
    public String accel = "1E 08 1E 08";

    public String mag = "24 00 00 C8 41 38 00 46";

    public String powerRank = "70 6F 77 65 72 72 61 6E 6B";            // powerrank

    // dragon markers

    public String maxHealth = "6D 61 78 68 65 61 6C 74 68 46";
    public String maxHealth2 = "6D 61 78 68 65 61 6C 74 68 32 46";

    public String hpRegen = "68 65 61 6C 74 68 72 65 67 65 6E 46";
    public String hpRegen2 = "68 70 72 65 67 65 6E 46";

    public String magicDamage = "73 70 65 6C 6C 64 61 6D 61 67 65 46";
    public String magicDamage2 = "64 61 6D 61 67 65 73 70 65 6C 6C 46";
    public String magicDamage3 = "6D 61 67 69 63 64 61 6D 61 67 65 46";

    public String physDamage = "70 68 79 73 69 63 61 6C 64 61 6D 61 67 65 46";
    public String physDamage2 = "64 61 6D 61 67 65 70 68 79 73 46";

    public String critDamage = "63 72 69 74 68 69 74 64 61 6D 61 67 65 46";
    public String critDamage2 = "63 72 69 74 68 69 74 64 6D 67 46";

    public String critHit = "63 72 69 74 63 68 61 6E 63 65 46";
    public String critHit2 = "63 72 69 74 68 69 74 63 68 61 6E 63 65 46";

    public String magicFind = "6D 61 67 69 63 66 69 6E 64 46";
    public String magicFind2 = "6D 66 46";

    public String maxEnergy = "6D 61 78 65 6E 65 72 67 79 46";

    public String energyRegen = "65 6E 65 72 67 79 72 65 67 65 6E 46";

    public String light = "6C 69 67 68 74 46";
    public String jump = "6A 75 6D 70 46";
    public String laser = "6D 69 6E 69 6E 67 46";
    public String attackSpeed = "61 74 74 61 63 6B 73 70 65 65 64 46";

    public String percent = "CD CC";
}
