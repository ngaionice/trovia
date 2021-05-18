package ui2;

import datamodel.DataModel;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.cell.CheckBoxTreeCell;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LogicController {

    DataModel model;
    String benchFilter = "_interactive";
    String collectionFilter = "";
    String itemFilter = "";
    String placeableFilter = "";
    String stringFilter = "prefabs_";
    String recipeFilter = "";

    List<String> selectedPaths = new ArrayList<>();
    List<String> failedPaths = new ArrayList<>();

    void loadDatabase(File selectedFile) {
        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            String lang = "en";     // need to make mini-dialog to select language, but setting to english for now
            try {
                model = new DataModel(path, lang);
                // popup saying data loaded -- TODO: make method return boolean to do this
            } catch (SQLException ex) {
                // do something
            }
        }
    }

    void createDatabase(File selectedDirectory) {
        if (selectedDirectory != null) {
            String path = selectedDirectory.getAbsolutePath();
            System.out.println(path);
            // create new database at the specified path
        }
    }

    void setFilter(String filter, String type) {
        switch (type) {
            case "bench":
            case "profession":
                benchFilter = filter;
            case "collection":
                collectionFilter = filter;
            case "item":
                itemFilter = filter;
            case "placeable":
                placeableFilter = filter;
            case "recipe":
                recipeFilter = filter;
            case "string":
                stringFilter = filter;
        }
    }

    String getFilterText(String type) {
        switch (type) {
            case "bench":
            case "profession":
                return benchFilter;
            case "collection":
                return collectionFilter;
            case "item":
                return itemFilter;
            case "placeable":
                return placeableFilter;
            case "recipe":
                return recipeFilter;
            case "string":
                return stringFilter;
            default:
                return "";
        }
    }

    CheckBoxTreeItem<String> getParseTree(String path, String type) {
        String filter = getFilterText(type);
        File dir = new File(path);   // since presenter checks that the input path is a directory, we can assume that here
        List<String> paths = Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .map(File::getPath).filter(value -> value.contains(filter)).collect(Collectors.toList());

        CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>(path);
        rootItem.setExpanded(false);
        List<CheckBoxTreeItem<String>> nonDirectories = new ArrayList<>();
        for (String subPath : paths) {

            // if directory, recursively call the method and add them all to root; note if the sub-directory has no valid files, it gets omitted
            if (new File(subPath).isDirectory()) {
                CheckBoxTreeItem<String> subDir = getParseTree(subPath, type);

                if (!subDir.isLeaf()) {
                    rootItem.getChildren().add(getParseTree(subPath, type));
                }
            }

            // else, add path to list if it has the filter keyword
            else {
                // if npcCheck passes and contains filter word, we process the item
                boolean npcCheck = !type.equals("collection") || !subPath.contains("_npc");
                if (subPath.contains(filter) && npcCheck) {
                    CheckBoxTreeItem<String> item = new CheckBoxTreeItem<>(subPath);

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

    void clearSelectedPaths() {
        selectedPaths.clear();
    }
}
