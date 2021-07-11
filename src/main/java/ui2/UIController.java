package ui2;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import datamodel.DataModel;
import datamodel.Enums;
import datamodel.Serializer;
import datamodel.parser.parsestrategies.ParseException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UIController {

    DataModel model = new DataModel();
    //    String benchFilter = "_interactive";
    String filter = "";
    //    String stringFilter = "prefabs_";
    List<String> selectedPaths = new ArrayList<>();
    List<String> failedPaths = new ArrayList<>();

//    StringProperty logs = new SimpleStringProperty("");

    /**
     * Can return null.
     */
    String loadFile(Stage stage, String fileType, String fileExtension, String promptText) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(promptText);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileType, fileExtension));
        File selected = fileChooser.showOpenDialog(stage);
        return selected == null ? null : selected.getAbsolutePath();
    }

    /**
     * Can return null.
     */
    String loadDirectory(Stage stage, String promptText) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(promptText);
        File selected = dirChooser.showDialog(stage);
        return selected == null ? null : selected.getAbsolutePath();
    }

    void dumpLogs(Stage stage, TextArea logger) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select the location to save the logs at.");
        File selected = dirChooser.showDialog(stage);
        if (selected != null) {
            String path = selected.getAbsolutePath();
            LocalDateTime currTime = LocalDateTime.now();
            String fileName = "log-" + currTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss-SSS")) + ".txt";
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

    void loadBlueprints(String dirPath) {
        model.createBlueprintPaths(dirPath);
    }

    void loadData(TextArea logger, List<Button> buttonsToDisable, List<Button> buttonsToEnable, String mapDirPath) {
        Task<Void> task = getLoadTask(logger, mapDirPath);
        new Thread(() -> {
            task.run();
            Platform.runLater(() -> {
                disableActionButtons(buttonsToDisable);
                enableActionButtons(buttonsToEnable);
            });
            print(logger, "Data loading complete.");
        }).start();
    }

    Task<Void> getLoadTask(TextArea logger, String mapDirPath) {
        return new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    model.createBlueprintMapping(mapDirPath);
                } catch (IOException e) {
                    print(logger, "Error occurred while loading in blueprint map.");
                }
                return null;
            }
        };
    }

    /**
     * @param selected    the selected file from the file selector
     * @param changedOnly if true, export changed data only; else exports everything stored
     * @param selection   an int array of length 9, indicating which categories are to be exported: 0 = not exported, exported otherwise; index-category mapping as follows:
     *                    0: benches
     *                    1: collections
     *                    2: collection indices
     *                    3: gear styles
     *                    4: items
     *                    5: placeables
     *                    6: recipes
     *                    7: skins
     *                    8: strings
     * @param logger      the TextArea used for logging
     */
    void serialize(File selected, boolean changedOnly, boolean usePrettyPrint, int[] selection, TextArea logger) {
        Serializer s = new Serializer();
        Gson serializer = s.getSerializer(usePrettyPrint);
        if (selected != null) {
            if (selection.length != 9) {
                print(logger, "Invalid selection length; probably a programming oversight. No exporting was done.");
                return;
            }
            String dirPath = selected.getAbsolutePath();
            LocalDateTime currTime = LocalDateTime.now();
            String fileName = "trove-entity-" + currTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss-SSS")) + ".json";
            try {
                String path = dirPath + "\\" + fileName;
                JsonWriter writer = serializer.newJsonWriter(new BufferedWriter(new FileWriter(path)));
                writer.beginObject();
                if (selection[0] != 0)
                    s.writeBenches(writer, serializer, changedOnly ? model.getChangedBenches() : model.getSessionBenches());
                if (selection[1] != 0)
                    s.writeCollections(writer, serializer, changedOnly ? model.getChangedCollections() : model.getSessionCollections());
                if (selection[2] != 0)
                    s.writeCollectionIndices(writer, serializer, changedOnly ? model.getChangedCollectionIndices() : model.getSessionCollectionIndices());
                if (selection[3] != 0)
                    s.writeGearStyles(writer, serializer, changedOnly ? model.getChangedGearStyles() : model.getSessionGearStyles());
                if (selection[4] != 0)
                    s.writeItems(writer, serializer, changedOnly ? model.getChangedItems() : model.getSessionItems());
                if (selection[5] != 0)
                    s.writePlaceables(writer, serializer, changedOnly ? model.getChangedPlaceables() : model.getSessionPlaceables());
                if (selection[6] != 0)
                    s.writeRecipes(writer, serializer, changedOnly ? model.getChangedRecipes() : model.getSessionRecipes());
                if (selection[7] != 0)
                    s.writeSkins(writer, serializer, changedOnly ? model.getChangedSkins() : model.getSessionSkins());
                if (selection[8] != 0)
                    s.writeStrings(writer, changedOnly ? model.getChangedStrings() : model.getSessionStrings().getStrings());
                writer.endObject();
                writer.close();
                print(logger, "Data exported to " + path);
            } catch (IOException e) {
                print(logger, "Export failed due to an IOException; stack trace below:");
                List<String> errorList = Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList());
                printList(logger, errorList);
            }
        }
    }

    // PARSING
    CheckBoxTreeItem<String> getParseTree(String path, Enums.ObjectType type) {
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
                boolean npcCheck = !type.equals(Enums.ObjectType.COLLECTION) || !subPath.contains("_npc");
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

    void updateParseTree(Enums.ObjectType type, TreeView<String> tree, TextField directory) {
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

    void setParseDirectory(Stage stage, TextField directory, TreeView<String> tree, Enums.ObjectType type) {
        String selected = loadDirectory(stage, "Select the location to extract data from.");
        if (selected != null) {
            directory.setText(selected);
            updateParseTree(type, tree, directory);
        }
    }

    void updateParseDirectory(TextField filterField, TextField directory, TreeView<String> tree, Enums.ObjectType type) {
        filter = filterField.getText();
        if (directory.getText() != null) {
            updateParseTree(type, tree, directory);
        }
    }

    void setParseTypes(ComboBox<String> typeSelect) {
        ObservableList<String> types = FXCollections.observableArrayList("Benches", "Collections", "Collection indices", "Items", "Gear styles", "Placeables", "Professions", "Recipes", "Skins", "Strings");
        typeSelect.setItems(types);
        typeSelect.getSelectionModel().selectFirst();
    }

    public void parse(ProgressBar progressBar, Text progressText, TextArea logger, String typeString) {
        Enums.ObjectType type = Enums.ObjectType.getType(typeString);
        Task<Void> task = getParseTask(type, logger);
        progressBar.progressProperty().bind(task.progressProperty());
        progressText.textProperty().bind(task.messageProperty());

        new Thread(() -> {
            task.run();
            Platform.runLater(() -> {
                selectedPaths.clear();
                if (!failedPaths.isEmpty()) {
                    failedPaths.clear();
                }
            });
        }).start();
    }

    public Task<Void> getParseTask(Enums.ObjectType type, TextArea logger) {
        return new Task<Void>() {
            @Override
            protected Void call() {
                // clear out old failed paths
                failedPaths.clear();

                // begin parsing
                StringBuilder loggerText = new StringBuilder();
                int selectedPathsLength = selectedPaths.size();
                print(logger, "Parsing " + selectedPathsLength + " objects using parser for " + type.toString().toLowerCase() + ".");
                for (int i = 0; i < selectedPathsLength; i++) {
                    updateMessage("Parsing " + type + ": " + (i + 1) + "/" + selectedPathsLength);
                    updateProgress(i, selectedPathsLength);
                    try {
                        model.createObject(selectedPaths.get(i), type);
                    } catch (IOException | ParseException e) {
                        loggerText.append("[").append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))).append("] ").append("Parse failure: ").append(e.getMessage()).append("\n");
                        failedPaths.add(selectedPaths.get(i));
                    }
                }
                loggerText.append("[").append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))).append("] ").append("Parsing completed.").append("\n");
                printPlain(logger, loggerText.toString());
                updateMessage("Parsing complete.");
                updateProgress(selectedPathsLength, selectedPathsLength);

                return null;
            }
        };
    }

    String getFilterText() {
        return filter;
    }

    void clearSelectedPaths() {
        selectedPaths.clear();
    }

    // LOGGING
    void print(TextArea logger, String message) {
        logger.appendText("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + "] " + message + "\n");
    }

    void printPlain(TextArea logger, String message) {
        logger.appendText(message);
    }

    void printList(TextArea logger, List<String> messages) {
        StringBuilder builder = new StringBuilder();
        messages.forEach(m -> builder.append("[").append(LocalTime.now()).append("] ").append(m).append("\n"));
        logger.appendText(builder.toString());
    }
}
