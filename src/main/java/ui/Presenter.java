package ui;

import com.jfoenix.controls.*;
import controllers.UIController;
import javafx.beans.binding.DoubleBinding;
import ui.searchables.Searchable;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import parser.Parser;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Presenter {

    UIController uiCon = new UIController();
    DesignProperties p = new DesignProperties();
    JFXTreeView<String> dirView;

    String dirPath = "C:\\Program Files (x86)\\Glyph\\Games\\Trove\\Live\\extracted_dec_15_subset";
    String benchFilter = "_interactive";
    String colFilter = "";
    String itemFilter = "";
    String langFilter = "prefabs_";
    String recFilter = "";

    String benchSubPath = "\\prefabs\\placeable\\crafting";
    String colSubPath = "\\prefabs\\collections";
    String itemSubPath = "\\prefabs\\item";
    String langSubPath = "\\languages\\en";
    String profSubPath = "\\prefabs\\professions";
    String recSubPath = "\\prefabs\\recipes";

    // NAVIGATION BAR

    /**
     * Creates the navigation bar for the Create section of the UI.
     *
     * @param root the BorderPane containing the sidebar
     * @param nav  the VBox with the overarching navigation bar, containing the main menu and the sub-menu
     * @param mainNav the VBox containing the main menu
     */
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
        pColBtn.setOnAction(event -> root.setCenter(setPaneCreateDirectory(root, colSubPath, colFilter, Parser.ObjectType.COLLECTION)));
        pBenchBtn.setOnAction(event -> root.setCenter(setPaneCreateDirectory(root, benchSubPath, benchFilter, Parser.ObjectType.BENCH)));
        pItemBtn.setOnAction(event -> root.setCenter(setPaneCreateDirectory(root, itemSubPath, itemFilter, Parser.ObjectType.ITEM)));
        pRecBtn.setOnAction(event -> root.setCenter(setPaneCreateDirectory(root, recSubPath, recFilter, Parser.ObjectType.RECIPE)));
        pLangBtn.setOnAction(event -> root.setCenter(setPaneCreateDirectory(root, langSubPath, langFilter, Parser.ObjectType.LANG_FILE)));
        pProfBtn.setOnAction(event -> root.setCenter(setPaneCreateDirectory(root, profSubPath, "", Parser.ObjectType.PROFESSION)));
        settingsBtn.setOnAction(event -> root.setCenter(setPaneCreateSettings()));

        // set-up the pane
        Button[] options = new Button[]{pBenchBtn, pColBtn, pItemBtn, pProfBtn, pRecBtn, pLangBtn, cGearBtn, settingsBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(setPropVBox(typeNav, options, "#c7c7c7"));

    }

    void callView(BorderPane root, VBox nav, VBox mainNav) {

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
        JFXButton recipeBtn = new JFXButton("View benches");
        JFXButton langBtn = new JFXButton("View language files");

        // button actions
        List<Parser.ObjectType> allArticles = Arrays.asList(Parser.ObjectType.BENCH, Parser.ObjectType.COLLECTION, Parser.ObjectType.ITEM);

        searchBtn.setOnAction(event -> root.setCenter(notImplemented()));
        viewAllBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(root, allArticles, "All Articles", false)));
        itemBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(root, Collections.singletonList(Parser.ObjectType.ITEM), "Items", true)));
        colBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(root, Collections.singletonList(Parser.ObjectType.COLLECTION), "Collections", true)));
        recipeBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(root, Collections.singletonList(Parser.ObjectType.BENCH), "Benches", false)));
        langBtn.setOnAction(event -> root.setCenter(notImplemented()));

        Button[] options = new Button[]{searchBtn, viewAllBtn, itemBtn, colBtn, recipeBtn, langBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(setPropVBox(typeNav, options, "#c7c7c7"));

        // update center table
        root.setCenter(setPaneViewFiles(root, allArticles, "All Articles", false));
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
        nav.getChildren().add(setPropVBox(typeNav, options, "#c7c7c7"));
    }

    // CREATE-RELATED

    GridPane setPaneCreateSettings() {

        // clears previous saved paths
        uiCon.clearParseList();

        GridPane grid = new GridPane();
        grid.setBackground(p.backgroundMainPane);
        grid.setPadding(new Insets(80, 50, 70, 50));
        grid.setHgap(20);

        int textFieldWidth = 500;

        Text settingsText = getTextHeader("Parse Settings", p.colorTextHeader);

        Text dirText = getTextNormal("Absolute path of main directory:", p.colorTextNormal);
        JFXTextField dirField = getTextField("Current directory: " + dirPath, textFieldWidth);

        Text benchText = getTextNormal("Filter for bench parsing: (filters out files without the keyword)", p.colorTextNormal);
        JFXTextField benchField = getTextField("Current filter: " +  benchFilter, textFieldWidth);

        Text colText = getTextNormal("Filter for collection parsing: ", p.colorTextNormal);
        JFXTextField colField = getTextField("Current filter: " + colFilter, textFieldWidth);

        Text itemText = getTextNormal("Filter for item parsing: ", p.colorTextNormal);
        JFXTextField itemField = getTextField("Current filter: " + itemFilter, textFieldWidth);

        Text recText = getTextNormal("Filter for recipe parsing: ", p.colorTextNormal);
        JFXTextField recField = getTextField("Current filter: " + recFilter, textFieldWidth);

        Text langText = getTextNormal("Filter for language file parsing: ", p.colorTextNormal);
        JFXTextField langField = getTextField("Current filter: " + langFilter, textFieldWidth);

        // TODO: add custom sub-directory path editing

        HBox saveBox = new HBox();
        saveBox.setAlignment(Pos.CENTER_RIGHT);

        JFXButton save = getButton("Save", 60, 35, p.backgroundMainButton, p.colorTextMainButton);
        saveBox.getChildren().add(save);

        save.setOnAction(event -> {
            boolean clearDir = saveCreateSettings(dirField.getText(), benchField.getText(), colField.getText(), itemField.getText(), recField.getText(), langField.getText());

            if (clearDir) {
                dirField.clear();
                dirField.setPromptText("Current directory: " + dirPath);
            }
            benchField.clear();
            colField.clear();
            itemField.clear();
            recField.clear();
            langField.clear();

            benchField.setPromptText("Current filter: " + benchFilter);
            colField.setPromptText("Current filter: " + colFilter);
            itemField.setPromptText("Current filter: " + itemFilter);
            recField.setPromptText("Current filter: " + recFilter);
            langField.setPromptText("Current filter: " + langFilter);
        });

        List<Node> gridItems = Arrays.asList(settingsText,
                dirText, dirField,
                benchText, benchField,
                colText, colField,
                itemText, itemField,
                recText, recField,
                langText, langField,
                save);

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

    GridPane setPaneCreateDirectory(BorderPane root, String subPath, String filter, Parser.ObjectType type) {

        StackPane currPane = new StackPane();
        currPane.setBackground(p.backgroundMainPane);

        // clears previous saved paths
        uiCon.clearParseList();

        // update dirView to show the directory
        boolean isCollection = type == Parser.ObjectType.COLLECTION;
        dirView = new JFXTreeView<>(uiCon.getFileTree(dirPath + subPath, filter, isCollection));
        dirView.setCellFactory(CheckBoxTreeCell.forTreeView());
        dirView.getStyleClass().add("dir-view");
        dirView.setStyle("-fx-box-border: #1B1B1B;");
        dirView.setEditable(true);
        dirView.prefWidthProperty().bind(root.widthProperty().multiply(0.7));
        dirView.prefHeightProperty().bind(root.heightProperty().multiply(0.6));

        // put dirView into the StackPane
        currPane.getChildren().add(dirView);

        // create header
        String plural = type == Parser.ObjectType.BENCH ? "es" : "s";
        Text headerText = getTextHeader("Parse " + type + plural, p.colorTextHeader);

        // create progress bar and status text
        JFXProgressBar progressBar = new JFXProgressBar();
        progressBar.getStyleClass().add("jfx-progress-bar");
        progressBar.prefWidthProperty().bind(root.widthProperty().multiply(0.6));
        progressBar.setProgress(0);

        final Text progressText = new Text("Status");
        progressText.setFont(Font.font("Roboto Medium", 11));
        progressText.setFill(Paint.valueOf(p.colorTextNormal));

        VBox progressBox = new VBox();
        progressBox.setAlignment(Pos.BOTTOM_LEFT);
        progressBox.getChildren().add(progressText);
        progressBox.getChildren().add(progressBar);
        progressBox.prefHeightProperty().bind(root.heightProperty().multiply(0.005));

        // create button to start parsing - needs to show dialog box when there are failed files
        JFXButton startParse = getButton("Parse", 60, 35, p.backgroundMainButton, p.colorTextMainButton);

        startParse.setOnAction(event -> {

            // add button
            Task<Void> task = uiCon.getParseTask(type);
            progressBar.progressProperty().bind(task.progressProperty());
            progressText.textProperty().bind(task.messageProperty());

            // adds background thread that runs the task, and shows the dialog box after the task is done
            Thread thread = new Thread(() -> {
                task.run();

                Platform.runLater(() -> {
                    if (!uiCon.getFailedPaths().isEmpty()) {
                        JFXDialogLayout dialogLayout = new JFXDialogLayout();
                        JFXDialog dialog = new JFXDialog(currPane, dialogLayout, JFXDialog.DialogTransition.CENTER);

                        JFXButton button = getButton("Close", 60, 35, p.backgroundDialogButton, p.colorTextDialogButton);
                        button.setOnAction(event1 -> dialog.close());

                        dialogLayout.setHeading(new Text("Incomplete parsing"));
                        dialogLayout.setBody(uiCon.getFailedContent(dirPath));
                        dialogLayout.setActions(button);

                        dialog.show();
                    }
                });
            });
            thread.start();


        });

        VBox buttonBox = new VBox();
        buttonBox.getChildren().add(startParse);
        buttonBox.setPrefHeight(35);

        // set up the grid
        GridPane grid = new GridPane();
        setPropGridPane(grid, new Insets(80, 50, 20, 50), 0);
        setNodeGridPane(grid, Arrays.asList(headerText, currPane, buttonBox, progressBox));
//        grid.setGridLinesVisible(true); // debug

        GridPane.setMargin(currPane, new Insets(30, 30, 30, 0));
        GridPane.setMargin(progressBox, new Insets(35, 30, 10, 0));

        return grid;
    }

    // VIEW-RELATED

    GridPane setPaneViewFiles(BorderPane root, List<Parser.ObjectType> types, String headerText, boolean modifiable) {

        // set up grid
        GridPane grid = new GridPane();
        setPropGridPane(grid, new Insets(80, 50, 70, 50), 20);

        // set up StackPane to hold dialog box and TableView
        StackPane tablePane = new StackPane();

        // set dialog
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(tablePane, dialogLayout, JFXDialog.DialogTransition.CENTER);

        // set table
        TableView<Searchable> table = new TableView<>();
        AtomicReference<ObservableList<Searchable>> articles = new AtomicReference<>(uiCon.getSearchableList(types, ""));

        table.setStyle("-fx-box-border: #1B1B1B;");
        table.prefWidthProperty().bind(root.widthProperty().multiply(0.7));
        table.prefHeightProperty().bind(root.heightProperty().multiply(0.6));
        table.getStyleClass().add("table-view");

        TableColumn<Searchable, String> nameCol = new TableColumn<>("Name");
        TableColumn<Searchable, String> rPathCol = new TableColumn<>("Relative path");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        rPathCol.setCellValueFactory(new PropertyValueFactory<>("rPath"));

        table.setRowFactory(view -> {
            TableRow<Searchable> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    TablePosition pos = table.getSelectionModel().getSelectedCells().get(0);
                    int selectedRow = pos.getRow();

                    Searchable article = table.getItems().get(selectedRow);

                    VBox content;
                    if (article.getRPath().contains("item")) {
                        content = uiCon.getItemContent(article.getRPath());
                    } else if (article.getRPath().contains("collection")) {
                        content = uiCon.getCollectionContent(article.getRPath());
                    } else {
                        content = uiCon.getBenchContent(article.getRPath());
                    }


                    JFXButton button = getButton("Close", 60, 35, p.backgroundDialogButton, p.colorTextDialogButton);
                    button.setOnAction(event1 -> dialog.close());

                    dialogLayout.setHeading(new Text(article.getName()));
                    dialogLayout.setBody(content);
                    dialogLayout.setActions(button);
                    dialog.show();
                }
            });
            return row;
        });

        table.setItems(articles.get());

        // if modifiable is true, add a checkbox column and track which items are selected for modification
        if (modifiable) {
            TableColumn<Searchable, Boolean> checkCol = new TableColumn<>("");
            checkCol.setCellFactory(c -> new CheckBoxTableCell<>());
            checkCol.setCellValueFactory(c -> {
                Searchable cellValue = c.getValue();
                BooleanProperty property = cellValue.isSelected;

                property.addListener((observable, oldValue, newValue) -> {
                    cellValue.setIsSelected(newValue);
                    if (cellValue.getIsSelected()) {
                        uiCon.addSelectedArticle(cellValue.getRPath());
                        for (String item: uiCon.getAllSelectedArticles()) {
                            System.out.println(item);
                        }
                    } else {
                        uiCon.removeSelectedArticle(cellValue.getRPath());
                    }
                 });

                return property;
            });

            nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
            rPathCol.prefWidthProperty().bind(table.widthProperty().multiply(0.5));
            checkCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));

            table.getColumns().setAll(nameCol, rPathCol, checkCol);
            table.setEditable(true);
        } else {
            nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
            rPathCol.prefWidthProperty().bind(table.widthProperty().multiply(0.599));

            table.getColumns().setAll(nameCol, rPathCol);
        }


        tablePane.getChildren().add(table);
        dialog.setDialogContainer(tablePane);

        // set header + search bar
        HBox headerBox = new HBox();
        headerBox.setBackground(p.backgroundMainPane);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text header = new Text(headerText);
        header.setFont(p.fontHeader);
        header.setFill(Paint.valueOf("#fafafa"));

        JFXTextField searchField = getTextField("Search by name", root.widthProperty().multiply(0.15));

        JFXButton searchButton = getButton("Go", 35, 30, p.backgroundMainPane, p.colorTextMainButton);

        searchButton.setOnAction(event -> {

            articles.set(uiCon.getSearchableList(types, searchField.getText()));
            table.setItems(articles.get());
            table.refresh();
        });

        headerBox.getChildren().addAll(Arrays.asList(header, spacer, searchField, searchButton));

        // add items to grid
        setNodeGridPane(grid, Arrays.asList(headerBox, tablePane));

        GridPane.setMargin(headerBox, new Insets(0, 0, 10, 0));

        return grid;
    }

    // HELPER/MISC

    //

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

    JFXButton getButton(String text, double width, double height, Background background, String textColor) {
        JFXButton button = new JFXButton(text);
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.setAlignment(Pos.CENTER);
        button.setBackground(background);
        button.setTextFill(Paint.valueOf(textColor));
        return button;
    }

    Text getTextHeader(String string, String color) {
        Text text = new Text(string);
        text.setFont(p.fontHeader);
        text.setFill(Paint.valueOf(color));
        return text;
    }

    Text getTextNormal(String string, String color) {
        Text text = new Text(string);
        text.setFont(p.fontNormal);
        text.setFill(Paint.valueOf(color));
        return text;
    }

    void setPropGridPane(GridPane grid, Insets insets, double hGap) {
        grid.setBackground(p.backgroundMainPane);
        grid.setPadding(insets);
        grid.setHgap(hGap);
    }

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

    void setNodeGridPane(GridPane grid, List<Node> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            grid.add(nodes.get(i), 0, i);
        }
    }

    boolean saveCreateSettings(String dirInput, String benchInput, String colInput, String itemInput, String recInput, String langInput) {

        boolean alerted = false;
        // set directory path
        if (!dirInput.isEmpty() && dirInput.contains("\\") && new File(dirInput).isDirectory()) {
            dirPath = dirInput;
        } else if (!dirInput.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "The input directory was invalid, so the current directory path was not updated.");
            alert.showAndWait();
            alerted = true;
        }

        // set benchFilter
        if (!benchInput.isEmpty()) {
            benchFilter = benchInput;
        }

        // set colFilter
        if (!colInput.isEmpty()) {
            colFilter = colInput;
        }

        // set itemFilter
        if (!itemInput.isEmpty()) {
            itemFilter = itemInput;
        }

        // set recFilter
        if (!recInput.isEmpty()) {
            recFilter = recInput;
        }

        // set langFilter
        if (!langInput.isEmpty()) {
            langFilter = langInput;
        }
        return alerted;
    }

    StackPane notImplemented() {

        Text display = new Text("This feature is not yet implemented.");
        display.setFill(Paint.valueOf("#c7c7c7"));
        display.setFont(p.fontNormal);
        StackPane pane = new StackPane();
        pane.setBackground(p.backgroundMainPane);
        pane.getChildren().add(display);

        return pane;
    }


}
