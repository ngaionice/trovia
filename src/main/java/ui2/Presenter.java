package ui2;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Presenter {

    Stage stage;
    Scene scene;
    BorderPane root;
    LogicController logic;

    public Presenter(Stage stage, Scene sc, BorderPane root) {
        this.stage = stage;
        this.scene = sc;
        this.root = root;
        this.logic = new LogicController();
    }

    public VBox getNavBar() {
        // note that we will need the other components later on to set screens

        VBox navBox = new VBox();
        Region spacer = new Region();
        JFXButton parseButton = new JFXButton("Parse");
        JFXButton editButton = new JFXButton("Edit");
        JFXButton syncButton = new JFXButton("Sync");
        JFXButton reviewButton = new JFXButton("Review");
        JFXButton loadButton = new JFXButton("Load");
        JFXButton createButton = new JFXButton("Create");
        JFXButton quitButton = new JFXButton("Quit");
        Separator separator = new Separator();
        Separator separator2 = new Separator();

        List<JFXButton> buttonList = Arrays.asList(parseButton, editButton, syncButton, reviewButton, loadButton, createButton, quitButton);
        String[] buttonIds = new String[]{"button-parse", "button-edit", "button-sync", "button-review", "button-load", "button-create", "button-quit"};

        spacer.prefHeightProperty().bind(scene.heightProperty().multiply(0.125));
        for (int i = 0; i < buttonIds.length; i++) {
            buttonList.get(i).getStyleClass().add("nav-button");
            buttonList.get(i).setGraphic(new FontIcon());
            buttonList.get(i).setId(buttonIds[i]);
        }
        navBox.getChildren().addAll(buttonList);
        navBox.getChildren().add(4, separator);
        navBox.getChildren().add(7, separator2);
        navBox.getChildren().add(0, spacer);
        navBox.setId("nav-box");
        separator.getStyleClass().add("nav-separator");
        separator2.getStyleClass().add("nav-separator");

        // set button actions
        parseButton.setOnAction(e -> setParseScreen());
        editButton.setOnAction(e -> setEditScreen());
        syncButton.setOnAction(e -> setSyncScreen());
        reviewButton.setOnAction(e -> setReviewScreen());

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Database Files", "*.db"));
            File selected = fileChooser.showOpenDialog(stage);
            logic.loadDatabase(selected);
        });
        createButton.setOnAction(e -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Select the location to save the database in.");
            File selected = dirChooser.showDialog(stage);
            logic.createDatabase(selected);
        });

        quitButton.setOnAction(e -> runQuitSequence());

        return navBox;
    }

    private void setParseScreen() {
        BorderPane screenRoot = new BorderPane();
        HBox header = new HBox();
        Text headerText = new Text("Parse");

        header.getChildren().add(headerText);

        // add center pane for parsing, need file tree, filter, progress bar and FAB for go

        JFXTabPane tabs = new JFXTabPane();
        Tab benches = getParseTab("Benches", "bench");
        Tab collections = getParseTab("Collections", "collection");
        Tab items = getParseTab("Items", "item");
        Tab placeables = getParseTab("Placeables", "placeable");
        Tab professions = getParseTab("Professions", "profession");
        Tab recipes = getParseTab("Recipes", "recipe");
        Tab strings = getParseTab("Strings", "string");
        tabs.getTabs().addAll(benches, collections, items, placeables, professions, recipes, strings);

        header.getStyleClass().add("header");
        headerText.getStyleClass().add("header-text");

        screenRoot.setTop(header);
        screenRoot.setCenter(tabs);
        root.setCenter(screenRoot);
    }

    private Tab getParseTab(String name, String type) {
        Tab tab = new Tab(name);

        StackPane root = new StackPane();
        AnchorPane anchor = new AnchorPane();
        GridPane grid = new GridPane();
        JFXTextField directory = new JFXTextField();
        JFXButton dirButton = new JFXButton();
        JFXTextField filter = new JFXTextField();
        JFXButton filterButton = new JFXButton();
        TreeView<String> tree = new TreeView<>();
        JFXButton startButton = new JFXButton();
        JFXProgressBar progressBar = new JFXProgressBar();
        Text progressText = new Text("Status");
        VBox progressBox = new VBox();

        directory.setPromptText("Directory");
        directory.setDisable(true);
        filter.setPromptText("Filter");
        filter.setText(logic.getFilterText(type));

//        C:\Program Files (x86)\Glyph\Games\Trove\Live\extracted_dec_15_subset\prefabs\placeable
        dirButton.setOnAction(e -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Select the location to extract data from.");
            File selected = dirChooser.showDialog(stage);
            if (selected != null) {
                directory.setText(selected.getAbsolutePath());
                tree.setRoot(logic.getParseTree(directory.getText(), type));
                updateParseTree(type, tree, directory);
            }
        });
        filterButton.setOnAction(e -> {
            logic.setFilter(filter.getText(), type);
            filter.setText(logic.getFilterText(type));
            if (directory.getText() != null) {
                updateParseTree(type, tree, directory);
            }
        });

        root.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");
        grid.getStyleClass().add("grid-content");
        directory.getStyleClass().add("text-field-dir");
        dirButton.getStyleClass().addAll("button-inline", "color-subtle");
        filter.getStyleClass().add("text-field-filter");
        dirButton.getStyleClass().addAll("button-inline", "color-subtle");
        filterButton.getStyleClass().addAll("button-inline", "color-subtle");
        tree.getStyleClass().add("dir-view");
        startButton.getStyleClass().addAll("floating-button", "button-start");
        dirButton.setId("button-set-dir");
        filterButton.setId("button-update");
        progressText.getStyleClass().add("text-normal");
        progressText.setId("text-progress");
        progressBar.setProgress(0);

        dirButton.setRipplerFill(Color.valueOf("#FAFAFA"));
        filterButton.setRipplerFill(Color.valueOf("#FAFAFA"));
        startButton.setRipplerFill(Color.valueOf("#FAFAFA"));

        dirButton.setGraphic(new FontIcon());
        filterButton.setGraphic(new FontIcon());
        startButton.setGraphic(new FontIcon());
        tree.setCellFactory(CheckBoxTreeCell.forTreeView());
        tree.prefWidthProperty().bind(root.widthProperty().multiply(0.80));
        tree.prefHeightProperty().bind(root.heightProperty().multiply(0.68));
        progressBar.prefWidthProperty().bind(root.widthProperty().multiply(0.80));

        tab.setContent(root);
        root.getChildren().add(anchor);
        anchor.getChildren().add(grid);
        anchor.getChildren().add(startButton);
        progressBox.getChildren().addAll(progressText, progressBar);
        grid.add(directory, 0, 0);
        grid.add(dirButton, 1, 0);
        grid.add(filter, 2, 0);
        grid.add(filterButton, 3, 0);
        grid.add(progressBox, 0, 1, 4, 1);
        grid.add(tree, 0, 2, 4, 1);

        AnchorPane.setRightAnchor(anchor, 0.0);
        AnchorPane.setLeftAnchor(anchor, 0.0);
        AnchorPane.setTopAnchor(anchor, 0.0);
        AnchorPane.setBottomAnchor(anchor, 0.0);
        AnchorPane.setRightAnchor(startButton, 36.0);
        AnchorPane.setBottomAnchor(startButton, 36.0);

        JFXDepthManager.setDepth(anchor, 1);

        return tab;
    }

    private void updateParseTree(String type, TreeView<String> tree, JFXTextField directory) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                CheckBoxTreeItem<String> treeRoot = logic.getParseTree(directory.getText(), type);
                Platform.runLater(() -> tree.setRoot(treeRoot));
                return null;
            }
        };
        new Thread(task).start();
    }

    private void setEditScreen() {

    }

    private void setSyncScreen() {

    }

    private void setReviewScreen() {

    }

    private void runQuitSequence() {
        // need to add more stuff such as checking data is saved, etc
        Platform.exit();
    }
}
