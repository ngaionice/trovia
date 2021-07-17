package ui2;

import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;
import datamodel.Enums;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Presenter {

    Stage stage;
    Scene scene;
    BorderPane root;
    UIController controller;

    public Presenter(Stage stage, Scene sc, BorderPane root) {
        this.stage = stage;
        this.scene = sc;
        this.root = root;
        this.controller = new UIController();
    }

    public VBox getNavBar() {
        VBox navBox = new VBox();
        Region spacer = new Region();
        JFXButton parseButton = new JFXButton("Parse");
        JFXButton reviewButton = new JFXButton("Review");
        Separator separator = new Separator();
        JFXButton exportButton = new JFXButton("Export");
        JFXButton logsButton = new JFXButton("Logs");
        JFXButton quitButton = new JFXButton("Quit");

        List<Button> actionButtons = Arrays.asList(parseButton, reviewButton, exportButton, logsButton);
        List<JFXButton> buttonList = Arrays.asList(parseButton, reviewButton, exportButton, logsButton, quitButton);
        String[] buttonIds = new String[]{"button-parse", "button-review", "button-export", "button-logs", "button-quit"};

        spacer.prefHeightProperty().bind(scene.heightProperty().multiply(0.125));
        for (int i = 0; i < buttonIds.length; i++) {
            buttonList.get(i).getStyleClass().add("nav-button");
            buttonList.get(i).setGraphic(new FontIcon());
            buttonList.get(i).setId(buttonIds[i]);
        }
        navBox.getChildren().addAll(buttonList);
        navBox.getChildren().add(3, separator);
        navBox.getChildren().add(0, spacer);
        navBox.setId("nav-box");
        separator.getStyleClass().add("nav-separator");

        // set button actions
        parseButton.setOnAction(e -> setParseScreen());
        reviewButton.setOnAction(e -> setReviewScreen());

        exportButton.setOnAction(e -> setExportScreen());

        logsButton.setOnAction(e -> setLogsScreen());
        quitButton.setOnAction(e -> runQuitSequence());

        controller.disableActionButtons(actionButtons);
        root.setCenter(getScreenBorderPaneWithLogger("Setup", getSetupScreenContent(actionButtons)));
        return navBox;
    }

    private Pane getSetupScreenContent(List<Button> actionButtons) {
        StackPane center = new StackPane();
        AnchorPane anchor = new AnchorPane();
        GridPane grid = new GridPane();

        Text externalText = new Text("Load existing data");
        JFXTextField dataLoc = new JFXTextField();
        JFXButton dataLocButton = getJFXButton(Arrays.asList("button-inline", "color-subtle"), "button-set-dir");

        Separator sep = new Separator();
        Text mapText = new Text("Parsing aids");
        JFXTextField bppLoc = new JFXTextField();
        JFXButton bppLocButton = getJFXButton(Arrays.asList("button-inline", "color-subtle"), "button-set-dir");

        Separator sep2 = new Separator();
        Text configText = new Text("Additional configuration");
        JFXCheckBox weirdBox = new JFXCheckBox("Non-standard folder structure");

        Separator sep3 = new Separator();

        Text ignoreText = new Text("Press the start button to load in the data, or to skip data loading and start parsing data.\nIf you do not load data now, you will not be able to do so later.");

        JFXButton startButton = getJFXButton(Collections.singletonList("floating-button"), "button-start");

        dataLocButton.setOnAction(e -> dataLoc.setText(controller.loadFile(stage, "JSON files", "*.json", "Select the JSON file containing the entities.")));
        bppLocButton.setOnAction(e -> bppLoc.setText(controller.loadDirectory(stage, "Select the directory containing the mapping files. This directory normally has a relative path of /prefabs/blocks.")));
        startButton.setOnAction(e -> controller.loadData(Arrays.asList(dataLocButton, bppLocButton, startButton), actionButtons, dataLoc.getText(), bppLoc.getText(), weirdBox.selectedProperty().getValue()));

        // element styling
        {
            dataLoc.setPromptText("Entity JSON file location");
            bppLoc.setPromptText("Blueprint-placeable mapping supplementary files directory (skip if not parsing placeables)");
            dataLoc.getStyleClass().add("text-field-dir");
            dataLoc.setDisable(true);
            bppLoc.setDisable(true);
            Arrays.asList(externalText, mapText, configText, ignoreText).forEach(text -> text.getStyleClass().add("text-normal"));
            center.getStyleClass().add("pane-background");
            anchor.getStyleClass().add("card-backing");
            grid.getStyleClass().add("grid-content");
        }

        // placing items into grid
        {
            center.getChildren().add(anchor);
            anchor.getChildren().add(grid);
            anchor.getChildren().add(startButton);
            grid.add(externalText, 0, 0);
            grid.add(dataLoc, 0, 1);
            grid.add(dataLocButton, 1, 1);
            grid.add(sep, 0, 2, 2, 1);
            grid.add(mapText, 0, 3);
            grid.add(bppLoc, 0, 4);
            grid.add(bppLocButton, 1, 4);
            grid.add(sep2, 0, 5, 2, 1);
            grid.add(configText, 0, 6);
            grid.add(weirdBox, 0, 7);
            grid.add(sep3, 0, 8, 2, 1);
            grid.add(ignoreText, 0, 9, 2, 1);
        }

        setMaxAnchor(grid);
        setFabAnchor(startButton);

        JFXDepthManager.setDepth(anchor, 1);
        return center;
    }

    private void setParseScreen() {
        root.setCenter(getScreenBorderPaneWithLogger("Parse", getParseScreenContent()));
    }

    private Pane getParseScreenContent() {
        StackPane center = new StackPane();
        AnchorPane anchor = new AnchorPane();
        GridPane grid = new GridPane();
        JFXTextField directory = new JFXTextField();
        JFXButton dirButton = getJFXButton(Arrays.asList("button-inline", "color-subtle"), "button-set-dir");
        JFXTextField filter = new JFXTextField();
        JFXButton filterButton = getJFXButton(Arrays.asList("button-inline", "color-subtle"), "button-update");
        JFXComboBox<String> typeSelect = new JFXComboBox<>();
        TreeView<String> tree = new TreeView<>();
        JFXButton startButton = getJFXButton(Collections.singletonList("floating-button"), "button-start");
        JFXProgressBar progressBar = new JFXProgressBar();
        Text progressText = new Text("Status");
        VBox progressBox = new VBox();

        directory.setPromptText("Directory");
        directory.setDisable(true);
        filter.setPromptText("Filter");
        filter.setText(controller.getFilterText());
        typeSelect.setPromptText("Parse Type");
        progressBar.setProgress(0);

        controller.setParseTypes(typeSelect);
        dirButton.setOnAction(e -> {
            controller.setParseDirectory(stage, directory, tree, Enums.ObjectType.getType(typeSelect.getValue()));
            controller.clearSelectedPaths();
        });
        filterButton.setOnAction(e -> {
            controller.updateParseDirectory(filter, directory, tree, Enums.ObjectType.getType(typeSelect.getValue()));
            controller.clearSelectedPaths();
        });
        startButton.setOnAction(e -> controller.parse(progressBar, progressText, typeSelect.getValue()));

        center.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");
        grid.getStyleClass().add("grid-content");
        directory.getStyleClass().add("text-field-dir");
        filter.getStyleClass().add("text-field-filter");
        tree.getStyleClass().add("dir-view");
        progressText.getStyleClass().add("text-normal");
        progressText.setId("text-progress");

        tree.setCellFactory(CheckBoxTreeCell.forTreeView());
        tree.prefWidthProperty().bind(center.widthProperty());
        tree.prefHeightProperty().bind(center.heightProperty().multiply(0.68));
        progressBar.prefWidthProperty().bind(center.widthProperty().multiply(0.80));

        center.getChildren().add(anchor);
        anchor.getChildren().add(grid);
        anchor.getChildren().add(startButton);
        progressBox.getChildren().addAll(progressText, progressBar);
        grid.add(directory, 0, 0);
        grid.add(dirButton, 1, 0);
        grid.add(filter, 2, 0);
        grid.add(filterButton, 3, 0);
        grid.add(typeSelect, 4, 0);
        grid.add(progressBox, 0, 1, 5, 1);
        grid.add(tree, 0, 2, 5, 1);

        setMaxAnchor(grid);
        setFabAnchor(startButton);

        JFXDepthManager.setDepth(anchor, 1);
        return center;
    }

    private void setReviewScreen() {
        root.setCenter(getScreenBorderPaneWithLogger("Review", getReviewScreenContent()));
    }

    private Pane getReviewScreenContent() {
        StackPane center = new StackPane();
        AnchorPane anchor = new AnchorPane();
        GridPane grid = new GridPane();
        String[] types = new String[]{"Benches", "Collections", "Collection indices", "Gear styles", "Items", "Placeables", "Recipes", "Skins", "Strings"};

        JFXComboBox<String> categories = new JFXComboBox<>();
        categories.setPromptText("Category");

        Separator sepVert = new Separator();
        sepVert.setOrientation(Orientation.VERTICAL);

        Text overview = new Text("Unmerged changes:");
        List<Text> countTexts = new ArrayList<>();
        for (int i = 0; i < 9; i++) countTexts.add(new Text());

        VBox counts = new VBox();
        counts.getChildren().addAll(countTexts);

        ListView<String> lv = new ListView<>();

        JFXNodesList buttons = new JFXNodesList();
        JFXButton actions = getJFXButton(Collections.singletonList("floating-button"), "button-options");
        JFXButton mergeButton = getJFXButton(Collections.singletonList("floating-sub-button"), "button-merge");
        JFXButton undoButton = getJFXButton(Collections.singletonList("floating-sub-button"), "button-undo");

        buttons.addAnimatedNode(actions);
        buttons.addAnimatedNode(mergeButton);
        buttons.addAnimatedNode(undoButton);

        grid.add(categories, 0, 0);
        grid.add(overview, 0, 1);
        grid.add(counts, 0, 2);
        grid.add(sepVert, 1, 0, 1, 3);
        grid.add(lv, 2, 0, 1, 3);

        center.getChildren().add(anchor);
        anchor.getChildren().add(grid);
        anchor.getChildren().add(buttons);

        controller.rScreenBindCountTexts(countTexts, types);
        controller.rScreenSetupDataViews(types, categories, lv);
        controller.rScreenSetupActionButtons(mergeButton, undoButton, categories, lv);

        overview.getStyleClass().add("text-bold");
        countTexts.forEach(t -> t.getStyleClass().add("text-normal"));
        counts.getStyleClass().add("list-box");
        buttons.getStyleClass().add("node-list");
        center.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");
        grid.getStyleClass().add("grid-content");
        lv.prefWidthProperty().bind(center.widthProperty());
        lv.prefHeightProperty().bind(center.heightProperty());

        setMaxAnchor(grid);
        setFabAnchor(buttons);

        JFXDepthManager.setDepth(anchor, 1);
        return center;
    }

    private void setExportScreen() {
        root.setCenter(getScreenBorderPaneWithLogger("Export", getExportScreenContent()));
    }

    private Pane getExportScreenContent() {
        StackPane center = new StackPane();
        AnchorPane anchor = new AnchorPane();
        GridPane grid = new GridPane();
        JFXTextField directory = new JFXTextField();
        JFXButton dirButton = getJFXButton(Arrays.asList("button-inline", "color-subtle"), "button-set-dir");
        JFXButton startButton = getJFXButton(Collections.singletonList("floating-button"), "button-dump");
        JFXToggleButton exportTypeToggle = new JFXToggleButton();
        JFXToggleButton prettyPrintToggle = new JFXToggleButton();
        Separator separator = new Separator();

        BooleanProperty[] selected = new BooleanProperty[9];
        String[] texts = new String[]{"Benches", "Collections", "Collection Indices", "Gear Styles", "Items", "Placeables", "Recipes", "Skins", "Strings"};
        VBox types = new VBox();
        VBox toggles = new VBox();

        List<JFXCheckBox> checkboxes = new ArrayList<>();
        for (int i = 0; i < selected.length; i++) {
            selected[i] = new SimpleBooleanProperty(true);
            JFXCheckBox cb = new JFXCheckBox(texts[i]);
            cb.selectedProperty().bindBidirectional(selected[i]);
            cb.setSelected(false);
            checkboxes.add(cb);
        }
        types.getChildren().addAll(checkboxes);
        toggles.getChildren().addAll(prettyPrintToggle, exportTypeToggle);

        directory.setPromptText("Directory");
        directory.setDisable(true);
        exportTypeToggle.setText("Export session data and merged changes");
        prettyPrintToggle.setText("Pretty printing");
        prettyPrintToggle.setSelected(true);

        exportTypeToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                exportTypeToggle.setText("Export unmerged changes");
            } else {
                exportTypeToggle.setText("Export session data and merged changes");
            }
        });
        prettyPrintToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                prettyPrintToggle.setText("Use pretty printing");
            } else {
                prettyPrintToggle.setText("Use compressed mode");
            }
        });
        dirButton.setOnAction(e -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Select the location to export data to.");
            File selectedPath = dirChooser.showDialog(stage);
            if (selectedPath != null) {
                directory.setText(selectedPath.getAbsolutePath());
            }
        });
        startButton.setOnAction(e -> {
            File selectedDir = new File(directory.getText());
            if (directory.getText() != null && selectedDir.isDirectory()) {
                int[] selection = new int[selected.length];
                for (int i = 0; i < selection.length; i++) {
                    if (selected[i].getValue()) selection[i] = 1;
                }
                controller.serialize(selectedDir, exportTypeToggle.selectedProperty().getValue(), prettyPrintToggle.selectedProperty().getValue(), selection);
            }
        });

        center.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");
        grid.getStyleClass().add("grid-content");
        directory.getStyleClass().add("text-field-dir");
        types.getStyleClass().add("list-box");
        toggles.getStyleClass().add("list-box-negative");
        separator.setOrientation(Orientation.VERTICAL);

        center.getChildren().add(anchor);
        anchor.getChildren().add(grid);
        anchor.getChildren().add(startButton);
        grid.add(directory, 0, 0, 3, 1);
        grid.add(dirButton, 3, 0);
        grid.add(types, 0, 1);
        grid.add(toggles, 2, 1);
        grid.add(separator, 1, 1);
//        int row = 1;
//        for (int i = 0; i < checkboxes.size(); i++) {
//            grid.add(checkboxes.get(i), 0, i + row);
//        }

        setMaxAnchor(grid);
        setFabAnchor(startButton);

        JFXDepthManager.setDepth(anchor, 1);
        return center;
    }

    private BorderPane getScreenBorderPaneWithLogger(String headerString, Node content) {
        BorderPane screenRoot = new BorderPane();
        HBox header = new HBox();
        Text headerText = new Text(headerString);

        header.getChildren().add(headerText);
        header.getStyleClass().add("header");
        headerText.getStyleClass().add("header-text");

        screenRoot.setTop(header);
        screenRoot.setCenter(content);
        screenRoot.setBottom(getLogsRegion());
        return screenRoot;
    }

    private void setLogsScreen() {
        BorderPane screenRoot = new BorderPane();
        StackPane stack = new StackPane();
        AnchorPane anchor = new AnchorPane();
        HBox header = new HBox();
        Text headerText = new Text("Logs");
        JFXButton dumpButton = getJFXButton(Collections.singletonList("floating-button"), "button-dump");
        ListView<String> logger = getLogger();

        dumpButton.setOnAction(e -> controller.dumpLogs(stage));

        header.getChildren().add(headerText);
        stack.getChildren().add(anchor);
        anchor.getChildren().add(logger);
        anchor.getChildren().add(dumpButton);

        header.getStyleClass().add("header");
        headerText.getStyleClass().add("header-text");
        stack.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");

        setMaxAnchor(logger);
        setFabAnchor(dumpButton);

        JFXDepthManager.setDepth(anchor, 1);

        screenRoot.setTop(header);
        screenRoot.setCenter(stack);
        root.setCenter(screenRoot);
    }

    private StackPane getLogsRegion() {
        StackPane root = new StackPane();
        StackPane loggerWrapper = new StackPane();
        root.getStyleClass().add("pane-background-no-top");
        root.getChildren().add(loggerWrapper);
        loggerWrapper.getChildren().add(getLogger());
        JFXDepthManager.setDepth(loggerWrapper, 1);

        return root;
    }

    private ListView<String> getLogger() {
        ListView<String> logger = new ListView<>();
        controller.setupLogger(logger);
        logger.setId("logger");
        return logger;
    }

    private void runQuitSequence() {
        // need to add more stuff such as checking data is saved, etc
        Platform.exit();
    }

    // HELPER METHODS

    private void setMaxAnchor(Node node) {
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
    }

    private void setFabAnchor(Node node) {
        AnchorPane.setRightAnchor(node, 36.0);
        AnchorPane.setBottomAnchor(node, 36.0);
    }

    JFXButton getJFXButton(List<String> cssClasses, String cssId) {
        JFXButton button = new JFXButton();
        button.setGraphic(new FontIcon());
        button.setId(cssId);
        cssClasses.forEach(c -> button.getStyleClass().add(c));
        return button;
    }
}
