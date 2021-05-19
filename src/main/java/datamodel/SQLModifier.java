package datamodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLModifier {

    Connection con;

    SQLModifier(Connection con) {
        this.con = con;
    }

    // NOTE:
    // anything that uses rPath or IDs requires them to be inserted into Article/Object/Extracted(or Custom)StringID first
    // otherwise, you will get a SQLException

    // INSERTS AND UPDATES

    private void insertArticle(String rPath) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into Article (rel_path, updated) values (?, datetime())");
        ps.setString(1, rPath);
        ps.executeUpdate();
    }

    private void insertObject(String rPath) throws SQLException {
        insertArticle(rPath);
        PreparedStatement ps = con.prepareStatement("insert into Object (rel_path, updated) values (?, datetime())");
        ps.setString(1, rPath);
        ps.executeUpdate();
    }

    /**
     * Should only be used when creating the database for the first time, or when additional languages are being added.
     *
     * @param lang the language code being inserted
     */
    void insertLanguage(String lang) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into Language (code, updated) values (?, datetime())");
        ps.setString(1, lang);
        ps.executeUpdate();
    }

    private void insertStringID(String id, boolean isCustom) throws SQLException {
        String table = !isCustom ? "ExtractedStringID" : "CustomStringID";
        String query = !isCustom ? "insert into ExtractedStringID (id, updated) values (?, datetime())" : "insert into CustomStringID (id, updated) values (?, datetime())";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, id);
        ps.executeUpdate();
    }

    /**
     * Inserts the given string and its info into ExtractedString or CustomString.
     * <p>
     * If the string identifier is not in ExtractedStringID/CustomStringID,
     * it will be inserted into ExtractedStringID/CustomStringID as well.
     *
     * @param lang      language code of the string
     * @param id        the identifier of the string
     * @param content   the content associated to the string in the specified language
     * @param isCustom true if inserting to ExtractedString
     */
    void insertString(String lang, String id, String content, boolean isCustom) throws SQLException {
        // check if the ID is already in ExtractedStringID/CustomStringID
        String searchQuery = !isCustom ?
                "select * from ExtractedStringID where id = ?" : "select * from CustomStringID where id = ?";
        PreparedStatement queryID = con.prepareStatement(searchQuery);
        queryID.setString(1, id);
        ResultSet queryOutput = queryID.executeQuery();
        if (!queryOutput.isBeforeFirst()) insertStringID(id, isCustom);

        // insert into ExtractedString
        String insertUpdate = !isCustom ?
                "insert into ExtractedString (lang, id, content, updated) values (?, ?, ?, datetime('now'))" :
                "insert into CustomString (lang, id, content, updated) values (?, ?, ?, datetime('now'))";
        PreparedStatement ps = con.prepareStatement(insertUpdate);
        ps.setString(1, lang);
        ps.setString(2, id);
        ps.setString(3, content);
        ps.executeUpdate();
    }

    void setString(String lang, String id, String content, boolean isCustom) throws SQLException {
        String query;
        if (!isCustom) {
            query = "update ExtractedString set content = ?, updated = datetime('now') where id = ? and lang = ?";
        } else {
            query = "update CustomString set content = ?, updated = datetime('now') where id = ? and lang = ?";
        }
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, content);
        ps.setString(2, id);
        ps.setString(3, lang);
        ps.executeUpdate();
    }

    // BENCHES

    void insertBench(String rPath, String nameID) {
        try {
            insertObject(rPath);
            PreparedStatement ps = con.prepareStatement("insert into Bench (rel_path, name_id, updated) values (?, ?, datetime('now'))");
            ps.setString(1, rPath);
            ps.setString(2, nameID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void setBenchProfession(String rPath, String professionName) {
        try {
            PreparedStatement ps = con.prepareStatement("update Bench set profession_name = ? where rel_path = ?");
            ps.setString(1, professionName);
            ps.setString(2, rPath);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void insertBenchCategory(String rPath, String nameID, int benchIndex) {
        try {
            PreparedStatement ps = con.prepareStatement("insert into BenchCategory (rel_path, name_id, bench_index, updated) values (?, ?, ?, datetime('now'))");
            ps.setString(1, rPath);
            ps.setString(2, nameID);
            ps.setInt(3, benchIndex);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // RECIPES

    void insertRecipe(String rPath, String name) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into Recipe (rel_path, name, updated) values (?, ?, datetime('now'))");
        ps.setString(1, rPath);
        ps.setString(2, name);
        ps.executeUpdate();
    }

    void setRecipeBench(String rPath, String id) throws SQLException {
        String query = "update Recipe set bench_id = ?, updated = datetime('now') where rel_path = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, id);
        ps.setString(2, rPath);
        ps.executeUpdate();
    }

    void setRecipeCategory(String rPath, String id) throws SQLException {
        String query = "update Recipe set cat_id = ?, updated = datetime('now') where rel_path = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, id);
        ps.setString(2, rPath);
        ps.executeUpdate();
    }

    void insertRecipeCost(String rPath, String inputRPath, int inputCount) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into RecipeCost (rel_path, input_rel, input_count, updated) values (?, ?, ?, datetime('now'))");
        ps.setString(1, rPath);
        ps.setString(2, inputRPath);
        ps.setInt(3, inputCount);
        ps.executeUpdate();
    }

    void setRecipeCost(String rPath, String inputRPath, int inputCount) throws SQLException {
        String query = "update RecipeCost set input_count = ?, updated = datetime('now') where rel_path = ? and input_rel = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, inputCount);
        ps.setString(2, rPath);
        ps.setString(3, inputRPath);
        ps.executeUpdate();
    }

    void insertRecipeOutput(String rPath, String outputRPath, int outputCount) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into RecipeOutput (rel_path, output_rel, output_count, updated) values (?, ?, ?, datetime('now'))");
        ps.setString(1, rPath);
        ps.setString(2, outputRPath);
        ps.setInt(3, outputCount);
        ps.executeUpdate();
    }

    void setRecipeOutput(String rPath, String outputRPath, int outputCount) throws SQLException {
        String query = "update RecipeOutput set output_count = ?, updated = datetime('now') where rel_path = ? and output_rel = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, outputCount);
        ps.setString(2, rPath);
        ps.setString(3, outputRPath);
        ps.executeUpdate();
    }


    // COLLECTIONS

    void insertCollection(String rPath, int troveMR, int geodeMR) throws SQLException {
        insertObject(rPath);
        PreparedStatement ps = con.prepareStatement("insert into Collection (rel_path, trove_mr, geode_mr, updated) values (?, ?, ?, datetime('now'))");
        ps.setString(1, rPath);
        ps.setInt(2, troveMR);
        ps.setInt(3, geodeMR);
        ps.executeUpdate();
    }

    void setCollectionName(String rPath, String id) throws SQLException {
        String query = "update Collection set name_id = ?, updated = datetime('now') where rel_path = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, id);
        ps.setString(2, rPath);
        ps.executeUpdate();
    }

    void setCollectionDesc(String rPath, String id) throws SQLException {
        String query = "update Collection set desc_id = ?, updated = datetime('now') where rel_path = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, id);
        ps.setString(2, rPath);
        ps.executeUpdate();
    }

    void setCollectionTroveMastery(String rPath, int value) throws SQLException {
        String query = "update Collection set trove_mr = ?, updated = datetime('now') where rel_path = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, value);
        ps.setString(2, rPath);
        ps.executeUpdate();
    }

    void setCollectionGeodeMastery(String rPath, int value) throws SQLException {
        String query = "update Collection set geode_mr = ?, updated = datetime('now') where rel_path = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, value);
        ps.setString(2, rPath);
        ps.executeUpdate();
    }

    void insertCollectionType(String rPath, String type) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into CollectionType (rel_path, type, updated) values (?, ?, datetime('now'))");
        ps.setString(1, rPath);
        ps.setString(2, type);
        ps.executeUpdate();
    }

    void insertCollectionProperty(String rPath, String prop, double propValue) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into CollectionProperty (rel_path, prop, prop_val, updated) values (?, ?, ?, datetime('now'))");
        ps.setString(1, rPath);
        ps.setString(2, prop);
        ps.setDouble(3, propValue);
        ps.executeUpdate();
    }

    void setCollectionProperty(String rPath, String prop, double propValue) throws SQLException {
        PreparedStatement ps = con.prepareStatement("update CollectionProperty set prop_val = ? where rel_path = ? and prop = ?");
        ps.setDouble(1, propValue);
        ps.setString(2, rPath);
        ps.setString(3, prop);
        ps.executeUpdate();
    }

    void insertCollectionBuff(String rPath, String buff, double buffValue) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into CollectionBuff (rel_path, buff, buff_val, updated) values (?, ?, ?, datetime('now'))");
        ps.setString(1, rPath);
        ps.setString(2, buff);
        ps.setDouble(3, buffValue);
        ps.executeUpdate();
    }

    void setCollectionBuff(String rPath, String buff, double buffValue) throws SQLException {
        PreparedStatement ps = con.prepareStatement("update CollectionBuff set buff_val = ? where rel_path = ? and buff = ?");
        ps.setDouble(1, buffValue);
        ps.setString(2, rPath);
        ps.setString(3, buff);
        ps.executeUpdate();
    }

    // ITEMS

    /**
     * Inserts the given item into Item.
     * <p>
     * Assumes the nameID is already in ExtractedStringID.
     * If that is not the case, insert the appropriate string with insertExtractedString first.
     *
     * @param rPath relative path of the item
     */
    void insertItem(String rPath) throws SQLException {
        insertObject(rPath);
        PreparedStatement ps = con.prepareStatement("insert into Item (rel_path, updated) values (?, datetime('now'))");
        ps.setString(1, rPath);
        ps.executeUpdate();
    }

    void setItemName(String rPath, String id) throws SQLException {
        String query = "update Item set name_id = ?, updated = datetime('now') where rel_path = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, id);
        ps.setString(2, rPath);
        ps.executeUpdate();
    }

    void setItemDesc(String rPath, String id) throws SQLException {
        String query = "update Item set desc_id = ?, updated = datetime('now') where rel_path = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, id);
        ps.setString(2, rPath);
        ps.executeUpdate();
    }

    void setItemTradable(String rPath, boolean isTradable) throws SQLException {
        String query = "update Item set tradable = ?, updated = datetime('now') where rel_path = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, isTradable ? 1 : 0);
        ps.setString(2, rPath);
        ps.executeUpdate();
    }

    void insertUnlock(String rPath, String colRPath) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into Unlock (item_rel, col_rel, updated) values (?, ?, datetime('now'))");
        ps.setString(1, rPath);
        ps.setString(2, colRPath);
        ps.executeUpdate();
    }

    void insertLootbox(String rPath, String rarity, String outputRPath, String outputCount) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into Lootbox (rel_path, rarity, output_rel, output_count, updated) values (?, ?, ?, ?, datetime('now'))");
        ps.setString(1, rPath);
        ps.setString(2, rarity);
        ps.setString(3, outputRPath);
        ps.setString(4, outputCount);
        ps.executeUpdate();
    }

    void setLootbox(String rPath, String rarity, String outputRPath, String outputCount) throws SQLException {
        PreparedStatement ps = con.prepareStatement("update Lootbox set output_count = ?, updated = datetime('now') where rel_path = ? and rarity = ? and output_rel = ? ");
        ps.setString(1, outputCount);
        ps.setString(2, rPath);
        ps.setString(3, rarity);
        ps.setString(4, outputRPath);
        ps.executeUpdate();
    }

    // PLACEABLES

    void insertPlaceable(String rPath) throws SQLException {
        insertObject(rPath);
        PreparedStatement ps = con.prepareStatement("insert into Placeable (rel_path, updated) values (?, datetime('now'))");
        ps.setString(1, rPath);
        ps.executeUpdate();
    }

    void setPlaceableName(String rPath, String id) throws SQLException {
        String query = "update Placeable set name_id = ?, updated = datetime('now') where rel_path = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, id);
        ps.setString(2, rPath);
        ps.executeUpdate();
    }

    void setPlaceableDesc(String rPath, String id) throws SQLException {
        String query = "update Placeable set desc_id = ?, updated = datetime('now') where rel_path = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, id);
        ps.setString(2, rPath);
        ps.executeUpdate();
    }

    void setPlaceableTradable(String rPath, boolean isTradable) throws SQLException {
        String query = "update Placeable set tradable = ?, updated = datetime('now') where rel_path = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, isTradable ? 1 : 0);
        ps.setString(2, rPath);
        ps.executeUpdate();
    }

    // MISC

    void insertNote(String rPath, String stringID) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into Notes (rel_path, string_id, updated) values (?, ?, datetime('now'))");
        ps.setString(1, rPath);
        ps.setString(2, stringID);
        ps.executeUpdate();
    }


    void insertDecon(String inputRPath, String outputRPath, int outputCount) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into Decon (input_rel, output_rel, output_count, updated) values (?, ?, ?, datetime('now'))");
        ps.setString(1, inputRPath);
        ps.setString(2, outputRPath);
        ps.setInt(3, outputCount);
        ps.executeUpdate();
    }

    void setDecon(String inputRPath, String outputRPath, int outputCount) throws SQLException {
        String query = "update Decon set output_count = ?, updated = datetime('now') where input_rel = ? and output_rel = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, outputCount);
        ps.setString(2, inputRPath);
        ps.setString(3, outputRPath);
        ps.executeUpdate();
    }
}
