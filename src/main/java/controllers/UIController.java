package controllers;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.CheckBoxTreeItem;
import parser.Parser;
import parser.parsestrategies.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UIController {

    LogicController con = new LogicController();
    List<String> selectedPaths = new ArrayList<>();

    public static class Searchable extends RecursiveTreeObject<Searchable> {

        public StringProperty name;
        public StringProperty rPath;

        public Searchable(String name, String rPath) {
            this.name = new SimpleStringProperty(name);
            this.rPath = new SimpleStringProperty(rPath);
        }

    }

    public static class Directory extends RecursiveTreeObject<Directory> {

        public StringProperty rPath;

        public Directory(String name, String rPath) {
            this.rPath = new SimpleStringProperty(rPath);
        }

    }

    public ObservableList<Searchable> getSearchableList(List<String> types) {
        ObservableList<Searchable> searchList = FXCollections.observableArrayList();
        List<String[]> nameAndRPathList = con.getNameAndRPathList(types);

        for (String[] item: nameAndRPathList) {
            searchList.add(new Searchable(item[0], item[1]));
        }

        return searchList;
    }

    public List<String> getPaths(String dirPath, String filter) {
        return con.filterOutWithout(con.getPaths(con.getFiles(dirPath)), filter);
    }

    public CheckBoxTreeItem<String> getFileTree(String dirPath, String filter) {

        CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>(dirPath);
        rootItem.setExpanded(false);
        for (String path : getPaths(dirPath, filter)) {

            // if directory, recursively call the method and add them all to root; note if the sub-directory has no valid files, it gets omitted
            if (new File(path).isDirectory()) {
                CheckBoxTreeItem<String> subDir = getFileTree(path, filter);
                

                if (!subDir.isLeaf()) {
                    rootItem.getChildren().add(getFileTree(path, filter));
                }
            }

            // else, add path to root if it has the filter keyword
            else {
                if (path.contains(filter)) {
                    CheckBoxTreeItem<String> item = new CheckBoxTreeItem<>(path);

                    // add items to selectedPaths to be parsed later
                    item.selectedProperty().addListener(event -> {
                        if (item.isSelected()) {
                            selectedPaths.add(item.getValue());
                        } else {
                            selectedPaths.remove(item.getValue());
                        }

                    });
                    rootItem.getChildren().add(item);
                }
            }
        }
        return rootItem;
    }

    public Task<Void> getParseTask(Parser.ObjectType type) {
        return new Task<Void>() {
            @Override protected Void call() throws IOException, ParseException {
                int selectedPathsLength = selectedPaths.size();
                for (int i = 0; i < selectedPathsLength; i++) {
                    updateMessage("Parsing " + (i + 1) + "/" + selectedPathsLength + " " + type.toString());
                    updateProgress(i, selectedPathsLength);
                    con.createObject(selectedPaths.get(i), type);
                }
                updateMessage("Parsing complete.");
                updateProgress(selectedPathsLength, selectedPathsLength);
                return null;
            }
        };
    }
}
