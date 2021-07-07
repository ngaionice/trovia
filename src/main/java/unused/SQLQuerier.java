//package unused;
//
//import datamodel.Enums;
//import datamodel.objects.*;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableMap;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class SQLQuerier {
//
//    Connection con;
//
//    SQLQuerier(Connection con) {
//        this.con = con;
//    }
//
//    // QUERIES - SESSION STATE
//    // just return each group in full here
//
//    // note that the recipe-bench links are only shown in benches, and not in recipes.
//    // this is to avoid potential conflicting changes
//
//    ObservableMap<String, Bench> getBenches() throws SQLException {
//        // rPath of bench as key
//        ObservableMap<String, Bench> benches = FXCollections.observableHashMap();
//        ResultSet baseQuery = con.createStatement().executeQuery("select * from Bench");
//
//        // iterate through all benches
//        while (baseQuery.next()) {
//            String rPath = baseQuery.getString(1);
//            String nameID = baseQuery.getString(2);
//            String professionName = baseQuery.getString(3); // note that this can be null
//
//            PreparedStatement ps = con.prepareStatement("select (name_id, bench_index) from BenchCategory BC where BC.rel_path = ?");
//            ps.setString(1, rPath);
//            ResultSet categories = ps.executeQuery();
//
//            Map<String[], List<String>> benchCats = new HashMap<>();
//
//            // iterate through this bench's categories and get their recipes' rPaths
//            // note that some recipes show up in multiple benches
//            while (categories.next()) {
//                String catID = categories.getString(1);
//                String benchIndex = String.valueOf(categories.getInt(2));
//
//                PreparedStatement psRecipes = con.prepareStatement("select rel_path from Recipe where cat_id = ?");
//                psRecipes.setString(1, catID);
//                ResultSet rsRecipes = psRecipes.executeQuery();
//
//                List<String> recipes = new ArrayList<>();
//                while (rsRecipes.next()) {
//                    recipes.add(rsRecipes.getString(1));
//                }
//
//                benchCats.put(new String[]{catID, benchIndex}, recipes);
//            }
//
//            benches.put(rPath, new Bench(nameID, rPath, benchCats, professionName));
//        }
//        return benches;
//    }
//
//    ObservableMap<String, Collection> getCollections(String language) throws SQLException {
//        ObservableMap<String, Collection> collections = FXCollections.observableHashMap();
//        ResultSet baseQuery = con.createStatement().executeQuery("select * from Collection");
//
//        while (baseQuery.next()) {
//            String rPath = baseQuery.getString(1);
//            String nameID = baseQuery.getString(2);
//            String descID = baseQuery.getString(3);
//            int troveMR = baseQuery.getInt(4);
//            int geodeMR = baseQuery.getInt(5);
//
//            PreparedStatement psTypes = con.prepareStatement("select type from CollectionType where rel_path = ?");
//            psTypes.setString(1, rPath);
//            ResultSet rsTypes = psTypes.executeQuery();
//
//            List<Enums.Type> types = new ArrayList<>();
//            while (rsTypes.next()) {
//                types.add(Enums.Type.valueOf(rsTypes.getString(1)));
//            }
//
//            PreparedStatement psProps = con.prepareStatement("select prop, prop_val from CollectionProperty where rel_path = ?");
//            psProps.setString(1, rPath);
//            ResultSet rsProps = psProps.executeQuery();
//
//            Map<Enums.Property, Double> props = new HashMap<>();
//            while (rsProps.next()) {
//                props.put(Enums.Property.valueOf(rsProps.getString(1)), rsProps.getDouble(2));
//            }
//
//            PreparedStatement psBuffs = con.prepareStatement("select buff, buff_val from CollectionBuff where rel_path = ?");
//            psBuffs.setString(1, rPath);
//            ResultSet rsBuffs = psBuffs.executeQuery();
//
//            Map<Enums.Buff, Double> buffs = new HashMap<>();
//            while (rsBuffs.next()) {
//                buffs.put(Enums.Buff.valueOf(rsBuffs.getString(1)), rsBuffs.getDouble(2));
//            }
//
//            PreparedStatement psNotes = con.prepareStatement("select string_id from Notes where rel_path = ?");
//            psNotes.setString(1, rPath);
//            ResultSet rsNotes = psNotes.executeQuery();
//
//            List<String> notes = new ArrayList<>();
//            while (rsNotes.next()) {
//                notes.add(rsNotes.getString(1));
//            }
//
//            collections.put(rPath, new Collection(nameID, descID, rPath, troveMR, geodeMR, types, props, buffs));
//        }
//        return collections;
//    }
//
//    ObservableMap<String, Item> getItems(String language) throws SQLException {
//        ObservableMap<String, Item> items = FXCollections.observableHashMap();
//        ResultSet baseQuery = con.createStatement().executeQuery("select * from Item");
//
//        while (baseQuery.next()) {
//            String rPath = baseQuery.getString(1);
//            String nameID = baseQuery.getString(2);
//            String descID = baseQuery.getString(3);
//            int tradable = baseQuery.getInt(4);
//
//            PreparedStatement psUnlocks = con.prepareStatement("select col_rel from Unlock where item_rel = ?");
//            psUnlocks.setString(1, rPath);
//            ResultSet rsUnlocks = psUnlocks.executeQuery();
//
//            List<String> unlocks = new ArrayList<>();
//            while (rsUnlocks.next()) {
//                unlocks.add(rsUnlocks.getString(1));
//            }
//
//            PreparedStatement psLootbox = con.prepareStatement("select rarity, output_rel, output_count from Lootbox where rel_path = ?");
//            psLootbox.setString(1, rPath);
//            ResultSet rsLootbox = psLootbox.executeQuery();
//
//            Map<String, String> common = new HashMap<>();
//            Map<String, String> uncommon = new HashMap<>();
//            Map<String, String> rare = new HashMap<>();
//            while (rsLootbox.next()) {
//                String rarity = rsLootbox.getString(1);
//                String outputRPath = rsLootbox.getString(2);
//                String outputCount = rsLootbox.getString(3);
//                if (rarity.equals("COMMON")) {
//                    common.put(outputRPath, outputCount);
//                } else if (rarity.equals("UNCOMMON")) {
//                    uncommon.put(outputRPath, outputCount);
//                } else {
//                    rare.put(outputRPath, outputCount);
//                }
//            }
//
//            PreparedStatement psDecon = con.prepareStatement("select output_rel, output_count from Decon where input_rel = ?");
//            psDecon.setString(1, rPath);
//            ResultSet rsDecon = psDecon.executeQuery();
//
//            Map<String, Integer> decons = new HashMap<>();
//            while (rsDecon.next()) {
//                String outputRPath = rsDecon.getString(1);
//                int outputCount = rsDecon.getInt(2);
//                decons.put(outputRPath, outputCount);
//            }
//
//            PreparedStatement psNotes = con.prepareStatement("select string_id from Notes where rel_path = ?");
//            psNotes.setString(1, rPath);
//            ResultSet rsNotes = psNotes.executeQuery();
//
//            List<String> notes = new ArrayList<>();
//            while (rsNotes.next()) {
//                notes.add(rsNotes.getString(1));
//            }
//
//            boolean isTradable = tradable == 1;
//
//            items.put(rPath, new Item(nameID, descID, rPath, unlocks.toArray(new String[0]), decons, common, uncommon, rare, isTradable));
//        }
//        return items;
//    }
//
//    ObservableMap<String, Placeable> getPlaceables(String language) throws SQLException {
//        ObservableMap<String, Placeable> placeables = FXCollections.observableHashMap();
//        ResultSet baseQuery = con.createStatement().executeQuery("select * from Placeable");
//
//        while (baseQuery.next()) {
//            String rPath = baseQuery.getString(1);
//            String nameID = baseQuery.getString(2);
//            String descID = baseQuery.getString(3);
//            int tradable = baseQuery.getInt(4);
//
//            PreparedStatement psNotes = con.prepareStatement("select string_id from Notes where rel_path = ?");
//            psNotes.setString(1, rPath);
//            ResultSet rsNotes = psNotes.executeQuery();
//
//            List<String> notes = new ArrayList<>();
//            while (rsNotes.next()) {
//                notes.add(rsNotes.getString(1));
//            }
//
//            boolean isTradable = tradable == 1;
//            placeables.put(rPath, new Placeable(nameID, descID, rPath, notes, isTradable));
//        }
//        return placeables;
//    }
//
//    ObservableMap<String, Recipe> getRecipes() throws SQLException {
//        ObservableMap<String, Recipe> recipes = FXCollections.observableHashMap();
//        ResultSet baseQuery = con.createStatement().executeQuery("select rel_path, name from Recipe");
//
//        while (baseQuery.next()) {
//            String rPath = baseQuery.getString(1);
//            String name = baseQuery.getString(2);
//
//            PreparedStatement psCosts = con.prepareStatement("select input_rel, input_count from RecipeCost where rel_path = ?");
//            psCosts.setString(1, rPath);
//            ResultSet rsCosts = psCosts.executeQuery();
//
//            Map<String, Integer> costs = new HashMap<>();
//            while (rsCosts.next()) {
//                costs.put(rsCosts.getString(1), rsCosts.getInt(2));
//            }
//
//            PreparedStatement psOutput = con.prepareStatement("select output_rel, output_count from RecipeOutput where rel_path = ?");
//            psOutput.setString(1, rPath);
//            ResultSet rsOutput = psOutput.executeQuery();
//
//            Map<String, Integer> output = new HashMap<>();
//            while (rsOutput.next()) {
//                output.put(rsOutput.getString(1), rsOutput.getInt(2));
//            }
//
//            recipes.put(rPath, new Recipe(name, rPath, costs, output));
//        }
//        return recipes;
//    }
//
//    /**
//     * Returns a map containing 2 ObservableStrings, with keys "extracted" and "custom" respectively.
//     * <p>
//     * The value associated with "extracted" contains strings that originated from the game, while the value
//     * associated with "custom" contains strings that were added in manually.
//     *
//     * @return a Map with 2 keys, "extracted" and "custom"
//     */
//    Map<String, Strings> getStrings(String language) throws SQLException {
//        Map<String, Strings> strings = new HashMap<>();
//        PreparedStatement extractedQuery = con.prepareStatement("select id, content from ExtractedString where lang = ?");
//        PreparedStatement customQuery = con.prepareStatement("select id, content from CustomString where lang = ?");
//        extractedQuery.setString(1, language);
//        customQuery.setString(1, language);
//        ResultSet extracted = extractedQuery.executeQuery();
//        ResultSet custom = customQuery.executeQuery();
//
//        Map<String, String> extractedStrings = new HashMap<>();
//        Map<String, String> customStrings = new HashMap<>();
//
//        getStringsHelper(extracted, extractedStrings);
//        getStringsHelper(custom, customStrings);
//
//        strings.put("extracted", new Strings("extracted", extractedStrings));
//        strings.put("custom", new Strings("custom", customStrings));
//
//        return strings;
////            Map<String, ObservableStrings> strings = new HashMap<>();
////            Map<String[], String> extractedStrings = new HashMap<>();
////            Map<String[], String> customStrings = new HashMap<>();
////            strings.put("extracted", new ObservableStrings(extractedStrings));
////            strings.put("custom", new ObservableStrings(customStrings));
////            return strings;
//    }
//
//    boolean hasBench(String rPath) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from Bench where rel_path = ?");
//        checkQuery.setString(1, rPath);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasCollection(String rPath) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from Collection where rel_path = ?");
//        checkQuery.setString(1, rPath);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasItem(String rPath) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from Item where rel_path = ?");
//        checkQuery.setString(1, rPath);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasPlaceable(String rPath) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from Placeable where rel_path = ?");
//        checkQuery.setString(1, rPath);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasRecipe(String rPath) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from Recipe where rel_path = ?");
//        checkQuery.setString(1, rPath);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasString(String id, boolean isCustom) throws SQLException {
//        PreparedStatement checkQuery;
//        if (!isCustom) {
//            checkQuery = con.prepareStatement("select * from ExtractedString where id = ?");
//        } else {
//            checkQuery = con.prepareStatement("select * from CustomString where id = ?");
//        }
//        checkQuery.setString(1, id);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasCollectionType(String rPath, String type) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from CollectionType where rel_path = ? and type = ?");
//        checkQuery.setString(1, rPath);
//        checkQuery.setString(2, type);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasCollectionProperty(String rPath, String prop) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from CollectionProperty where rel_path = ? and prop = ?");
//        checkQuery.setString(1, rPath);
//        checkQuery.setString(2, prop);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasCollectionBuff(String rPath, String buff) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from CollectionBuff where rel_path = ? and buff = ?");
//        checkQuery.setString(1, rPath);
//        checkQuery.setString(2, buff);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasNote(String rPath) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from Notes where rel_path = ?");
//        checkQuery.setString(1, rPath);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasUnlock(String itemRPath, String colRPath) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from Unlock where item_rel = ? and col_rel = ?");
//        checkQuery.setString(1, itemRPath);
//        checkQuery.setString(2, colRPath);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasDecon(String inputRPath, String outputRPath) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from Decon where input_rel = ? and output_rel = ?");
//        checkQuery.setString(1, inputRPath);
//        checkQuery.setString(2, outputRPath);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasLoot(String inputRPath, String rarity, String outputRPath) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from Lootbox where rel_path = ? and output_rel = ? and rarity = ?");
//        checkQuery.setString(1, inputRPath);
//        checkQuery.setString(2, outputRPath);
//        checkQuery.setString(3, rarity);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasRecipeCost(String rPath, String inputRPath) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from RecipeCost where rel_path = ? and input_rel = ?");
//        checkQuery.setString(1, rPath);
//        checkQuery.setString(2, inputRPath);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    boolean hasRecipeOutput(String rPath, String outputRPath) throws SQLException {
//        PreparedStatement checkQuery = con.prepareStatement("select * from RecipeOutput where rel_path = ? and output_rel = ?");
//        checkQuery.setString(1, rPath);
//        checkQuery.setString(2, outputRPath);
//        ResultSet queryOutput = checkQuery.executeQuery();
//
//        return queryOutput.isBeforeFirst();
//    }
//
//    private void getStringsHelper(ResultSet stringSet, Map<String, String> map) throws SQLException {
//        while (stringSet.next()) {
//            String id = stringSet.getString(1);
//            String content = stringSet.getString(2);
//
//            map.put(id, content);
//        }
//    }
//
//
//    // QUERIES - API
//}
