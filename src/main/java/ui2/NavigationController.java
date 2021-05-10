package ui2;

import com.jfoenix.controls.JFXButton;
import datamodel.DataModel;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavigationController {

    DataModel model;
    Stage stage;
    Scene scene;
    BorderPane root;

    public NavigationController(Stage stage, Scene sc, BorderPane root) {
        this.stage = stage;
        this.scene = sc;
        this.root = root;
    }

    public VBox getNavBar() {
        // note that we will need the other components later on to set screens

        VBox navBox = new VBox();
        Region spacer = new Region();
        JFXButton parseButton = new JFXButton("Parse");
        JFXButton editButton = new JFXButton("Edit");
        JFXButton syncButton = new JFXButton("Sync");
        JFXButton loadButton = new JFXButton("Load");
        JFXButton quitButton = new JFXButton("Quit");
        Separator separator = new Separator();

        List<JFXButton> buttonList = Arrays.asList(parseButton, editButton, syncButton, loadButton, quitButton);
        String[] buttonIds = new String[]{"button-parse", "button-edit", "button-sync", "button-load", "button-quit"};

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
        separator.setId("nav-separator");

        // set button actions
        parseButton.setOnAction(e -> setParseScreen());
        editButton.setOnAction(e -> setEditScreen());
        syncButton.setOnAction(e -> setSyncScreen());
        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Database Files", "*.db"));
            File selected = fileChooser.showOpenDialog(stage);
            if (selected != null) {
                String path = selected.getAbsolutePath();
                String lang = "en";     // need to make mini-dialog to select language, but setting to english for now
                try {
                    model = new DataModel(path, lang);
                    // popup saying data loaded
                } catch (SQLException ex) {
                    // do something
                }
            }
        });
        quitButton.setOnAction(e -> runQuitSequence());

        return navBox;
    }

    private void setParseScreen() {
        BorderPane screenRoot = new BorderPane();
        HBox header = new HBox();
        Text headerText = new Text("Parse");

        header.getChildren().add(headerText);

        header.getStyleClass().add("header");
        headerText.getStyleClass().add("header-text");

        // add center pane for parsing, need file tree, filter, progress bar and FAB for go

        screenRoot.setTop(header);
        root.setCenter(screenRoot);
    }

    private void setEditScreen() {

    }

    private void setSyncScreen() {

    }

    private void runQuitSequence() {

    }
}
