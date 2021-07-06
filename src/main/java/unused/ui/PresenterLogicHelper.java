package unused.ui;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import datamodel.Enums;
import unused.model.ModelController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBoxTreeItem;
import unused.model.objects.CollectionEnums;
import unused.ui.searchables.Searchable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PresenterLogicHelper {

    // controls logic for elements in the Presenter
    String dirPath = "C:\\Program Files (x86)\\Glyph\\Games\\Trove\\Live\\extracted_dec_15_subset";
    String benchFilter = "_interactive";
    String colFilter = "";
    String itemFilter = "";
    String langFilter = "prefabs_";
    String recFilter = "";

    List<String> selectedPaths = new ArrayList<>();
    List<String> failedPaths = new ArrayList<>();

    ModelController con;

    public PresenterLogicHelper(ModelController con) {
        this.con = con;
    }

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


    public CheckBoxTreeItem<String> getFileTree(String dirPath, String filter, boolean collectionFilter) {

        CheckBoxTreeItem<java.lang.String> rootItem = new CheckBoxTreeItem<>(dirPath);
        rootItem.setExpanded(false);
        List<CheckBoxTreeItem<java.lang.String>> nonDirectories = new ArrayList<>();
        for (java.lang.String path : con.getPathsWithFilter(dirPath, filter)) {

            // if directory, recursively call the method and add them all to root; note if the sub-directory has no valid files, it gets omitted
            if (new File(path).isDirectory()) {
                CheckBoxTreeItem<java.lang.String> subDir = getFileTree(path, filter, collectionFilter);


                if (!subDir.isLeaf()) {
                    rootItem.getChildren().add(getFileTree(path, filter, collectionFilter));
                }
            }

            // else, add path to list if it has the filter keyword
            else {
                // if npcCheck passes and contains filter word, we process the item
                boolean npcCheck = !collectionFilter || !path.contains("_npc");
                if (path.contains(filter) && npcCheck) {
                    CheckBoxTreeItem<java.lang.String> item = new CheckBoxTreeItem<>(path);

                    // add items to selectedPaths to be parsed later
                    item.selectedProperty().addListener(event -> {
                        if (item.isSelected()) {
                            selectedPaths.add(item.getValue());
                        } else {
                            selectedPaths.remove(item.getValue());
                        }

                    });
                    nonDirectories.add(item);
                }
            }
        }

        // add all the non-directories back in; this is done instead of adding the items in at processing time to show the directories first
        rootItem.getChildren().addAll(nonDirectories);
        return rootItem;
    }

    public Task<Void> getParseTask(Enums.ObjectType type) {
        return new Task<Void>() {
            @Override protected Void call() throws IOException {

                // clear out old failed paths
                failedPaths.clear();

                // begin parsing
                int selectedPathsLength = selectedPaths.size();
                for (int i = 0; i < selectedPathsLength; i++) {
                    updateMessage("Parsing " + (i + 1) + "/" + selectedPathsLength + " " + type.toString());
                    updateProgress(i, selectedPathsLength);
                    String output = con.createObject(selectedPaths.get(i), type);
                    if (output != null) {
                        failedPaths.add(output);
                    }
                }
                updateMessage("Parsing complete.");
                updateProgress(selectedPathsLength, selectedPathsLength);

                return null;
            }
        };
    }

    Task<Void> getSyncTask(boolean exportAll) {
        return new Task<Void>() {
            @Override protected Void call() {

                // begin parsing
                updateMessage("Synchronizing to database.");
                con.exportDataMongo(exportAll);

                updateMessage("Synchronization complete.");
                updateProgress(1, 1);

                return null;
            }
        };
    }

    void clearParseList() {
        selectedPaths.clear();
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

    void itemPropertyInsert(List<JFXComboBox<String>> comboBoxes, List<String> rPaths, List<JFXTextField> quantities, int numberOfFields, boolean lootbox, String rarity) {
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
                con.addLootboxContent(rPath, rarity, loot);
            }
        } else {
            for (String rPath: rPaths) {
                con.addDeconContent(rPath, loot);
            }
        }
    }

    List<String> getFailedPaths() {
        return failedPaths;
    }

    void addNotes(List<String> rPaths, String note) {
        for (String item: rPaths) {
            con.addNotes(item, note);
        }
    }

    List<String> matchNewRecipes() {
        List<String> output = con.matchNewRecipes();
        if (output != null) {
            for (int i = 0; i < output.size(); i++) {
                output.set(i, output.get(i).replace(dirPath, ""));
            }
            return output;
        }
        return null;
    }

    // VIEW

    ObservableList<Searchable> getSearchableList(List<Enums.ObjectType> types, String filter, String mapType) {
        ObservableList<Searchable> searchList = FXCollections.observableArrayList();
        List<String[]> nameAndRPathList = con.getNameAndRPathList(types, mapType);

        for (String[] item: nameAndRPathList) {
            if (item[0].toLowerCase().contains(filter.toLowerCase())) {
                searchList.add(new Searchable(item[0], item[1]));
            }
        }

        return searchList;
    }

    String[] getProperties(Map.Entry<CollectionEnums.Property, Double> entry) {
        switch (entry.getKey()) {
            case GROUND_MS:
                return new String[] {"Ground movement speed: ",entry.getValue().toString()};
            case AIR_MS:
                return new String[] {"Flight speed: ",entry.getValue().toString()};
            case GLIDE:
                return new String[] {"Glide: ",entry.getValue().toString()};
            case POWER_RANK:
                return new String[] {"Power Rank: ",entry.getValue().toString()};
            case MAG_MS:
                return new String[] {"Mag rider speed: ",entry.getValue().toString()};
            case WATER_MS:
                return new String[] {"Ship top speed: ",entry.getValue().toString()};
            case ACCEL:
                return new String[] {"Ship acceleration: ",entry.getValue().toString()};
            case TURN_RATE:
                return new String[] {"Ship turning rate: ",entry.getValue().toString()};
            default:
                return null;
        }
    }

    String[] getBuffs(Map.Entry<CollectionEnums.Buff, Double> entry) {
        switch (entry.getKey()) {
            case CD:
                return new String[] {"Critical Damage (%): ",entry.getValue().toString()};
            case CH:
                return new String[] {"Critical Hit (%): ",entry.getValue().toString()};
            case PD:
                return new String[] {"Physical Damage: ",entry.getValue().toString()};
            case MD:
                return new String[] {"Magical Damage: ",entry.getValue().toString()};
            case AS:
                return new String[] {"Attack Speed: ",entry.getValue().toString()};
            case EN:
                return new String[] {"Max Energy: ",entry.getValue().toString()};
            case ER:
                return new String[] {"Energy Regen: ",entry.getValue().toString()};
            case JP:
                return new String[] {"Jump: ",entry.getValue().toString()};
            case LS:
                return new String[] {"Lasermancy: ",entry.getValue().toString()};
            case LT:
                return new String[] {"Light: ",entry.getValue().toString()};
            case HR:
                return new String[] {"Health Regen: ",entry.getValue().toString()};
            case MF:
                return new String[] {"Magic Find: ",entry.getValue().toString()};
            case MH:
                return new String[] {"Maximum Health: ", entry.getValue().toString()};
            case ER_PCT:
                double percentage = entry.getValue()*100;
                return new String[] {"Energy Regen (%): ", Double.toString(percentage)};
            case HR_PCT:
                percentage = entry.getValue()*100;
                return new String[] {"Health Regen (%): ", Double.toString(percentage)};
            case MH_PCT:
                percentage = entry.getValue()*100;
                return new String[] {"Maximum Health (%): ", Double.toString(percentage)};
            default:
                return null;
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
