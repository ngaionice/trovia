package ui;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import controllers.ModelController;
import controllers.UIController;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PresenterLogicHelper {

    // controls logic for elements in the Presenter

    ModelController con = new ModelController();

    String dirPath = "C:\\Program Files (x86)\\Glyph\\Games\\Trove\\Live\\extracted_dec_15_subset";
    String benchFilter = "_interactive";
    String colFilter = "";
    String itemFilter = "";
    String langFilter = "prefabs_";
    String recFilter = "";

    // CREATE
    boolean saveParseSettings(String dirInput, String benchInput, String colInput, String itemInput, String recInput, String langInput) {

        boolean alerted = false;
        // set directory path
        if (!dirInput.isEmpty() && dirInput.contains("\\") && new File(dirInput).isDirectory()) {
            dirPath = dirInput;
        } else if (!dirInput.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "The input directory was invalid, so the current directory path was not updated.");
            alert.showAndWait();
            alerted = true;
        }

        if (!benchInput.isEmpty()) {
            benchFilter = benchInput;
        }

        if (!colInput.isEmpty()) {
            colFilter = colInput;
        }

        if (!itemInput.isEmpty()) {
            itemFilter = itemInput;
        }

        if (!recInput.isEmpty()) {
            recFilter = recInput;
        }

        if (!langInput.isEmpty()) {
            langFilter = langInput;
        }
        return alerted;
    }

    VBox getContent(String rPath, UIController con) {

        VBox content;
        if (rPath.contains("item")) {
            content = con.getItemContent(rPath);
        } else if (rPath.contains("collection")) {
            content = con.getCollectionContent(rPath);
        } else {
            content = con.getBenchContent(rPath);
        }

        return content;
    }

    // MODIFY

    boolean itemPropertyInputValidation(List<JFXComboBox<String>> comboBoxes, List<String> itemNames, List<JFXTextField> quantities, int numberOfFields, boolean lootbox) {
        boolean status = true;
        for (int i = 0; i < numberOfFields; i++) {
            if (!itemNames.contains(comboBoxes.get(i).getValue())) {
                status = false;
                break;
            }

            String intRegex = lootbox ? "\\d*[-]?\\d+" : "\\d+";
            if (!quantities.get(i).getText().matches(intRegex)) {
                status = false;
                break;
            }
        }
        return status;
    }

    void itemPropertyInsert(List<JFXComboBox<String>> comboBoxes, List<String> rPaths, List<JFXTextField> quantities, int numberOfFields, boolean lootbox, String rarity, UIController uiCon) {
        // get the rPaths
        List<String[]> loot = new ArrayList<>();
        for (int i = 0; i < numberOfFields; i++) {
            String item = comboBoxes.get(i).getValue();
            String quantity = quantities.get(i).getText();

            String itemRPath = item.split(" - ")[1];
            loot.add(new String[]{itemRPath, quantity});
        }

        // add the loot

        // if lootbox, add to lootbox, else add to decons
        if (lootbox) {
            for (String rPath: rPaths) {
                uiCon.addLootboxContent(rPath, rarity, loot);
            }
        } else {
            for (String rPath: rPaths) {
                uiCon.addDeconContent(rPath, loot);
            }
        }
    }

    // VAR GETTERS
    String getDirPath() {
        return dirPath;
    }

    String getBenchFilter() {
        return benchFilter;
    }

    String getColFilter() {
        return colFilter;
    }

    String getItemFilter() {
        return itemFilter;
    }

    String getLangFilter() {
        return langFilter;
    }

    String getRecFilter() {
        return recFilter;
    }
}
