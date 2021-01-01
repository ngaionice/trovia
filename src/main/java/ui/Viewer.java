package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import objects.Article;

import java.util.Arrays;

public class Viewer extends Application {

    int viewWidth = 1280;
    int viewHeight = 720;

    int navButtonWidth = 75;
    int viewButtonWidth = 150;

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();

        Scene scene = new Scene(root, viewWidth, viewHeight);
        scene.getStylesheets().add(getClass().getResource("/fontstyle.css").toExternalForm());

        sceneSetup(root);


        primaryStage.setTitle("Trovia - Data Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    void sceneSetup(BorderPane root) {
        root.setLeft(navSetUp(root));
//        root.setRight(viewOptionsSetUp());
        root.setCenter(dataDisplaySetUp());
    }

    VBox navSetUp(BorderPane root) {

        // the overarching VBox
        VBox nav = new VBox(70);
        nav.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));
        nav.setPadding(new Insets(70, 0,0,0));

        // sub-VBox 1
        VBox mainNav = new VBox();
        mainNav.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));
//        mainNav.setPadding(new Insets(90, 0,0,0));

        JFXButton createBtn = new JFXButton("Create");
        JFXButton modifyBtn = new JFXButton("Modify");
        JFXButton viewBtn = new JFXButton("View");

        Button[] options = new Button[] {createBtn, modifyBtn, viewBtn};

        vBoxSetup(mainNav, options);

        // sub-VBox 2
        VBox typeNav = new VBox();
//        viewNav.setPadding(new Insets(90, 0,0,0));

        createBtn.setOnAction(event -> callCreate(root, nav, mainNav));
        modifyBtn.setOnAction(event -> callModify(root, nav, mainNav));
        viewBtn.setOnAction(event -> callView(root, nav, mainNav));

        // spacer
        Region spacer = new Region();
        spacer.setPrefHeight(40);


        // add the VBoxes to the main VBox
        nav.getChildren().addAll(Arrays.asList(mainNav, typeNav));

        return nav;
    }

//    VBox viewOptionsSetUp() {
//        VBox views = new VBox();
//        views.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));
//
//        JFXButton searchBtn = new JFXButton("Search");
//        JFXButton itemBtn = new JFXButton("View items");
//        JFXButton colBtn = new JFXButton("View collections");
//        JFXButton recipeBtn = new JFXButton("View recipes");
//        JFXButton langBtn = new JFXButton("View language files");
//
//        Button[] options = new Button[] {searchBtn, itemBtn, colBtn, recipeBtn, langBtn};
//
//        return vBoxSetup(views, options);
//    }

    private VBox vBoxSetup(VBox vBox, Button[] options) {
        for (Button item: options) {
            item.setFont(Font.font("Roboto Regular"));
            item.setTextFill(Paint.valueOf("white"));
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

    void callCreate(BorderPane root, VBox nav, VBox mainNav) {

        // update center table
        root.setCenter(createDisplaySetUp());

        // remove previous nav
        nav.getChildren().clear();

        // make new nav
        VBox typeNav = new VBox();
        typeNav.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));
        JFXButton pColBtn = new JFXButton("Parse Collections");
        JFXButton pBenchBtn = new JFXButton("Parse Benches");
        JFXButton pItemBtn = new JFXButton("Parse Items");
        JFXButton pRecBtn = new JFXButton("Parse Recipes");
        JFXButton pLangBtn = new JFXButton("Parse Language Files");
        JFXButton pProfBtn = new JFXButton("Parse Professions");
        JFXButton cGearBtn = new JFXButton("Create Gears");

        Button[] options = new Button[] {pBenchBtn, pColBtn, pItemBtn, pProfBtn, pRecBtn, pLangBtn, cGearBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(vBoxSetup(typeNav, options));
    }

    void callModify(BorderPane root, VBox nav, VBox mainNav) {

        // update center table
        root.setCenter(modifyDisplaySetUp());

        // remove previous nav
        nav.getChildren().clear();

        // make new nav
        VBox typeNav = new VBox();
        typeNav.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));

        JFXButton benchBtn = new JFXButton("Modify Benches");
        JFXButton colBtn = new JFXButton("Modify Collections");
        JFXButton itemBtn = new JFXButton("Modify Items");

        Button[] options = new Button[] {benchBtn, colBtn, itemBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(vBoxSetup(typeNav, options));
    }

    void callView(BorderPane root, VBox nav, VBox mainNav) {

        // update center table
        root.setCenter(dataDisplaySetUp());

        // remove previous nav
        nav.getChildren().clear();

        // make new nav
        VBox typeNav = new VBox();
        typeNav.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));

        JFXButton searchBtn = new JFXButton("Search");
        JFXButton itemBtn = new JFXButton("View items");
        JFXButton colBtn = new JFXButton("View collections");
        JFXButton recipeBtn = new JFXButton("View recipes");
        JFXButton langBtn = new JFXButton("View language files");

        Button[] options = new Button[] {searchBtn, itemBtn, colBtn, recipeBtn, langBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(vBoxSetup(typeNav, options));
    }

    TableView<String> createDisplaySetUp() {
        TableView<String> table = new TableView<>();
        table.setBackground(new Background(new BackgroundFill(Color.rgb(33, 33, 33), CornerRadii.EMPTY, Insets.EMPTY)));

        return table;

    }

    TableView<String> modifyDisplaySetUp() {
        TableView<String> table = new TableView<>();
        table.setBackground(new Background(new BackgroundFill(Color.rgb(33, 33, 33), CornerRadii.EMPTY, Insets.EMPTY)));

        return table;

    }

    TableView<String> dataDisplaySetUp() {
        TableView<String> table = new TableView<>();
        table.setBackground(new Background(new BackgroundFill(Color.rgb(33, 33, 33), CornerRadii.EMPTY, Insets.EMPTY)));

        return table;

    }


}
