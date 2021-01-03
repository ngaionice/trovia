package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;

public class View extends Application {

    int viewWidth = 1280;
    int viewHeight = 720;

    int navButtonWidth = 75;
    int viewButtonWidth = 150;

    Presenter pr = new Presenter();

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();

        JFXDecorator decorator = new JFXDecorator(primaryStage, root);
        decorator.setCustomMaximize(true);
        Scene scene = new Scene(decorator, viewWidth, viewHeight);

        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        sceneInitSetUp(root);

        primaryStage.setTitle("Trovia - Database Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    void sceneInitSetUp(BorderPane root) {
        root.setLeft(navInitSetUp(root));
        Text start = new Text("Select an option on the navigation bar to begin.");
        start.setFill(Paint.valueOf("#424242"));
        start.setFont(Font.font("Roboto Light"));
        StackPane startPane = new StackPane();
        startPane.setBackground(new Background(new BackgroundFill(Color.rgb(33,33,33), CornerRadii.EMPTY, Insets.EMPTY)));
        startPane.getChildren().add(start);
        root.setCenter(startPane);
    }

    VBox navInitSetUp(BorderPane root) {

        // the overarching VBox
        VBox nav = new VBox(70);
        nav.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));
        nav.setPadding(new Insets(70, 0,0,0));

        // sub-VBox 1
        VBox mainNav = new VBox();
        mainNav.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));

        JFXButton createBtn = new JFXButton("Create");
        JFXButton modifyBtn = new JFXButton("Modify");
        JFXButton viewBtn = new JFXButton("View");
        JFXButton syncBtn = new JFXButton("Synchronize");

        Button[] options = new Button[] {createBtn, modifyBtn, viewBtn, syncBtn};

        pr.vBoxSetup(mainNav, options, "white");

        // sub-VBox 2
        VBox typeNav = new VBox();

        createBtn.setOnAction(event -> pr.callCreate(root, nav, mainNav));
        modifyBtn.setOnAction(event -> pr.callModify(root, nav, mainNav));
        viewBtn.setOnAction(event -> pr.callView(root, nav, mainNav));
        syncBtn.setOnAction(event -> pr.callSync(root, nav, mainNav));

        // add the VBoxes to the main VBox
        nav.getChildren().addAll(Arrays.asList(mainNav, typeNav));

        return nav;
    }










}
