package ui2;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import datamodel.CollectionEnums;
import datamodel.DataModel;
import datamodel.objects.*;
import datamodel.parser.Parser;
import datamodel.parser.parsestrategies.ParseException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UIController {

    enum TabType {
        BENCH("bench"),
        COLLECTION("collection"),
        ITEM("item"),
        PLACEABLE("placeable"),
        RECIPE("recipe");

        private final String string;

        TabType(String name) {
            string = name;
        }

        @Override
        public String toString() {
            return string;
        }
    }

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

    void print(TextArea logger, String message) {
        logger.setText(logger.getText() + "[" + LocalTime.now() + "] " + message + "\n");
    }

    void clearSelectedPaths() {
        selectedPaths.clear();
    }

    void setEditTabTable(TableView<ArticleTable> table, TableColumn<ArticleTable, String> rPathCol, TableColumn<ArticleTable, String> nameCol, TabType type) {
        switch (type) {
            case BENCH:
                ObservableMap<String, ObservableBench> benches = model.getSessionBenches();
                ObservableList<ArticleTable> benchList = FXCollections.observableArrayList(benches.values());
                table.setItems(benchList);
                table.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> model.setCurrentBench((ObservableBench) newValue)));
                model.currentBenchProperty().addListener((((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        table.getSelectionModel().clearSelection();
                    } else {
                        table.getSelectionModel().select(newValue);
                    }
                })));
                break;
            case COLLECTION:
                ObservableMap<String, ObservableCollection> collections = model.getSessionCollections();
                ObservableList<ArticleTable> collectionList = FXCollections.observableArrayList(collections.values());
                table.setItems(collectionList);
                table.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> model.setCurrentCollection((ObservableCollection) newValue)));
                model.currentCollectionProperty().addListener((((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        table.getSelectionModel().clearSelection();
                    } else {
                        table.getSelectionModel().select(newValue);
                    }
                })));
                break;
            case ITEM:
                ObservableMap<String, ObservableItem> items = model.getSessionItems();
                ObservableList<ArticleTable> itemList = FXCollections.observableArrayList(items.values());
                table.setItems(itemList);
                table.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> model.setCurrentItem((ObservableItem) newValue)));
                model.currentItemProperty().addListener((((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        table.getSelectionModel().clearSelection();
                    } else {
                        table.getSelectionModel().select(newValue);
                    }
                })));
                break;
            case PLACEABLE:
                ObservableMap<String, ObservablePlaceable> placeables = model.getSessionPlaceables();
                ObservableList<ArticleTable> placeableList = FXCollections.observableArrayList(placeables.values());
                table.setItems(placeableList);
                table.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> model.setCurrentPlaceable((ObservablePlaceable) newValue)));
                model.currentPlaceableProperty().addListener((((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        table.getSelectionModel().clearSelection();
                    } else {
                        table.getSelectionModel().select(newValue);
                    }
                })));
                break;
            case RECIPE:
                ObservableMap<String, ObservableRecipe> recipes = model.getSessionRecipes();
                ObservableList<ArticleTable> recipeList = FXCollections.observableArrayList(recipes.values());
                table.setItems(recipeList);
                table.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> model.setCurrentRecipe((ObservableRecipe) newValue)));
                model.currentRecipeProperty().addListener((((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        table.getSelectionModel().clearSelection();
                    } else {
                        table.getSelectionModel().select(newValue);
                    }
                })));
                break;
        }
        rPathCol.setCellValueFactory(cellData -> cellData.getValue().rPathProperty());
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty()); // TODO: when extracted strings become available, map to the names, else use identifier
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

    void setEditTabCollectionSidebar(TextField rPathField, TextField nameField, TextField descField, TextField troveMRField,
                                     TextField geodeMRField, ListView<String> types,
                                     TableView<ObservableMap<CollectionEnums.Property, Double>> properties,
                                     TableColumn<ObservableMap<CollectionEnums.Property, Double>, String> propCol,
                                     TableColumn<ObservableMap<CollectionEnums.Property, Double>, Double> propValCol,
                                     TableView<ObservableMap<CollectionEnums.Buff, Double>> buffs,
                                     TableColumn<ObservableMap<CollectionEnums.Buff, Double>, String> buffCol,
                                     TableColumn<ObservableMap<CollectionEnums.Buff, Double>, Double> buffValCol, TextArea notes) {
        model.currentCollectionProperty().addListener(((observable, oldValue, newValue) -> {
            types.getItems().clear();
            properties.getItems().clear();
            buffs.getItems().clear();
            if (oldValue != null) {
                rPathField.textProperty().unbindBidirectional(oldValue.rPathProperty());
                nameField.textProperty().unbindBidirectional(oldValue.nameProperty());
                descField.textProperty().unbindBidirectional(oldValue.descProperty());
                troveMRField.textProperty().unbindBidirectional(oldValue.troveMRProperty());
                geodeMRField.textProperty().unbindBidirectional(oldValue.geodeMRProperty());
            }
            if (newValue == null) {
                rPathField.setText("");
                nameField.setText("");
                descField.setText("");
                troveMRField.setText("");
                geodeMRField.setText("");
                notes.setText("");
            } else {
                rPathField.textProperty().bindBidirectional(newValue.rPathProperty());
                nameField.textProperty().bindBidirectional(newValue.nameProperty());
                descField.textProperty().bindBidirectional(newValue.descProperty());
                troveMRField.textProperty().bindBidirectional(newValue.troveMRProperty(), new NumberStringConverter());
                geodeMRField.textProperty().bindBidirectional(newValue.geodeMRProperty(), new NumberStringConverter());

                // TODO: finish properties, buffs and notes
                newValue.getTypes().forEach(item -> types.getItems().add(item.toString()));
            }
        }));
    }

    void setEditTabItemSidebar(TextField rPathField, TextField nameField, TextField descField, CheckBox tradableBox,
                               TableView<ObservableMap<String, Integer>> decons, ComboBox<String> lootComboBox, TableView<ObservableMap<String, String>> loot, JFXTextArea notes) {
        lootComboBox.getItems().addAll("Common", "Uncommon", "Rare");
        model.currentItemProperty().addListener(((observable, oldValue, newValue) -> {
            decons.getItems().clear();
            loot.getItems().clear();
            if (oldValue != null) {
                rPathField.textProperty().unbindBidirectional(oldValue.rPathProperty());
                nameField.textProperty().unbindBidirectional(oldValue.nameProperty());
                descField.textProperty().unbindBidirectional(oldValue.descProperty());
                tradableBox.selectedProperty().unbindBidirectional(oldValue.tradableProperty());
            }
            if (newValue == null) {
                rPathField.setText("");
                nameField.setText("");
                descField.setText("");
                tradableBox.setSelected(false);
                notes.setText("");
            } else {
                rPathField.textProperty().bindBidirectional(newValue.rPathProperty());
                nameField.textProperty().bindBidirectional(newValue.nameProperty());
                descField.textProperty().bindBidirectional(newValue.descProperty());
                tradableBox.selectedProperty().bindBidirectional(newValue.tradableProperty());
                // TODO: finish decons, loot, notes
            }
        }));
    }

    void setEditTabPlaceableSidebar(TextField rPathField, TextField nameField, TextField descField, CheckBox tradableBox, JFXTextArea notes) {
        model.currentPlaceableProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                rPathField.textProperty().unbindBidirectional(oldValue.rPathProperty());
                nameField.textProperty().unbindBidirectional(oldValue.nameProperty());
                descField.textProperty().unbindBidirectional(oldValue.descProperty());
                tradableBox.selectedProperty().unbindBidirectional(oldValue.tradableProperty());
            }
            if (newValue == null) {
                rPathField.setText("");
                nameField.setText("");
                descField.setText("");
                tradableBox.setSelected(false);
                notes.setText("");
            } else {
                rPathField.textProperty().bindBidirectional(newValue.rPathProperty());
                nameField.textProperty().bindBidirectional(newValue.nameProperty());
                descField.textProperty().bindBidirectional(newValue.descProperty());
                tradableBox.selectedProperty().bindBidirectional(newValue.tradableProperty());
                // TODO: finish notes
            }
        }));
    }

    void setEditTabRecipeSidebar(JFXTextField rPathField, JFXTextField nameField,
                                 TableView<MapEntry<String, Integer>> costs, TableColumn<MapEntry<String, Integer>, String> costNameCol,
                                 TableColumn<MapEntry<String, Integer>, Integer> costValCol, TableView<MapEntry<String, Integer>> output,
                                 TableColumn<MapEntry<String, Integer>, String> outputNameCol, TableColumn<MapEntry<String, Integer>, Integer> outputValCol) {
        model.currentRecipeProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                rPathField.textProperty().unbindBidirectional(oldValue.rPathProperty());
                nameField.textProperty().unbindBidirectional(oldValue.nameProperty());
                costs.getItems().clear();
                output.getItems().clear();
            }
            if (newValue == null) {
                rPathField.setText("");
                nameField.setText("");
            } else {
                rPathField.textProperty().bindBidirectional(newValue.rPathProperty());
                nameField.textProperty().bindBidirectional(newValue.nameProperty());

                ObservableMap<String, Integer> costMap = FXCollections.observableHashMap();
                ObservableList<MapEntry<String, Integer>> costEntries = FXCollections.observableArrayList();
                costs.setItems(costEntries);
                addMapTableListener(costMap, costEntries);

                ObservableMap<String, Integer> outputMap = FXCollections.observableHashMap();
                ObservableList<MapEntry<String, Integer>> outputEntries = FXCollections.observableArrayList();
                output.setItems(outputEntries);
                addMapTableListener(outputMap, outputEntries);

                newValue.getCosts().forEach(costMap::put);
                newValue.getOutput().forEach(outputMap::put);
            }
        }));
        costNameCol.setCellValueFactory(cd -> Bindings.createStringBinding(() -> cd.getValue().getKey()));
        costValCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getValue()).asObject());
        outputNameCol.setCellValueFactory(cd -> Bindings.createStringBinding(() -> cd.getValue().getKey()));
        outputValCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getValue()).asObject());
    }

    void addMapTableListener(ObservableMap<String, Integer> map, ObservableList<MapEntry<String, Integer>> entries) {
        map.addListener((MapChangeListener.Change<? extends String, ? extends Integer> change) -> {
            boolean removed = change.wasRemoved();
            if (removed != change.wasAdded()) {
                if (removed) {
                    // no put for existing key
                    // remove pair completely
                    entries.remove(new MapEntry<>(change.getKey(), (Integer) null));
                } else {
                    // add new entry
                    entries.add(new MapEntry<>(change.getKey(), change.getValueAdded()));
                }
            } else {
                // replace existing entry
                MapEntry<String, Integer> entry = new MapEntry<>(change.getKey(), change.getValueAdded());

                int index = entries.indexOf(entry);
                entries.set(index, entry);
            }
        });
    }

    public final class MapEntry<K, V> {

        private final K key;
        private final V value;

        public MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            // check equality only based on keys
            if (obj instanceof MapEntry) {
                MapEntry<?, ?> other = (MapEntry<?, ?>) obj;
                return Objects.equals(key, other.key);
            } else {
                return false;
            }
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

    }
}
