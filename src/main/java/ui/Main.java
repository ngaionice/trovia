package ui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.Objects;
import java.util.Optional;

public class Main extends Application {

    Presenter presenter = null;
    Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(confirmCloseEventHandler);
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1280, 720);
        presenter = new Presenter(primaryStage, scene, root);


        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/data-views.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/icon-styles.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/jfx-styles.css")).toExternalForm());
        root.setLeft(presenter.getNavBar());

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/cube.png"))));
        primaryStage.setTitle("Trovia-E");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private final EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
        if (presenter == null || presenter.safeToClose()) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You haven't exported your changes. Are you sure you want to exit?");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/dialogs.css")).toExternalForm());
        dialogPane.getStyleClass().add("dialog");
        Button exitButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        exitButton.setText("Exit");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.initStyle(StageStyle.UTILITY);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(primaryStage);
        Optional<ButtonType> closeResponse = alert.showAndWait();
        if (closeResponse.isPresent() && !ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
        }
    };
}
