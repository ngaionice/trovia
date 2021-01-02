package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controllers.Controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;

public class Presenter {

    Controller con = new Controller();
    String dirPath;

    void callCreate(BorderPane root, VBox nav, VBox mainNav) {

        // update center table
        root.setCenter(setUpCreateSettingsPane());

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
        pColBtn.setOnAction(event -> root.setCenter(notImplemented()));
        pBenchBtn.setOnAction(event -> root.setCenter(notImplemented()));
        pItemBtn.setOnAction(event -> root.setCenter(notImplemented()));
        pRecBtn.setOnAction(event -> root.setCenter(notImplemented()));
        pLangBtn.setOnAction(event -> root.setCenter(notImplemented()));
        pProfBtn.setOnAction(event -> root.setCenter(notImplemented()));
        settingsBtn.setOnAction(event -> root.setCenter(setUpCreateSettingsPane()));

        // set-up the pane
        Button[] options = new Button[] {pBenchBtn, pColBtn, pItemBtn, pProfBtn, pRecBtn, pLangBtn, cGearBtn, settingsBtn};

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

        Button[] options = new Button[] {benchBtn, colBtn, itemBtn};

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
        JFXButton itemBtn = new JFXButton("View items");
        JFXButton colBtn = new JFXButton("View collections");
        JFXButton recipeBtn = new JFXButton("View recipes");
        JFXButton langBtn = new JFXButton("View language files");

        // button actions
        searchBtn.setOnAction(event -> root.setCenter(notImplemented()));
        itemBtn.setOnAction(event -> root.setCenter(notImplemented()));
        colBtn.setOnAction(event -> root.setCenter(notImplemented()));
        recipeBtn.setOnAction(event -> root.setCenter(notImplemented()));
        langBtn.setOnAction(event -> root.setCenter(notImplemented()));

        Button[] options = new Button[] {searchBtn, itemBtn, colBtn, recipeBtn, langBtn};

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

        Button[] options = new Button[] {addBtn, removeBtn, syncBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(vBoxSetup(typeNav, options, "#c7c7c7"));
    }

    GridPane setUpCreateSettingsPane() {
        GridPane grid = new GridPane();
        grid.setBackground(new Background(new BackgroundFill(Color.rgb(33, 33, 33), CornerRadii.EMPTY, Insets.EMPTY)));
        grid.setPadding(new Insets(80, 50, 70, 50));
        grid.setHgap(20);

        Text settingsText = new Text("Parse Settings");
        settingsText.setFont(Font.font("Roboto Medium", 15));
        settingsText.setFill(Paint.valueOf("#fafafa"));

        Text spacer = new Text("");
        spacer.setFont(Font.font(30));

        Text directoryText = new Text("Path of main directory:");
        directoryText.setFont(Font.font("Roboto Regular", 12));
        directoryText.setFill(Paint.valueOf("#c7c7c7"));

        JFXTextField directory = new JFXTextField();
        directory.setPrefWidth(500);
        directory.setFocusColor(Paint.valueOf("#c7c7c7"));
        directory.setPromptText("Current directory: null");

        Text spacer2 = new Text("");
        spacer2.setFont(Font.font(30));

        HBox saveBox = new HBox();
        saveBox.setAlignment(Pos.CENTER_RIGHT);

        JFXButton save = new JFXButton("Save");
        save.setPrefWidth(60);
        save.setPrefHeight(35);
        save.setAlignment(Pos.CENTER);
        save.setBackground(new Background(new BackgroundFill(Color.rgb(238, 238, 238), new CornerRadii(3), Insets.EMPTY)));
        saveBox.getChildren().add(save);

        save.setOnAction(event -> saveCreateSettings(directory.getText(), directory));


        grid.add(settingsText, 0, 0);
        grid.add(spacer, 0, 1);
        grid.add(directoryText, 0, 2);
        grid.add(directory, 0, 3);
        grid.add(spacer2, 0, 4);
        grid.add(save, 0, 5);


        return grid;

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
        for (Button item: options) {
            item.setFont(Font.font("Roboto Regular"));
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
        display.setFont(Font.font("Roboto Regular"));
        StackPane pane = new StackPane();
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(33,33,33), CornerRadii.EMPTY, Insets.EMPTY)));
        pane.getChildren().add(display);

        return pane;
    }

    void saveCreateSettings(String input, TextField dir) {
        if (input != null && input.contains("\\")) {
            dirPath = input;
            dir.setPromptText("Current directory: " + dirPath);
            dir.clear();
//            new Text("The inputted directory path is not valid, and nothing was saved."));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The input directory was invalid, so the current directory path was not updated.");
            alert.showAndWait();
        }
    }
}
