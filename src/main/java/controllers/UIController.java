package controllers;

import managers.*;
import objects.CollectionEnums;
import ui.DesignProperties;
import ui.searchables.Searchable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import parser.Parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class UIController {

    LogicController con = new LogicController();
    DesignProperties dp = new DesignProperties();

    List<String> selectedPaths = new ArrayList<>();
    List<String> failedPaths = new ArrayList<>();

    List<String> selectedArticles = new ArrayList<>();

    // CREATE-RELATED

    public List<String> getPaths(String dirPath, String filter) {
        return con.filterOutWithout(con.getPaths(con.getFiles(dirPath)), filter);
    }

    public CheckBoxTreeItem<String> getFileTree(String dirPath, String filter, boolean npcFilter) {

        CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>(dirPath);
        rootItem.setExpanded(false);
        List<CheckBoxTreeItem<String>> nonDirectories = new ArrayList<>();
        for (String path : getPaths(dirPath, filter)) {

            // if directory, recursively call the method and add them all to root; note if the sub-directory has no valid files, it gets omitted
            if (new File(path).isDirectory()) {
                CheckBoxTreeItem<String> subDir = getFileTree(path, filter, npcFilter);


                if (!subDir.isLeaf()) {
                    rootItem.getChildren().add(getFileTree(path, filter, npcFilter));
                }
            }

            // else, add path to list if it has the filter keyword
            else {

                // npc filter for Collection parsing
                boolean npcCheck = true;
                if (npcFilter && path.contains("_npc")) {
                    npcCheck = false;
                }

                // if npcCheck passes and contains filter word, we process the item
                if (path.contains(filter) && npcCheck) {
                    CheckBoxTreeItem<String> item = new CheckBoxTreeItem<>(path);

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

    public Task<Void> getParseTask(Parser.ObjectType type) {
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

    public void clearParseList() {
        selectedPaths.clear();
    }

    // VIEW-RELATED

    public VBox getItemContent(String rPath) {
        VBox content = getContentBox();

        // will need to get more items from LogicController later as we add more properties
        content.getChildren().add(new Text("Description:"));
        TextArea desc = new TextArea(con.getItemDesc(rPath));
        desc.setWrapText(true);
        desc.setPrefHeight(content.getHeight()*0.2);
        content.getChildren().add(desc);

        content.getChildren().add(new Text("Description identifer:"));
        content.getChildren().add(new Text(con.getItemDescIdentifier(rPath)));

        content.getChildren().add(new Text("Relative path:"));
        content.getChildren().add(new Text(rPath));

        return content;
    }

    public VBox getCollectionContent(String rPath) {
        VBox content = getContentBox();

        content.getChildren().add(createSubHeaderText("Description:", content));
        content.getChildren().add(createContentText(con.getCollectionDesc(rPath), content));

        content.getChildren().add(createSubHeaderText("Description identifer:", content));
        content.getChildren().add(createContentText(con.getCollectionDescIdentifier(rPath), content));

        content.getChildren().add(createSubHeaderText("Relative path:", content));
        content.getChildren().add(createContentText(rPath, content));

        Map<CollectionEnums.Property, Double> colProp = con.getCollectionProp(rPath);
        Map<CollectionEnums.Buff, Double> buffs = con.getDragonBuffs(rPath);

        if (!colProp.isEmpty()) {
            for (Map.Entry<CollectionEnums.Property, Double> item: colProp.entrySet()) {
                Text[] prop = getProperties(item, content);
                content.getChildren().add(prop[0]);
                content.getChildren().add(prop[1]);
            }
        }

        if (buffs != null) {
            for (Map.Entry<CollectionEnums.Buff, Double> item: buffs.entrySet()) {
                Text[] prop = getBuffs(item, content);
                content.getChildren().add(prop[0]);
                content.getChildren().add(prop[1]);
            }
        }

        Integer[] mastery = con.getMastery(rPath);
        content.getChildren().add(createSubHeaderText("Trove Mastery: ", content));
        content.getChildren().add(createContentText(mastery[0].toString(), content));

        content.getChildren().add(createSubHeaderText("Geode Mastery: ", content));
        content.getChildren().add(createContentText(mastery[1].toString(), content));

        if (con.getRecipes(rPath) != null) {
            content.getChildren().add(createSubHeaderText("Recipes: ", content));
            for (String item: con.getRecipes(rPath)) {
                content.getChildren().add(createContentText(item, content));
            }
        }

        if (con.getNotes(rPath) != null) {
            content.getChildren().add(createSubHeaderText("Notes: ", content));
            for (String item: con.getNotes(rPath)) {
                content.getChildren().add(createContentText(item, content));
            }
        }




        return content;
    }

    public VBox getBenchContent(String rPath) {
        VBox content = getContentBox();

        content.getChildren().add(createSubHeaderText("Recipes in bench:", content));
        content.getChildren().add(new TextArea(String.join(" \n", con.getBenchRecipes(rPath))));

        content.getChildren().add(createSubHeaderText("Relative path:", content));
        content.getChildren().add(createContentText(rPath, content));

        return content;
    }

    public ObservableList<Searchable> getSearchableList(List<Parser.ObjectType> types, String filter) {
        ObservableList<Searchable> searchList = FXCollections.observableArrayList();
        List<String[]> nameAndRPathList = con.getNameAndRPathList(types);

        for (String[] item: nameAndRPathList) {
            if (item[0].toLowerCase().contains(filter.toLowerCase())) {
                searchList.add(new Searchable(item[0], item[1]));
            }
        }

        return searchList;
    }

    public List<String> getFailedPaths() {
        return failedPaths;
    }

    public VBox getFailedContent(String dirPath) {
        VBox content = new VBox();
        content.setSpacing(3);

        for (int i = 0; i < failedPaths.size(); i++) {
            failedPaths.set(i, failedPaths.get(i).substring(dirPath.length() + 1));
        }
        String failedPathsJoined = String.join(" \n", failedPaths);

        content.getChildren().add(new Text("The following paths were not parsed:"));
        content.getChildren().add(new TextArea(failedPathsJoined));

        TextArea message = new TextArea("Note that this is normal, if files that have not been designed to be parsed were selected.");
        message.setWrapText(true);
        message.setPrefHeight(50);
        content.getChildren().add(message);

        return content;
    }

    // MODIFY-RELATED

    public void addSelectedArticle(String rPath) {
        selectedArticles.add(rPath);
    }

    public void removeSelectedArticle(String rPath) {
        selectedArticles.remove(rPath);
    }

    public void clearSelectedArticles(String rPath) {
        selectedArticles.clear();
    }

    public List<String> getAllSelectedArticles() {
        return selectedArticles;
    }

    // DEBUGGING

    public void debugParse() throws IOException {
        con.createObject("C:\\Program Files (x86)\\Glyph\\Games\\Trove\\Live\\extracted_dec_15_subset\\languages\\en\\prefabs_item_aura.binfab", Parser.ObjectType.LANG_FILE);
    }

    // HELPER
    VBox getContentBox() {
        VBox box = new VBox();
        box.setSpacing(3);
        return box;
    }

    Text createContentText(String text, VBox box) {
        Text newText = new Text(text);
        newText.setFont(dp.fontNormal);
        newText.wrappingWidthProperty().bind(box.widthProperty().multiply(0.9));
        return newText;
    }

    Text createSubHeaderText(String text, VBox box) {
        Text newText = new Text(text);
        newText.setFont(dp.fontSubHeader);
        newText.wrappingWidthProperty().bind(box.widthProperty().multiply(0.9));
        return newText;
    }

    Text[] getProperties(Map.Entry<CollectionEnums.Property, Double> entry, VBox box) {
        switch (entry.getKey()) {
            case GROUND_MS:
                return switchHelper("Ground movement speed: ",entry.getValue().toString(), box);
            case AIR_MS:
                return switchHelper("Flight speed: ",entry.getValue().toString(), box);
            case GLIDE:
                return switchHelper("Glide: ",entry.getValue().toString(), box);
            case POWER_RANK:
                return switchHelper("Power Rank: ",entry.getValue().toString(), box);
            case MAG_MS:
                return switchHelper("Mag rider speed: ",entry.getValue().toString(), box);
            case WATER_MS:
                return switchHelper("Ship top speed: ",entry.getValue().toString(), box);
            case ACCEL:
                return switchHelper("Ship acceleration: ",entry.getValue().toString(), box);
            case TURN_RATE:
                return switchHelper("Ship turning rate: ",entry.getValue().toString(), box);
            default:
                return null;
        }
    }

    Text[] getBuffs(Map.Entry<CollectionEnums.Buff, Double> entry, VBox box) {
        switch (entry.getKey()) {
            case CD:
                return switchHelper("Critical Damage (%): ",entry.getValue().toString(), box);
            case CH:
                return switchHelper("Critical Hit (%): ",entry.getValue().toString(), box);
            case PD:
                return switchHelper("Physical Damage: ",entry.getValue().toString(), box);
            case MD:
                return switchHelper("Magical Damage: ",entry.getValue().toString(), box);
            case AS:
                return switchHelper("Attack Speed: ",entry.getValue().toString(), box);
            case EN:
                return switchHelper("Max Energy: ",entry.getValue().toString(), box);
            case ER:
                return switchHelper("Energy Regen: ",entry.getValue().toString(), box);
            case JP:
                return switchHelper("Jump: ",entry.getValue().toString(), box);
            case LS:
                return switchHelper("Lasermancy: ",entry.getValue().toString(), box);
            case LT:
                return switchHelper("Light: ",entry.getValue().toString(), box);
            case HR:
                return switchHelper("Health Regen: ",entry.getValue().toString(), box);
            case MF:
                return switchHelper("Magic Find: ",entry.getValue().toString(), box);
            case MH:
                return switchHelper("Maximum Health: ", entry.getValue().toString(), box);
            case ER_PCT:
                return switchHelper("Energy Regen (%): ",entry.getValue().toString(), box);
            case HR_PCT:
                return switchHelper("Health Regen (%): ", entry.getValue().toString(), box);
            case MH_PCT:
                return switchHelper("Maximum Health (%): ",entry.getValue().toString(), box);
            default:
                return null;
        }
    }

    Text[] switchHelper(String desc, String value, VBox box) {
        return new Text[] {createSubHeaderText(desc, box), createContentText(value, box)};
    }
}
