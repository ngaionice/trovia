package ui2;

import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;
import datamodel.Enums;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Presenter {

    Stage stage;
    Scene scene;
    BorderPane root;
    UIController controller;
    TextArea logger;

    public Presenter(Stage stage, Scene sc, BorderPane root) {
        this.stage = stage;
        this.scene = sc;
        this.root = root;
        this.controller = new UIController();
        this.logger = getLogger();
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
        root.setCenter(getScreenBorderPaneWithLogger("Load", getLoadScreenContent(actionButtons)));
        return navBox;
    }

    private Pane getLoadScreenContent(List<Button> actionButtons) {
        StackPane center = new StackPane();
        AnchorPane anchor = new AnchorPane();
        GridPane grid = new GridPane();

        Text externalText = new Text("Load existing data (coming soon)");
        JFXTextField dataLoc = new JFXTextField();
        JFXButton dataLocButton = new JFXButton();

        Separator sep = new Separator();
        Text mapText = new Text("Parsing aids");
        JFXTextField bppLoc = new JFXTextField();
        JFXButton bppLocButton = new JFXButton();

        Separator sep2 = new Separator();
        Text ignoreText = new Text("Press the start button to load in the data, or to skip data loading and start parsing data.\nIf you do not load data now, you will not be able to do so later.");

        JFXButton startButton = new JFXButton();

        dataLocButton.setOnAction(e -> dataLoc.setText(controller.loadFile(stage, "JSON files", "*.json", "Select the JSON file containing the entities.")));
        bppLocButton.setOnAction(e -> bppLoc.setText(controller.loadDirectory(stage, "Select the directory containing the mapping files. This directory normally has a relative path of /prefabs/blocks.")));
        startButton.setOnAction(e -> controller.loadData(logger, Arrays.asList(dataLocButton, bppLocButton, startButton), actionButtons, dataLoc.getText() ,bppLoc.getText()));

        // element styling
        {
            dataLoc.setPromptText("Entity JSON file location");
            bppLoc.setPromptText("Blueprint-placeable mapping files directory (skip if not parsing blueprints)");
            dataLoc.getStyleClass().add("text-field-dir");
            dataLoc.setDisable(true);
            bppLoc.setDisable(true);
            startButton.getStyleClass().addAll("floating-button", "button-start");
            Arrays.asList(dataLocButton, bppLocButton).forEach(b -> {
                b.setId("button-set-dir");
                b.getStyleClass().addAll("button-inline", "color-subtle");
            });
            Arrays.asList(externalText, mapText, ignoreText).forEach(text -> text.getStyleClass().add("text-normal"));
            center.getStyleClass().add("pane-background");
            anchor.getStyleClass().add("card-backing");
            grid.getStyleClass().add("grid-content");

            dataLocButton.setGraphic(new FontIcon());
            bppLocButton.setGraphic(new FontIcon());
            startButton.setGraphic(new FontIcon());
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
            grid.add(ignoreText, 0, 6, 2, 1);
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
        JFXButton dirButton = new JFXButton();
        JFXTextField filter = new JFXTextField();
        JFXButton filterButton = new JFXButton();
        JFXComboBox<String> typeSelect = new JFXComboBox<>();
        TreeView<String> tree = new TreeView<>();
        JFXButton startButton = new JFXButton();
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
        startButton.setOnAction(e -> controller.parse(progressBar, progressText, logger, typeSelect.getValue()));

        center.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");
        grid.getStyleClass().add("grid-content");
        directory.getStyleClass().add("text-field-dir");
        filter.getStyleClass().add("text-field-filter");
        tree.getStyleClass().add("dir-view");
        dirButton.getStyleClass().addAll("button-inline", "color-subtle");
        filterButton.getStyleClass().addAll("button-inline", "color-subtle");
        startButton.getStyleClass().addAll("floating-button", "button-start");
        dirButton.setId("button-set-dir");
        filterButton.setId("button-update");
        progressText.getStyleClass().add("text-normal");
        progressText.setId("text-progress");

        dirButton.setGraphic(new FontIcon());
        filterButton.setGraphic(new FontIcon());
        startButton.setGraphic(new FontIcon());
        tree.setCellFactory(CheckBoxTreeCell.forTreeView());
        tree.prefWidthProperty().bind(center.widthProperty().multiply(0.80));
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
        categories.setPromptText("Current category");

        Separator sepVert = new Separator();
        sepVert.setOrientation(Orientation.VERTICAL);

        Text overview = new Text("Change counts:");
        List<Text> countTexts = new ArrayList<>();
        for (int i = 0; i < 9; i++) countTexts.add(new Text());

        VBox counts = new VBox();
        counts.setSpacing(4);
        counts.getChildren().addAll(countTexts);

        ListView<String> changedView = new ListView<>();


        overview.getStyleClass().add("text-normal");
        countTexts.forEach(t -> t.getStyleClass().add("text-normal"));

        controller.rScreenBindCountTexts(countTexts, types);
        controller.rScreenSetupDataViews(types, categories, changedView);

        grid.add(categories, 0, 0);
        grid.add(overview, 0, 1);
        grid.add(counts, 0, 2);
        grid.add(sepVert, 1, 0, 1, 3);
        grid.add(changedView, 2, 0, 1, 3);

        center.getChildren().add(anchor);
        anchor.getChildren().add(grid);

        center.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");
        grid.getStyleClass().add("grid-content");

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
        JFXButton dirButton = new JFXButton();
        JFXButton startButton = new JFXButton();
        JFXCheckBox changedButton = new JFXCheckBox();
        JFXCheckBox prettyPrintButton = new JFXCheckBox();
        Separator separator = new Separator();

        BooleanProperty[] selected = new BooleanProperty[9];
        String[] texts = new String[]{"Benches", "Collections", "Collection Indices", "Gear Styles", "Items", "Placeables", "Recipes", "Skins", "Strings"};

        List<JFXCheckBox> checkboxes = new ArrayList<>();
        for (int i = 0; i < selected.length; i++) {
            selected[i] = new SimpleBooleanProperty(true);
            JFXCheckBox cb = new JFXCheckBox(texts[i]);
            cb.selectedProperty().bindBidirectional(selected[i]);
            cb.setSelected(false);
            checkboxes.add(cb);
        }
        prettyPrintButton.setSelected(true);

        directory.setPromptText("Directory");
        directory.setDisable(true);
        changedButton.setText("Export changes only");
        prettyPrintButton.setText("Pretty printing");

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
                controller.serialize(selectedDir, changedButton.selectedProperty().getValue(), prettyPrintButton.selectedProperty().getValue(), selection, logger);
            }
        });

        center.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");
        grid.getStyleClass().add("grid-content");
        directory.getStyleClass().add("text-field-dir");
        dirButton.getStyleClass().addAll("button-inline", "color-subtle");
        startButton.getStyleClass().addAll("floating-button", "button-start");
        dirButton.setId("button-set-dir");

        dirButton.setGraphic(new FontIcon());
        startButton.setGraphic(new FontIcon());

        center.getChildren().add(anchor);
        anchor.getChildren().add(grid);
        anchor.getChildren().add(startButton);
        grid.add(directory, 0, 0, 2, 1);
        grid.add(dirButton, 2, 0);
        grid.add(changedButton, 1, 1);
        grid.add(prettyPrintButton, 0, 1);
        grid.add(separator, 0, 2, 3, 1);
        int row = 3;
        for (int i = 0; i < checkboxes.size(); i++) {
            if ((i % 2) == 0) {
                grid.add(checkboxes.get(i), 0, row);
            } else {
                grid.add(checkboxes.get(i), 1, row);
                row++;
            }
        }

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
        JFXButton dumpButton = new JFXButton();

        dumpButton.setOnAction(e -> controller.dumpLogs(stage, logger));

        header.getChildren().add(headerText);
        stack.getChildren().add(anchor);
        anchor.getChildren().add(logger);
        anchor.getChildren().add(dumpButton);

        header.getStyleClass().add("header");
        headerText.getStyleClass().add("header-text");
        stack.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");
        dumpButton.getStyleClass().add("floating-button");
        dumpButton.setId("button-dump");

        dumpButton.setGraphic(new FontIcon());

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
        loggerWrapper.getChildren().add(logger);
        JFXDepthManager.setDepth(loggerWrapper, 1);

        return root;
    }

    private TextArea getLogger() {
        TextArea logger = new JFXTextArea();
        logger.setEditable(false);
        logger.setWrapText(true);
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
}
