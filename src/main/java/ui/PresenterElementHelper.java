package ui;

import com.jfoenix.controls.*;
import model.ModelController;
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
import model.objects.CollectionEnums;
import ui.searchables.Searchable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
        textArea.setEditable(false);

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
    
    // VBOX CREATORS FOR VIEW
    VBox getItemContent(String rPath, ModelController con) {
        VBox content = getContentBox();

        // will need to get more items from ModelController later as we add more properties
        content.getChildren().add(getTextH3("Description:", p.colorTextDialogButton));
        if (con.getItemDesc(rPath) != null) {
            content.getChildren().add(getTextNormal(con.getItemDesc(rPath), p.colorTextDialogButton));
        } else {
            content.getChildren().add(getTextNormal("Not available.", p.colorTextDialogButton));
        }

        content.getChildren().add(getTextH3("Description identifer:", p.colorTextDialogButton));
        content.getChildren().add(getTextNormal(con.getItemDescIdentifier(rPath), p.colorTextDialogButton));

        content.getChildren().add(getTextH3("Relative path:", p.colorTextDialogButton));
        content.getChildren().add(getTextNormal(rPath, p.colorTextDialogButton));

        if (con.getLootbox(rPath) != null) {

            content.getChildren().add(getTextH3("Lootbox Contents", p.colorTextDialogButton));
            Map<String, Map<String, String>> loot = con.getLootbox(rPath);

            // common
            if (loot.get("common") != null) {
                Map<String, String> common = loot.get("common");
                content.getChildren().add(createContentText("Common loot", content));
                for (String itemPath: common.keySet()) {
                    content.getChildren().add(createContentText(con.getName(itemPath) + " - " + common.get(itemPath), content));
                }
            }

            // uncommon
            if (loot.get("uncommon") != null) {
                Map<String, String> uncommon = loot.get("uncommon");
                content.getChildren().add(createContentText("Uncommon loot", content));
                for (String itemPath: uncommon.keySet()) {
                    content.getChildren().add(createContentText(con.getName(itemPath) + " - " + uncommon.get(itemPath), content));
                }
            }

            // rare
            if (loot.get("rare") != null) {
                Map<String, String> rare = loot.get("rare");
                content.getChildren().add(createContentText("Rare loot", content));
                for (String itemPath: rare.keySet()) {
                    content.getChildren().add(createContentText(con.getName(itemPath) + " - " + rare.get(itemPath), content));
                }
            }
        }

        if (con.getDecons(rPath) != null) {
            content.getChildren().add(getTextH3("Loots into: ", p.colorTextDialogButton));
            Map<String, Integer> decons = con.getDecons(rPath);
            for (String itemPath: decons.keySet()) {
                content.getChildren().add(createContentText( con.getName(itemPath)+ " - " + decons.get(itemPath), content));
            }
        }

        if (con.getItemRecipes(rPath) != null) {
            content.getChildren().add(getTextH3("Recipes: ", p.colorTextDialogButton));
            for (String item: con.getItemRecipes(rPath)) {
                content.getChildren().add(createContentText(item, content));
            }
        }

        if (!con.getItemNotes(rPath).isEmpty()) {
            content.getChildren().add(new Text("Notes:"));
            for (String identifier: con.getItemNotes(rPath)) {
                content.getChildren().add(new Text(con.getString(identifier)));
            }
        }


        return content;
    }

    VBox getCollectionContent(String rPath, ModelController con, PresenterLogicHelper logic) {
        VBox content = getContentBox();

        content.getChildren().add(getTextH3("Description:", p.colorTextDialogButton));
        content.getChildren().add(createContentText(con.getCollectionDesc(rPath), content));

        content.getChildren().add(getTextH3("Description identifer:", p.colorTextDialogButton));
        content.getChildren().add(createContentText(con.getCollectionDescIdentifier(rPath), content));

        content.getChildren().add(getTextH3("Relative path:", p.colorTextDialogButton));
        content.getChildren().add(createContentText(rPath, content));

        Map<CollectionEnums.Property, Double> colProp = con.getCollectionProperties(rPath);
        Map<CollectionEnums.Buff, Double> buffs = con.getCollectionBuffs(rPath);

        if (!colProp.isEmpty()) {
            content.getChildren().add(getTextH3("Collection abilities: ", p.colorTextDialogButton));
            for (Map.Entry<CollectionEnums.Property, Double> item: colProp.entrySet()) {
                String[] prop = logic.getProperties(item);
                content.getChildren().add(getTextNormal(prop[0] +  prop[1], p.colorTextDialogButton));
            }
        }

        if (buffs != null) {
            content.getChildren().add(getTextH3("Permanent stats buffs: ", p.colorTextDialogButton));
            for (Map.Entry<CollectionEnums.Buff, Double> item: buffs.entrySet()) {
                String[] prop = logic.getBuffs(item);
                content.getChildren().add(getTextNormal(prop[0]  + prop[1], p.colorTextDialogButton));
            }
        }

        Integer[] mastery = con.getCollectionMastery(rPath);
        content.getChildren().add(getTextH3("Trove Mastery: ", p.colorTextDialogButton));
        content.getChildren().add(createContentText(mastery[0].toString(), content));

        content.getChildren().add(getTextH3("Geode Mastery: ", p.colorTextDialogButton));
        content.getChildren().add(createContentText(mastery[1].toString(), content));

        if (con.getCollectionRecipes(rPath) != null) {
            content.getChildren().add(getTextH3("Recipes: ", p.colorTextDialogButton));
            for (String item: con.getCollectionRecipes(rPath)) {
                content.getChildren().add(createContentText(item, content));
            }
        }

        if (con.getCollectionNotes(rPath) != null) {
            content.getChildren().add(getTextH3("Notes: ", p.colorTextDialogButton));
            for (String identifier: con.getCollectionNotes(rPath)) {
                content.getChildren().add(createContentText(con.getString(identifier), content));
            }
        }

        return content;
    }

    VBox getBenchContent(String rPath, ModelController con) {
        VBox content = getContentBox();

        content.getChildren().add(getTextH3("Recipes in bench:", p.colorTextDialogButton));
        content.getChildren().add(new JFXTextArea(String.join(" \n", con.getBenchRecipes(rPath))));

        content.getChildren().add(getTextH3("Relative path:", p.colorTextDialogButton));
        content.getChildren().add(createContentText(rPath, content));

        return content;
    }

    VBox getFailedContent(String dirPath, List<String> failedPaths) {
        VBox content = new VBox();
        content.setSpacing(3);

        for (int i = 0; i < failedPaths.size(); i++) {
            failedPaths.set(i, failedPaths.get(i).substring(dirPath.length() + 1));
        }
        String failedPathsJoined = String.join(" \n", failedPaths);

        content.getChildren().add(new Text("The following paths were not parsed:"));
        TextArea failed = getTextArea(failedPathsJoined);
        content.getChildren().add(failed);

        Text message = new Text("Note that this is normal, if files that have not been designed to be parsed were selected.");
        message.setWrappingWidth(350);
        content.getChildren().add(message);

        return content;
    }

    VBox getContentBox() {
        VBox box = new VBox();
        box.setSpacing(3);
        return box;
    }

    Text createContentText(String str, VBox box) {
        Text text = getTextNormal(str, p.colorTextDialogButton);
        text.wrappingWidthProperty().bind(box.widthProperty().multiply(0.9));
        return text;
    }

}
