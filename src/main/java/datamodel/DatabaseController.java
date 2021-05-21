package datamodel;

import datamodel.objects.*;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.sqlite.SQLiteConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class DatabaseController {

    Connection con;
    SQLQuerier querier;
    SQLModifier modifier;

    public DatabaseController() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the database with the Trove schema specified at ddlPath.
     * <p>
     * Additionally inserts "en" into the language table as a default language.
     *
     * @param path    desired path and file name of the database
     * @param ddlPath the path of the schema
     */
    public void createDatabase(String path, String ddlPath) {
        try {
            String ddl = String.join(" ", Files.readAllLines(Paths.get(ddlPath)));
            con = DriverManager.getConnection("jdbc:sqlite:" + path);
            querier = new SQLQuerier(con);
            modifier = new SQLModifier(con);

            Statement statement = con.createStatement();

            for (String sql : ddl.split("--GO")) {
//                System.out.println(sql);
                statement.executeUpdate(sql);
            }
            modifier.insertLanguage("en");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadDatabase(String path) throws SQLException {
        path = path.replace("\\", "/");

        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);

        con = DriverManager.getConnection("jdbc:sqlite:" + path, config.toProperties());
        querier = new SQLQuerier(con);
        modifier = new SQLModifier(con);
    }

    // GETTERS

    ObservableMap<String, ObservableBench> getBenches() throws SQLException {
        return querier.getBenches();
    }

    ObservableMap<String, ObservableCollection> getCollections(String language) throws SQLException {
        return querier.getCollections(language);
    }

    ObservableMap<String, ObservableItem> getItems(String language) throws SQLException {
        return querier.getItems(language);
    }

    ObservableMap<String, ObservablePlaceable> getPlaceables(String language) throws SQLException {
        return querier.getPlaceables(language);
    }

    ObservableMap<String, ObservableRecipe> getRecipes() throws SQLException {
        return querier.getRecipes();
    }

    Map<String, ObservableStrings> getStrings(String language) throws SQLException {
        return querier.getStrings(language);
    }

    // SETTERS

    void updateDatabase(Map<String, ObservableBench> benches, Map<String, ObservableCollection> collections,
                        Map<String, ObservableItem> items, Map<String, ObservablePlaceable> placeables,
                        Map<String, ObservableRecipe> recipes, Map<String, ObservableStrings> strings, String language) throws SQLException {
        // there is a order to insertion: first we insert strings, then we insert recipes, then everything else
        // we insert strings to prevent non-existent references to string identifiers, then as benches depend on
        // recipes paths, we insert recipes before it, then we can insert everything else
        upsertStrings(strings, language);
        upsertRecipes(recipes);
        upsertBenches(benches);
        upsertCollections(collections, language);
        upsertItems(items, language);
        upsertPlaceables(placeables, language);
    }

    /**
     * Updates/inserts the benches in the database with the input data.
     *
     * @param benches a Map containing updated/new ObservableBenches, with the relative path of the bench as the key
     */
    private void upsertBenches(Map<String, ObservableBench> benches) throws SQLException {
        for (Map.Entry<String, ObservableBench> entry : benches.entrySet()) {
            String rPath = entry.getKey();
            ObservableBench bench = entry.getValue();

            String name = bench.getName();
            String professionName = bench.getProfessionName();
            Map<List<String>, ObservableList<String>> categories = bench.getCategories();

            // if there is no first row, then it's not in the database, so we just insert
            if (!querier.hasBench(rPath)) {
                // insert the bench
                modifier.insertBench(rPath, name);
                // insert the bench categories
                for (List<String> key : categories.keySet()) {
                    String catID = key.get(0);
                    int benchIndex = Integer.valueOf(key.get(0), 10);   // could be an issue if not a number
                    modifier.insertBenchCategory(rPath, catID, benchIndex);
                }
            }

            // update bench profession if applicable
            if (!professionName.equals("N/A")) {
                modifier.setBenchProfession(rPath, professionName);
            }

            // update the recipes
            for (Map.Entry<List<String>, ObservableList<String>> category : categories.entrySet()) {
                String key = category.getKey().get(0);
                for (String path : category.getValue()) {
                    modifier.setRecipeBench(path, name);
                    modifier.setRecipeCategory(path, key);
                }
            }
        }
    }

    private void upsertCollections(Map<String, ObservableCollection> collections, String notesLanguage) throws SQLException {
        for (Map.Entry<String, ObservableCollection> entry : collections.entrySet()) {
            String rPath = entry.getKey();
            ObservableCollection collection = entry.getValue();

            // insert if not present, else update mastery
            if (!querier.hasCollection(rPath)) {
                modifier.insertCollection(rPath, collection.getTroveMR(), collection.getGeodeMR());
            } else {
                modifier.setCollectionTroveMastery(rPath, collection.getTroveMR());
                modifier.setCollectionGeodeMastery(rPath, collection.getGeodeMR());
            }

            modifier.setCollectionName(rPath, collection.getName());
            modifier.setCollectionDesc(rPath, collection.getDesc());

            for (String noteID : collection.getNotes()) {
                insertNotes(rPath, notesLanguage, noteID);
            }

            for (CollectionEnums.Type type : collection.getTypes()) {
                insertCollectionType(rPath, type);
            }

            for (Map.Entry<CollectionEnums.Property, Double> prop : collection.getProperties().entrySet()) {
                upsertCollectionProperty(rPath, prop.getKey(), prop.getValue());
            }

            for (Map.Entry<CollectionEnums.Buff, Double> buff : collection.getBuffs().entrySet()) {
                upsertCollectionBuff(rPath, buff.getKey(), buff.getValue());
            }

        }
    }

    private void upsertItems(Map<String, ObservableItem> items, String notesLanguage) throws SQLException {
        for (Map.Entry<String, ObservableItem> entry : items.entrySet()) {
            String rPath = entry.getKey();
            ObservableItem item = entry.getValue();

            if (!querier.hasItem(rPath)) {
                modifier.insertItem(rPath);
            }

            modifier.setItemName(rPath, item.getName());
            modifier.setItemDesc(rPath, item.getDesc());
            modifier.setItemTradable(rPath, item.isTradable());

            for (String noteID : item.getNotes()) {
                insertNotes(rPath, notesLanguage, noteID);
            }

            for (String unlock : item.getUnlocks()) {
                insertUnlock(rPath, unlock);
            }

            for (Map.Entry<String, Integer> decon : item.getDecons().entrySet()) {
                upsertDecon(rPath, decon.getKey(), decon.getValue());
            }

            for (Map.Entry<String, String> loot : item.getLootCommon().entrySet()) {
                upsertLoot(rPath, "COMMON", loot.getKey(), loot.getValue());
            }

            for (Map.Entry<String, String> loot : item.getLootUncommon().entrySet()) {
                upsertLoot(rPath, "UNCOMMON", loot.getKey(), loot.getValue());
            }

            for (Map.Entry<String, String> loot : item.getLootRare().entrySet()) {
                upsertLoot(rPath, "RARE", loot.getKey(), loot.getValue());
            }
        }
    }

    private void upsertPlaceables(Map<String, ObservablePlaceable> placeables, String notesLanguage) throws SQLException {
        for (Map.Entry<String, ObservablePlaceable> entry : placeables.entrySet()) {
            String rPath = entry.getKey();
            ObservablePlaceable placeable = entry.getValue();

            if (!querier.hasPlaceable(rPath)) {
                modifier.insertPlaceable(rPath);
            }

            modifier.setPlaceableName(rPath, placeable.getName());
            modifier.setPlaceableDesc(rPath, placeable.getDesc());
            modifier.setPlaceableTradable(rPath, placeable.isTradable());

            for (String noteID : placeable.getNotes()) {
                insertNotes(rPath, notesLanguage, noteID);
            }
        }
    }

    private void upsertRecipes(Map<String, ObservableRecipe> recipes) throws SQLException {
        for (Map.Entry<String, ObservableRecipe> entry : recipes.entrySet()) {
            String rPath = entry.getKey();
            ObservableRecipe recipe = entry.getValue();

            if (!querier.hasRecipe(rPath)) {
                modifier.insertRecipe(rPath, recipe.getName());
            }

            for (Map.Entry<String, Integer> cost : recipe.getCosts().entrySet()) {
                upsertRecipeCost(rPath, cost.getKey(), cost.getValue());
            }

            for (Map.Entry<String, Integer> output : recipe.getOutput().entrySet()) {
                upsertRecipeOutput(rPath, output.getKey(), output.getValue());
            }
        }
    }

    private void upsertStrings(Map<String, ObservableStrings> strings, String language) throws SQLException {
        ObservableStrings extractedStrings = strings.get("extracted");
        ObservableStrings customStrings = strings.get("custom");

        if (extractedStrings != null) {
            for (Map.Entry<String, String> entry : extractedStrings.getStrings().entrySet()) {
                upsertString(entry.getKey(), entry.getValue(), language, false);
            }
        }

        if (customStrings != null) {
            for (Map.Entry<String, String> entry : customStrings.getStrings().entrySet()) {
                upsertString(entry.getKey(), entry.getValue(), language, true);
            }
        }
    }

    /**
     * Inserts the note into the database. Does nothing if the noteID is already in the database.
     *
     * @param rPath    the relative path the note is associated with
     * @param language the language of this note being inserted
     * @param noteID   the unique string identifier of this ntoe
     * @throws SQLException if something bad happens
     */
    private void insertNotes(String rPath, String language, String noteID) throws SQLException {
        // if true, does not exist, so insert, else do nothing since noteID are fixed.
        if (!querier.hasNote(rPath)) modifier.insertNote(rPath, noteID);
    }

    // SETTER HELPERS

    private void insertCollectionType(String rPath, CollectionEnums.Type type) throws SQLException {
        if (!querier.hasCollectionType(rPath, type.name().toUpperCase()))
            modifier.insertCollectionType(rPath, type.name().toUpperCase());
    }

    private void upsertCollectionProperty(String rPath, CollectionEnums.Property prop, double value) throws SQLException {
        if (!querier.hasCollectionProperty(rPath, prop.name().toUpperCase())) {
            modifier.insertCollectionProperty(rPath, prop.name().toUpperCase(), value);
        } else {
            modifier.setCollectionProperty(rPath, prop.name().toUpperCase(), value);
        }
    }

    private void upsertCollectionBuff(String rPath, CollectionEnums.Buff buff, double value) throws SQLException {
        if (!querier.hasCollectionBuff(rPath, buff.name().toUpperCase())) {
            modifier.insertCollectionBuff(rPath, buff.name().toUpperCase(), value);
        } else {
            modifier.setCollectionBuff(rPath, buff.name().toUpperCase(), value);
        }
    }

    private void insertUnlock(String itemRPath, String colRPath) throws SQLException {
        if (!querier.hasUnlock(itemRPath, colRPath)) modifier.insertUnlock(itemRPath, colRPath);
    }

    private void upsertDecon(String inputRPath, String outputRPath, int quantity) throws SQLException {
        if (!querier.hasDecon(inputRPath, outputRPath)) {
            modifier.insertDecon(inputRPath, outputRPath, quantity);
        } else {
            modifier.setDecon(inputRPath, outputRPath, quantity);
        }
    }

    private void upsertLoot(String inputRPath, String rarity, String outputRPath, String quantity) throws SQLException {
        if (!querier.hasLoot(inputRPath, rarity, outputRPath)) {
            modifier.insertLootbox(inputRPath, rarity, outputRPath, quantity);
        } else {
            modifier.setLootbox(inputRPath, rarity, outputRPath, quantity);
        }
    }

    private void upsertRecipeCost(String rPath, String inputRPath, int quantity) throws SQLException {
        if (!querier.hasRecipeCost(rPath, inputRPath)) {
            modifier.setRecipeCost(rPath, inputRPath, quantity);
        } else {
            modifier.insertRecipeCost(rPath, inputRPath, quantity);
        }
    }

    private void upsertRecipeOutput(String rPath, String outputRPath, int quantity) throws SQLException {
        if (!querier.hasRecipeOutput(rPath, outputRPath)) {
            modifier.setRecipeOutput(rPath, outputRPath, quantity);
        } else {
            modifier.insertRecipeOutput(rPath, outputRPath, quantity);
        }
    }

    private void upsertString(String id, String content, String lang, boolean isCustom) throws SQLException {
        boolean hasString = querier.hasString(id, isCustom);
        if (!hasString) {
            modifier.insertString(lang, id, content, isCustom);
        } else {
            modifier.setString(lang, id, content, isCustom);
        }
    }
}
