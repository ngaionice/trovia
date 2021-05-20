package ui2;

import datamodel.DataModel;
import datamodel.parser.parsestrategies.ParseException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import datamodel.parser.Parser;

import java.io.*;
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

    DataModel model;
    String benchFilter = "_interactive";
    String collectionFilter = "";
    String itemFilter = "";
    String placeableFilter = "";
    String stringFilter = "prefabs_";
    String recipeFilter = "";

    List<String> selectedPaths = new ArrayList<>();
    List<String> failedPaths = new ArrayList<>();

    void loadDatabase(Stage stage, TextArea logger) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Database Files", "*.db"));
        File selected = fileChooser.showOpenDialog(stage);
        if (selected != null) {
            String path = selected.getAbsolutePath();
            String lang = "en";     // need to make mini-dialog to select language, but setting to english for now
            try {
                model = new DataModel(path, lang);
                print(logger, "Database loaded from " + path);
            } catch (SQLException ex) {
                // do something
            }
        }
    }

    void createDatabase(Stage stage, TextArea logger) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select the location to save the database at.");
        File selected = dirChooser.showDialog(stage);
        if (selected != null) {
            String path = selected.getAbsolutePath();
            System.out.println(path);
            // create new database at the specified path
        }
    }

    void dumpLogs(Stage stage, TextArea logger) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select the location to save the logs at.");
        File selected = dirChooser.showDialog(stage);
        if (selected != null) {
            String path = selected.getAbsolutePath();
            LocalDateTime currTime = LocalDateTime.now();
            String fileName = "trovia-log-" + currTime.toString().replace(":", "-").replace(".", "-") + ".txt";
            try  {
                Path file = Paths.get(path + "\\" + fileName);
                Files.write(file, Arrays.asList(logger.getText().split("\\n")), StandardCharsets.UTF_8);
                logger.clear();
                print(logger, "Dumped log at " + currTime.toLocalTime());
            } catch (IOException e) {
                print(logger, "Log dump failed due to an IOException, with stack trace below:");
                Arrays.asList(e.getStackTrace()).forEach(error -> print(logger, error.toString()));
            }
        }
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
}
