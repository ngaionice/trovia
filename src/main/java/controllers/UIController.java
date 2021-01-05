package controllers;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import parser.Parser;
import parser.parsestrategies.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UIController {

    LogicController con = new LogicController();
    List<String> selectedPaths = new ArrayList<>();
    List<String> failedPaths = new ArrayList<>();

    public static class Searchable extends RecursiveTreeObject<Searchable> {

        public StringProperty name;
        public StringProperty rPath;

        public Searchable(String name, String rPath) {
            this.name = new SimpleStringProperty(name);
            this.rPath = new SimpleStringProperty(rPath);
        }

        public String getName() {
            return name.get();
        }

        public String getRPath() {
            return rPath.get();
        }
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

    public VBox getItemContent(String rPath) {
        VBox content = new VBox();
        content.setSpacing(3);

        // will need to get more items from LogicController later as we add more properties
        content.getChildren().add(new Text("Description:"));
        content.getChildren().add(new Text(con.getItemDesc(rPath)));

        content.getChildren().add(new Text("Description identifer:"));
        content.getChildren().add(new Text(con.getItemDescIdentifier(rPath)));

        content.getChildren().add(new Text("Relative path:"));
        content.getChildren().add(new Text(rPath));

        return content;
    }

    public VBox getCollectionContent(String rPath) {
        VBox content = new VBox();
        content.setSpacing(3);

        content.getChildren().add(new Text("Description:"));
        TextArea desc = new TextArea(con.getCollectionDesc(rPath));
        content.getChildren().add(desc);

        content.getChildren().add(new Text("Description identifer:"));
        content.getChildren().add(new Text(con.getCollectionDescIdentifier(rPath)));

        content.getChildren().add(new Text("Relative path:"));
        content.getChildren().add(new Text(rPath));

        return content;
    }

    public VBox getBenchContent(String rPath) {

        VBox content = new VBox();
        content.setSpacing(3);

        content.getChildren().add(new Text("Recipes in bench:"));
        content.getChildren().add(new TextArea(String.join(" \n", con.getBenchRecipes(rPath))));

        content.getChildren().add(new Text("Relative path:"));
        content.getChildren().add(new Text(rPath));

        return content;
    }

    public void debugParse() throws IOException {
        con.createObject("C:\\Program Files (x86)\\Glyph\\Games\\Trove\\Live\\extracted_dec_15_subset\\languages\\en\\prefabs_item_aura.binfab", Parser.ObjectType.LANG_FILE);
    }

    public List<String> getSelectedPaths() {
        return selectedPaths;
    }

    public List<String> getFailedPaths() {
        return failedPaths;
    }

    public void clearFailedPaths() {
        failedPaths.clear();
    }

    public VBox getFailedContent() {
        VBox content = new VBox();
        content.setSpacing(3);

        content.getChildren().add(new Text("The following paths were not parsed:"));
        content.getChildren().add(new TextArea(String.join(" \n", failedPaths)));

        TextArea message = new TextArea("Note that this is normal, if files that have not been designed to be parsed were selected.");
//        message.getStyleClass().add("text-field")
        message.setWrapText(true);
        message.setPrefHeight(50);
        content.getChildren().add(message);

        return content;
    }
}
