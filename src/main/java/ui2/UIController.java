package ui2;

import datamodel.DataModel;
import datamodel.objects.ObservableBench;
import datamodel.parser.Parser;
import datamodel.parser.parsestrategies.ParseException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class UIController {

    DataModel model;
    String benchFilter = "_interactive";
    String collectionFilter = "";
    String itemFilter = "";
    String placeableFilter = "";
    String stringFilter = "prefabs_";
    String recipeFilter = "";

    List<String> selectedPaths = new ArrayList<>();
    List<String> failedPaths = new ArrayList<>();

    boolean loadDatabase(Stage stage, TextArea logger) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Database Files", "*.db"));
        File selected = fileChooser.showOpenDialog(stage);
        if (selected != null) {
            String path = selected.getAbsolutePath();
            String lang = "en";     // need to make mini-dialog to select language, but setting to english for now
            try {
                model = new DataModel(path, lang);
                print(logger, "Database loaded from " + path);
                return true;
            } catch (SQLException e) {
                print(logger, "Database loading failed due to a SQLException; stack trace below:");
                Arrays.asList(e.getStackTrace()).forEach(error -> print(logger, error.toString()));
                return false;
            }
        }
        return false;
    }

    boolean createDatabase(Stage stage, TextArea logger) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select the location to save the database at.");
        File newDb = dirChooser.showDialog(stage);
        FileChooser fileChooser = new FileChooser();
        if (newDb != null) {
            fileChooser.setTitle("Select your Data Definition Language file.");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DDL files", "*.ddl"));
            File ddl = fileChooser.showOpenDialog(stage);
            if (ddl != null) {
                String path = newDb.getAbsolutePath() + "\\trove.db";
                String ddlPath = ddl.getAbsolutePath();
                String lang = "en";
                try {
                    model = new DataModel(path, ddlPath, lang);
                    print(logger, "Database created at " + path + " using DDL file at " + ddlPath);
                    return true;
                } catch (SQLException e) {
                    print(logger, "Database creation failed due to a SQLException; stack trace below:");
                    Arrays.asList(e.getStackTrace()).forEach(error -> print(logger, error.toString()));
                    return false;
                }
            }
        }


        return false;
    }

    void dumpLogs(Stage stage, TextArea logger) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select the location to save the logs at.");
        File selected = dirChooser.showDialog(stage);
        if (selected != null) {
            String path = selected.getAbsolutePath();
            LocalDateTime currTime = LocalDateTime.now();
            String fileName = "trovia-log-" + currTime.toString().replace(":", "-").replace(".", "-") + ".txt";
            try {
                Path file = Paths.get(path + "\\" + fileName);
                Files.write(file, Arrays.asList(logger.getText().split("\\n")), StandardCharsets.UTF_8);
                logger.clear();
                print(logger, "Dumped log at " + currTime.toLocalTime());
            } catch (IOException e) {
                print(logger, "Log dump failed due to an IOException; stack trace below:");
                Arrays.asList(e.getStackTrace()).forEach(error -> print(logger, error.toString()));
            }
        }
    }

    void enableActionButtons(List<Button> buttons) {
        buttons.forEach(item -> item.setDisable(false));
    }

    void disableActionButtons(List<Button> buttons) {
        buttons.forEach(item -> item.setDisable(true));
    }

    void setFilter(String filter, Parser.ObjectType type) {
        switch (type) {
            case BENCH:
            case PROFESSION:
                benchFilter = filter;
            case COLLECTION:
                collectionFilter = filter;
            case ITEM:
                itemFilter = filter;
            case PLACEABLE:
                placeableFilter = filter;
            case RECIPE:
                recipeFilter = filter;
            case STRING:
                stringFilter = filter;
        }
    }

    String getFilterText(Parser.ObjectType type) {
        switch (type) {
            case BENCH:
            case PROFESSION:
                return benchFilter;
            case COLLECTION:
                return collectionFilter;
            case ITEM:
                return itemFilter;
            case PLACEABLE:
                return placeableFilter;
            case RECIPE:
                return recipeFilter;
            case STRING:
                return stringFilter;
            default:
                return "";
        }
    }

    CheckBoxTreeItem<String> getParseTree(String path, Parser.ObjectType type) {
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
                boolean npcCheck = !type.equals(Parser.ObjectType.COLLECTION) || !subPath.contains("_npc");
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

    void updateParseTree(Parser.ObjectType type, TreeView<String> tree, TextField directory) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                CheckBoxTreeItem<String> treeRoot = getParseTree(directory.getText(), type);
                Platform.runLater(() -> tree.setRoot(treeRoot));
                return null;
            }
        };
        new Thread(task).start();
    }

    void setParseDirectory(Stage stage, TextField directory, TreeView<String> tree, Parser.ObjectType type) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select the location to extract data from.");
        File selected = dirChooser.showDialog(stage);
        if (selected != null) {
            directory.setText(selected.getAbsolutePath());
            updateParseTree(type, tree, directory);
        }
    }

    void updateParseDirectory(TextField filter, TextField directory, TreeView<String> tree, Parser.ObjectType type) {
        setFilter(filter.getText(), type);
        filter.setText(getFilterText(type));
        if (directory.getText() != null) {
            updateParseTree(type, tree, directory);
        }
    }

    public void parse(ProgressBar progressBar, Text progressText, TextArea logger, Parser.ObjectType type) {
        Task<Void> task = getParseTask(type);
        progressBar.progressProperty().bind(task.progressProperty());
        progressText.textProperty().bind(task.messageProperty());

        new Thread(() -> {
            task.run();
            Platform.runLater(() -> {
                selectedPaths.clear();
                if (!failedPaths.isEmpty()) {
                    failedPaths.forEach(item -> print(logger, "Parse failure: " + item));
                    failedPaths.clear();
                }
            });
        }).start();
    }

    public Task<Void> getParseTask(Parser.ObjectType type) {
        return new Task<Void>() {
            @Override
            protected Void call() {
                // clear out old failed paths
                failedPaths.clear();

                // begin parsing
                int selectedPathsLength = selectedPaths.size();
                for (int i = 0; i < selectedPathsLength; i++) {
                    updateMessage("Parsing " + (i + 1) + "/" + selectedPathsLength + " " + type.toString());
                    updateProgress(i, selectedPathsLength);
                    try {
                        model.createObject(selectedPaths.get(i), type);
                    } catch (IOException | ParseException e) {
                        failedPaths.add(selectedPaths.get(i));
                    }
                }
                updateMessage("Parsing complete.");
                updateProgress(selectedPathsLength, selectedPathsLength);

                return null;
            }
        };
    }

    void setEditTabBenchTable(TableView<ObservableBench> table, TableColumn<ObservableBench, String> nameCol, TableColumn<ObservableBench, String> rPathCol) {
        ObservableMap<String, ObservableBench> benches = model.getSessionBenches();
        ObservableList<ObservableBench> benchList = FXCollections.observableArrayList(benches.values());

        rPathCol.setCellValueFactory(cellData -> cellData.getValue().rPathProperty());
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty()); // TODO: when extracted strings become available, map to the names, else use identifier

        table.getColumns().setAll(Arrays.asList(nameCol, rPathCol));
        table.setItems(benchList);

        table.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> model.setCurrentBench(newValue)));

        model.currentBenchProperty().addListener((((observable, oldValue, newValue) -> {
            if (newValue == null) {
                table.getSelectionModel().clearSelection();
            } else {
                table.getSelectionModel().select(newValue);
            }
        })));
    }

    void setEditTabBenchSidebar(TextField rPathField, TextField nameField, TextField professionNameField, ComboBox<String> categoryComboBox, ListView<String> categories) {

        model.currentBenchProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                rPathField.textProperty().unbindBidirectional(oldValue.rPathProperty());
                nameField.textProperty().unbindBidirectional(oldValue.nameProperty());
                professionNameField.textProperty().unbindBidirectional(oldValue.professionNameProperty());
                categoryComboBox.getItems().clear();
                categories.getItems().clear();
            }
            if (newValue == null) {
                rPathField.setText("");
                nameField.setText("");
                professionNameField.setText("");
            } else {
                rPathField.textProperty().bindBidirectional(newValue.rPathProperty());
                nameField.textProperty().bindBidirectional(newValue.nameProperty());
                professionNameField.textProperty().bindBidirectional(newValue.professionNameProperty());
                // add category items stuff
                ObservableList<String> dropDownCategories = FXCollections.observableArrayList();
                newValue.getCategories().keySet().forEach(item -> dropDownCategories.add(String.join(" - ", item)));
                categoryComboBox.setItems(dropDownCategories);
            }
        }));

        categoryComboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            categories.getItems().clear();
            if (newValue != null) {
                List<String> key = Arrays.asList(newValue.split("\\s-\\s"));
                categories.getItems().setAll(model.getCurrentBench().getCategories().get(key));
            }
        }));
    }

    void print(TextArea logger, String message) {
        logger.setText(logger.getText() + "[" + LocalTime.now() + "] " + message + "\n");
    }

    void clearSelectedPaths() {
        selectedPaths.clear();
    }
}
