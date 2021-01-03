package ui;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controllers.UIController;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Presenter {

    UIController uiCon = new UIController();
    JFXTreeTableView<UIController.Searchable> articleView;
    JFXTreeView<String> dirView;

    Font sectionFont = Font.font("Roboto Medium", 15);
    Font normalFont = Font.font("Roboto Regular", 12);

    String dirPath = "C:\\Program Files (x86)\\Glyph\\Games\\Trove\\Live\\extracted_dec_15_subset";
    String benchFilter = "_interactive";
    String langFilter = "prefabs_";

    Background mainBackground = new Background(new BackgroundFill(Color.rgb(33, 33, 33), CornerRadii.EMPTY, Insets.EMPTY));
    Background buttonBackground = new Background(new BackgroundFill(Color.rgb(238, 238, 238), new CornerRadii(3), Insets.EMPTY));

    void callCreate(BorderPane root, VBox nav, VBox mainNav) {

        // update center table
        root.setCenter(setPaneCreateSettings());

        // remove previous nav
        nav.getChildren().clear();

        // make new nav
        VBox typeNav = new VBox();
        typeNav.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));

        // buttons
        JFXButton pColBtn = new JFXButton("Parse Collections");
        JFXButton pBenchBtn = new JFXButton("Parse Benches");
        JFXButton pItemBtn = new JFXButton("Parse Items");
        JFXButton pRecBtn = new JFXButton("Parse Recipes");
        JFXButton pLangBtn = new JFXButton("Parse Language Files");
        JFXButton pProfBtn = new JFXButton("Parse Professions");
        JFXButton cGearBtn = new JFXButton("Create Gears");
        JFXButton settingsBtn = new JFXButton("Settings");

        // button actions
        cGearBtn.setOnAction(event -> root.setCenter(notImplemented()));
        pColBtn.setOnAction(event -> root.setCenter(setPaneCreateViewDirectory("\\prefabs\\collections","")));
        pBenchBtn.setOnAction(event -> root.setCenter(setPaneCreateViewDirectory("\\prefabs\\placeable\\crafting",benchFilter)));
        pItemBtn.setOnAction(event -> root.setCenter(notImplemented()));
        pRecBtn.setOnAction(event -> root.setCenter(notImplemented()));
        pLangBtn.setOnAction(event -> root.setCenter(notImplemented()));
        pProfBtn.setOnAction(event -> root.setCenter(notImplemented()));
        settingsBtn.setOnAction(event -> root.setCenter(setPaneCreateSettings()));

        // set-up the pane
        Button[] options = new Button[]{pBenchBtn, pColBtn, pItemBtn, pProfBtn, pRecBtn, pLangBtn, cGearBtn, settingsBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(vBoxSetup(typeNav, options, "#c7c7c7"));
    }

    void callModify(BorderPane root, VBox nav, VBox mainNav) {

        // update center table
        root.setCenter(modifyDisplaySetUp());

        // remove previous nav
        nav.getChildren().clear();

        // make new nav
        VBox typeNav = new VBox();
        typeNav.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));

        // buttons
        JFXButton benchBtn = new JFXButton("Modify Benches");
        JFXButton colBtn = new JFXButton("Modify Collections");
        JFXButton itemBtn = new JFXButton("Modify Items");

        // button actions
        benchBtn.setOnAction(event -> root.setCenter(notImplemented()));
        colBtn.setOnAction(event -> root.setCenter(notImplemented()));
        itemBtn.setOnAction(event -> root.setCenter(notImplemented()));

        Button[] options = new Button[]{benchBtn, colBtn, itemBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(vBoxSetup(typeNav, options, "#c7c7c7"));
    }

    void callView(BorderPane root, VBox nav, VBox mainNav) {

        // update center table
        root.setCenter(viewDisplaySetUp());

        // remove previous nav
        nav.getChildren().clear();

        // make new nav
        VBox typeNav = new VBox();
        typeNav.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));

        // buttons
        JFXButton searchBtn = new JFXButton("Search");
        JFXButton viewAllBtn = new JFXButton("View all");
        JFXButton itemBtn = new JFXButton("View items");
        JFXButton colBtn = new JFXButton("View collections");
        JFXButton recipeBtn = new JFXButton("View recipes");
        JFXButton langBtn = new JFXButton("View language files");

        // button actions
        searchBtn.setOnAction(event -> root.setCenter(notImplemented()));
        viewAllBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(Arrays.asList("bench", "collection", "item"))));
        itemBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(Collections.singletonList("item"))));
        colBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(Collections.singletonList("collection"))));
        recipeBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(Collections.singletonList("bench"))));
        langBtn.setOnAction(event -> root.setCenter(notImplemented()));

        Button[] options = new Button[]{searchBtn, viewAllBtn, itemBtn, colBtn, recipeBtn, langBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(vBoxSetup(typeNav, options, "#c7c7c7"));
    }

    void callSync(BorderPane root, VBox nav, VBox mainNav) {

        // update center table
        root.setCenter(notImplemented());

        // remove previous nav
        nav.getChildren().clear();

        // make new nav
        VBox typeNav = new VBox();
        typeNav.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));

        // buttons
        JFXButton addBtn = new JFXButton("Review new Articles");
        JFXButton removeBtn = new JFXButton("Review deleted Articles");
        JFXButton syncBtn = new JFXButton("Update database");

        // button actions
        addBtn.setOnAction(event -> root.setCenter(notImplemented()));
        removeBtn.setOnAction(event -> root.setCenter(notImplemented()));
        syncBtn.setOnAction(event -> root.setCenter(notImplemented()));

        Button[] options = new Button[]{addBtn, removeBtn, syncBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(vBoxSetup(typeNav, options, "#c7c7c7"));
    }

    GridPane setPaneCreateSettings() {
        GridPane grid = new GridPane();
        grid.setBackground(mainBackground);
        grid.setPadding(new Insets(80, 50, 70, 50));
        grid.setHgap(20);

        Text settingsText = new Text("Parse Settings");
        settingsText.setFont(sectionFont);
        settingsText.setFill(Paint.valueOf("#fafafa"));

        Text directoryText = new Text("Absolute path of main directory:");
        directoryText.setFont(normalFont);
        directoryText.setFill(Paint.valueOf("#c7c7c7"));

        JFXTextField directory = new JFXTextField();
        directory.setPrefWidth(500);
        directory.setFocusColor(Paint.valueOf("#c7c7c7"));
        directory.setPromptText("Current directory: " + dirPath);

        Text benchText = new Text("Filter for bench parsing: (filters out files without the keyword)");
        JFXTextField benchField = new JFXTextField();
        setUpTextField(benchText, benchField, benchFilter);

        Text langText = new Text("Filter for language file parsing: (filters out files without the keyword)");
        JFXTextField langField = new JFXTextField();
        setUpTextField(langText, langField, langFilter);

        HBox saveBox = new HBox();
        saveBox.setAlignment(Pos.CENTER_RIGHT);

        JFXButton save = new JFXButton("Save");
        save.setPrefWidth(60);
        save.setPrefHeight(35);
        save.setAlignment(Pos.CENTER);
        save.setBackground(buttonBackground);
        saveBox.getChildren().add(save);

        save.setOnAction(event -> saveCreateSettings(directory.getText(), directory, benchField.getText(), benchField, langField.getText(), langField));

        List<Node> gridItems = Arrays.asList(settingsText, directoryText, directory, benchText, benchField, langText, langField, save);

        // add normal items
        for (int i = 0; i < gridItems.size() - 1; i++) {
            grid.add(gridItems.get(i), 0, i);
            RowConstraints con = new RowConstraints();
            // Here we set the pref height of the row, but you could also use .setPercentHeight(double) if you don't know much space you will need for each label.
            con.setPrefHeight(30);
            grid.getRowConstraints().add(con);
        }

        // add save button
        grid.add(save, 0, gridItems.size());
        RowConstraints con = new RowConstraints();
        con.setPrefHeight(35);
        grid.getRowConstraints().add(con);


        return grid;

    }

    private void setUpTextField(Text text, JFXTextField textField, String defaultText) {
        text.setFont(normalFont);
        text.setFill(Paint.valueOf("#c7c7c7"));

        textField.setPrefWidth(500);
        textField.setFocusColor(Paint.valueOf("#c7c7c7"));
        textField.setPromptText("Current filter: " + defaultText);
    }

    TableView<String> modifyDisplaySetUp() {
        TableView<String> table = new TableView<>();
        table.setBackground(new Background(new BackgroundFill(Color.rgb(33, 33, 33), CornerRadii.EMPTY, Insets.EMPTY)));

        return table;

    }

    TableView<String> viewDisplaySetUp() {
        TableView<String> table = new TableView<>();
        table.setBackground(new Background(new BackgroundFill(Color.rgb(33, 33, 33), CornerRadii.EMPTY, Insets.EMPTY)));

        return table;

    }

    VBox vBoxSetup(VBox vBox, Button[] options, String buttonColor) {
        for (Button item : options) {
            item.setFont(normalFont);
            item.setTextFill(Paint.valueOf(buttonColor));
            item.setMinWidth(175);
            item.setMinHeight(35);
            item.setPadding(new Insets(0, 0, 0, 25));
            item.setAlignment(Pos.BASELINE_LEFT);
        }

        for (Button option : options) {
            vBox.getChildren().add(option);
        }
        vBox.setAlignment(Pos.CENTER_LEFT);

        return vBox;
    }

    StackPane notImplemented() {

        Text display = new Text("This feature is not yet implemented.");
        display.setFill(Paint.valueOf("#c7c7c7"));
        display.setFont(normalFont);
        StackPane pane = new StackPane();
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(33, 33, 33), CornerRadii.EMPTY, Insets.EMPTY)));
        pane.getChildren().add(display);

        return pane;
    }

    void saveCreateSettings(String dirInput, TextField dir, String benchInput, TextField bench, String langInput, TextField lang) {

        // set directory path
        if (!dirInput.isEmpty() && dirInput.contains("\\") && new File(dirInput).isDirectory()) {
            dirPath = dirInput;
            dir.setPromptText("Current directory: " + dirPath);
            dir.clear();
        } else if (dirInput.isEmpty()) {
            dir.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The input directory was invalid, so the current directory path was not updated.");
            alert.showAndWait();
        }

        // set benchFilter
        if (!benchInput.isEmpty()) {
            benchFilter = benchInput;
            bench.setPromptText("Current filter: " + benchInput);
            bench.clear();
        }

        // set langFilter
        if (!langInput.isEmpty()) {
            langFilter = langInput;
            lang.setPromptText("Current filter: " + langInput);
            lang.clear();
        }
    }

    JFXTreeTableView<UIController.Searchable> setPaneViewFiles(List<String> types) {

        JFXTreeTableColumn<UIController.Searchable, String> artName = new JFXTreeTableColumn<>("Article Name");
        artName.setPrefWidth(225);
        artName.setCellValueFactory(param -> param.getValue().getValue().name);

        JFXTreeTableColumn<UIController.Searchable, String> rPath = new JFXTreeTableColumn<>("Relative Path");
        rPath.setPrefWidth(275);
        rPath.setCellValueFactory(param -> param.getValue().getValue().rPath);

        ObservableList<UIController.Searchable> articles = uiCon.getSearchableList(types);

        final TreeItem<UIController.Searchable> root = new RecursiveTreeItem<>(articles, RecursiveTreeObject::getChildren);

        articleView.getColumns().setAll(artName, rPath);
        articleView.setRoot(root);
        articleView.setShowRoot(false);

        return articleView;
    }

    JFXTreeView<String> setPaneCreateViewDirectory(String subPath, String filter) {
        dirView = new JFXTreeView<>(uiCon.getFileTree(dirPath + subPath, filter));
        dirView.setCellFactory(CheckBoxTreeCell.forTreeView());
        dirView.getStyleClass().add("dir-view");
        dirView.setStyle("-fx-box-border: #212121;");
        dirView.setEditable(true);
        return dirView;
    }


}
