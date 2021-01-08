package ui;

import com.jfoenix.controls.*;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ui.searchables.Searchable;

import java.util.Collection;
import java.util.List;

public class PresenterElementHelper {

    // provides smaller UI elements for Presenter

    DesignProperties p;

    public PresenterElementHelper(DesignProperties dp ) {
        this.p = dp;
    }

    // TEXT SETTERS
    Text getTextH1(String string, String color) {
        Text text = new Text(string);
        text.setFont(p.fontH1);
        text.setFill(Paint.valueOf(color));
        return text;
    }

    Text getTextH2(String string, String color) {
        Text text = new Text(string);
        text.setFont(p.fontH2);
        text.setFill(Paint.valueOf(color));
        return text;
    }

    Text getTextH3(String string, String color) {
        Text text = new Text(string);
        text.setFont(p.fontH3);
        text.setFill(Paint.valueOf(color));
        return text;
    }

    Text getTextNormal(String string, String color) {
        Text text = new Text(string);
        text.setFont(p.fontNormal);
        text.setFill(Paint.valueOf(color));
        return text;
    }

    // TEXT-FIELD SETTERS
    JFXTextField getTextField(String promptText, double prefWidth) {
        JFXTextField field = new JFXTextField();
        field.setPromptText(promptText);
        field.setPrefWidth(prefWidth);
        field.setFocusColor(Paint.valueOf(p.colorTextFieldFocus));
        return field;
    }

    JFXTextField getTextField(String promptText, DoubleBinding prefWidth) {
        JFXTextField field = new JFXTextField();
        field.setPromptText(promptText);
        field.prefWidthProperty().bind(prefWidth);
        field.setFocusColor(Paint.valueOf(p.colorTextFieldFocus));
        return field;
    }

    // ELEMENT CREATORS
    GridPane getDialogGrid() {
        GridPane grid = new GridPane();

        grid.setPadding(new Insets(0, 0, 0, 10));
        grid.setVgap(10);
        grid.setMinWidth(500);

        return grid;
    }

    AnchorPane getAnchorPane(Pane overlayPane, Button button, boolean isDialog) {
        AnchorPane anchor = new AnchorPane();

        button.getStyleClass().add("animated-option-button");
        JFXNodesList fab = new JFXNodesList();
        fab.addAnimatedNode(button);

        anchor.getChildren().add(overlayPane);
        anchor.getChildren().add(fab);

        AnchorPane.setRightAnchor(overlayPane, 0.0);
        AnchorPane.setLeftAnchor(overlayPane, 0.0);
        AnchorPane.setTopAnchor(overlayPane, 0.0);
        AnchorPane.setBottomAnchor(overlayPane, 0.0);

        if (isDialog) {
            AnchorPane.setRightAnchor(fab, 25.0);
            AnchorPane.setBottomAnchor(fab, 15.0);
        } else {
            AnchorPane.setRightAnchor(fab, 75.0);
            AnchorPane.setBottomAnchor(fab, 55.0);
        }

        return anchor;
    }

    TableView<Searchable> getTableView(Pane root) {
        TableView<Searchable> table = new TableView<>();

        table.setEditable(true);
        table.setStyle("-fx-box-border: #1B1B1B;");
        table.setFixedCellSize(40);
        table.prefWidthProperty().bind(root.widthProperty().multiply(0.7));
        table.prefHeightProperty().bind(root.heightProperty().multiply(0.7));

        return table;
    }

    JFXTreeView<String> getTreeView(Pane root, CheckBoxTreeItem<String> content) {
        JFXTreeView<String> dirView = new JFXTreeView<>(content);
        dirView.setCellFactory(CheckBoxTreeCell.forTreeView());
        dirView.getStyleClass().add("dir-view");
        dirView.setStyle("-fx-box-border: #1B1B1B;");
        dirView.setEditable(true);
        dirView.prefWidthProperty().bind(root.widthProperty().multiply(0.7));
        dirView.prefHeightProperty().bind(root.heightProperty().multiply(0.6));

        return dirView;
    }

    JFXButton getButton(String text, double width, double height, Background background, String textColor) {
        JFXButton button = new JFXButton(text);
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.setAlignment(Pos.CENTER);
        button.setBackground(background);
        button.setTextFill(Paint.valueOf(textColor));
        return button;
    }

    JFXDialog getDialog(StackPane root, Node header, Node content, Node button) {
        JFXDialogLayout layout = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(root, layout, JFXDialog.DialogTransition.CENTER);

        dialog.setStyle("-fx-background-color: #1B1B1BBB");
        layout.setBackground(p.backgroundDialog);

        layout.setHeading(header);
        layout.setBody(content);
        layout.setActions(button);

        return dialog;
    }

    VBox getProgressBox(Pane root, Text progressText, JFXProgressBar progressBar) {

        progressBar.getStyleClass().add("jfx-progress-bar");
        progressBar.prefWidthProperty().bind(root.widthProperty().multiply(0.6));
        progressBar.setProgress(0);

        progressText.setFont(p.fontNormal);
        progressText.setFill(Paint.valueOf(p.colorTextNormal));

        VBox progressBox = new VBox();
        progressBox.setAlignment(Pos.BOTTOM_LEFT);
        progressBox.getChildren().add(progressText);
        progressBox.getChildren().add(progressBar);
        progressBox.setSpacing(5);
        progressBox.prefHeightProperty().bind(root.heightProperty().multiply(0.005));

        return progressBox;
    }

    JFXComboBox<String> getComboBox(Collection<String> content, String promptText, double width) {
        JFXComboBox<String> options = new JFXComboBox<>();
        options.setEditable(true);
        options.setPrefWidth(width);
        options.setFocusColor(Paint.valueOf(p.colorTextFieldFocus));
        options.setPromptText(promptText);

        options.getItems().setAll(content);

        return options;
    }

    JFXAutoCompletePopup<String> getAutoCompletePopup(Collection<String> content, ComboBox<String> choices) {
        JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.setPrefWidth(300);
        autoCompletePopup.setFixedCellSize(32);
        autoCompletePopup.setStyle("-fx-focus-color: #6D6D6D ; -fx-faint-focus-color: -fx-control-inner-background ;");

        autoCompletePopup.setSelectionHandler(event -> choices.setValue(event.getObject()));

        autoCompletePopup.getSuggestions().setAll(content);
        return autoCompletePopup;
    }

    TextField getComboBoxEditor(ComboBox<String> comboBox, JFXAutoCompletePopup<String> autoCompletePopup) {
        TextField field = comboBox.getEditor();
        field.textProperty().addListener(observable -> {
            autoCompletePopup.filter(string -> string.toLowerCase().contains(field.getText().toLowerCase()));
            if (autoCompletePopup.getFilteredSuggestions().isEmpty() || comboBox.showingProperty().get() || !comboBox.focusedProperty().get()) {
                autoCompletePopup.hide();
            } else {
                autoCompletePopup.show(field);
            }
        });

        return field;
    }

    TextArea getTextArea(String content) {
        JFXTextArea textArea = new JFXTextArea(content);
        textArea.setFocusColor(Paint.valueOf(p.colorBackgroundDialog));
        textArea.setUnFocusColor(Paint.valueOf(p.colorBackgroundDialog));
        textArea.setBackground(p.backgroundDialog);

        return textArea;
    }

    JFXSnackbar.SnackbarEvent getSnackbarEvent(String text) {
        HBox textBox = new HBox();
        textBox.getChildren().add(getTextH3(text, p.colorTextDialogButton));
        textBox.setAlignment(Pos.CENTER_LEFT);
        textBox.setMinWidth(300);
        textBox.setMinHeight(35);
        textBox.setPadding(new Insets(0, 20, 0, 20));

        return new JFXSnackbar.SnackbarEvent(textBox, Duration.seconds(3.33), null);
    }

    // ELEMENT MODIFIERS

    VBox setPropVBox(VBox vBox, Button[] options, String buttonTextColor) {
        for (Button item : options) {
            item.setFont(p.fontNormal);
            item.setTextFill(Paint.valueOf(buttonTextColor));
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

    void setPropGridPane(GridPane grid, Insets insets, double hGap) {
        grid.setBackground(p.backgroundMainPane);
        grid.setPadding(insets);
        grid.setHgap(hGap);
    }

    void setNodeGridPane(GridPane grid, List<Node> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            grid.add(nodes.get(i), 0, i);
        }
    }

}
