package ui;

import com.jfoenix.controls.*;
import controllers.UIController;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.util.Duration;
import ui.searchables.Searchable;
import javafx.application.Platform;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Presenter {

    DesignProperties p;
    UIController uiCon;
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

    public Presenter(DesignProperties p) {
        this.p = p;
        uiCon = new UIController(p);
    }

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
        JFXButton articles = new JFXButton("All");
        JFXButton items = new JFXButton("Items");
        JFXButton collections = new JFXButton("Collections");
        JFXButton benches = new JFXButton("Benches");
        JFXButton languages = new JFXButton("Language files");

        // button actions
        List<Parser.ObjectType> allArticles = Arrays.asList(Parser.ObjectType.BENCH, Parser.ObjectType.COLLECTION, Parser.ObjectType.ITEM);

        articles.setOnAction(event -> root.setCenter(setPaneViewFiles(root, allArticles, "All Entries", false)));
        items.setOnAction(event -> root.setCenter(setPaneViewFiles(root, Collections.singletonList(Parser.ObjectType.ITEM), "Items", true)));
        collections.setOnAction(event -> root.setCenter(setPaneViewFiles(root, Collections.singletonList(Parser.ObjectType.COLLECTION), "Collections", true)));
        benches.setOnAction(event -> root.setCenter(setPaneViewFiles(root, Collections.singletonList(Parser.ObjectType.BENCH), "Benches", true)));
        languages.setOnAction(event -> root.setCenter(notImplemented()));

        Button[] options = new Button[]{articles, items, collections, benches, languages};

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
        JFXButton addBtn = new JFXButton("Review new entries");
        JFXButton removeBtn = new JFXButton("Review deleted entries");
        JFXButton syncBtn = new JFXButton("Update database");

        // button actions
        addBtn.setOnAction(event -> root.setCenter(notImplemented()));
        removeBtn.setOnAction(event -> root.setCenter(notImplemented()));
        syncBtn.setOnAction(event -> root.setCenter(setSyncPane()));

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
                        dialogLayout.setBackground(p.backgroundDialog);

                        JFXButton button = getButton("Close", 60, 35, p.backgroundDialog, p.colorTextDialog);
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

    StackPane setPaneViewFiles(BorderPane root, List<Parser.ObjectType> types, String headerText, boolean modifiable) {

        // set up StackPane to hold dialog box and TableView
        StackPane tablePane = new StackPane();

        // set up dialog
        JFXDialogLayout dialogInfoLayout = new JFXDialogLayout();
        JFXDialog dialogInfo = new JFXDialog(tablePane, dialogInfoLayout, JFXDialog.DialogTransition.CENTER);
        dialogInfo.setDialogContainer(tablePane);
        dialogInfo.setStyle("-fx-background-color: #1B1B1BBB");

        // set up table and map of selected items
        TableView<Searchable> table = new TableView<>();
        Map<Searchable, BooleanProperty> checkedRows = new HashMap<>();

        table.setEditable(true);
        table.setStyle("-fx-box-border: #1B1B1B;");
        table.setFixedCellSize(40);
        table.prefWidthProperty().bind(root.widthProperty().multiply(0.7));
        table.prefHeightProperty().bind(root.heightProperty().multiply(0.7));

        // set up listener: remove filtered items from map
        table.getItems().addListener((Change<? extends Searchable> c) -> {
            if (c.wasRemoved()) {
                c.getRemoved().forEach(checkedRows::remove);
            }
        });

        // set up columns
        TableColumn<Searchable, String> nameCol = new TableColumn<>("Name");
        TableColumn<Searchable, String> rPathCol = new TableColumn<>("Relative path");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        rPathCol.setCellValueFactory(new PropertyValueFactory<>("rPath"));
        nameCol.getStyleClass().add("text-col");
        rPathCol.getStyleClass().add("text-col");

        // set up row
        table.setRowFactory(view -> {
            TableRow<Searchable> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    int selectedRow = table.getSelectionModel().getSelectedCells().get(0).getRow();

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
                    button.setOnAction(event1 -> dialogInfo.close());

                    dialogInfoLayout.setHeading(new Text(article.getName()));
                    dialogInfoLayout.setBody(content);
                    dialogInfoLayout.setActions(button);
                    dialogInfo.show();
                }
            });
            return row;
        });

        // if modifiable is true, add the checkbox column and the edit button
        JFXButton edit = null;
        if (modifiable) {
            TableColumn<Searchable, Void> checkCol = new TableColumn<>();

            CheckBox checkAll = new CheckBox();
//            checkAll.getStyleClass().add("j-checkbox");
            checkAll.setOnAction(e -> {
                if (checkAll.isSelected()) {
                    table.getItems().forEach(p ->
                            checkedRows.computeIfAbsent(p, Searchable -> new SimpleBooleanProperty()).set(true));
                } else{
                    checkedRows.values().forEach(checked -> checked.set(false));
                }
            });

            checkCol.setGraphic(checkAll);
            checkCol.setEditable(true);
            checkCol.getStyleClass().add("checkbox-col");

            checkCol.setCellFactory(CheckBoxTableCell.forTableColumn(i ->
                    checkedRows.computeIfAbsent(table.getItems().get(i), p -> new SimpleBooleanProperty())));

            table.getColumns().setAll(Arrays.asList(checkCol, nameCol, rPathCol));

            // set up edit button
            edit = getButton("Edit", 50, 30, p.backgroundMainButton, p.colorTextMainButton);
            AtomicReference<List<String>> rPathsToEdit = new AtomicReference<>(new ArrayList<>());
            edit.setOnAction(evt -> {
                rPathsToEdit.set(checkedRows.entrySet().stream().filter(e -> e.getValue().get()).map(Map.Entry::getKey)
                        .map(Searchable::getRPath).collect(Collectors.toList()));
                JFXDialog dialogEdit = getEditPane(tablePane, rPathsToEdit.get(), types.get(0));

                dialogEdit.show();
            });
        } else {
            table.getColumns().setAll(Arrays.asList(nameCol, rPathCol));
        }

        // get and set content
        ObservableList<Searchable> articles = uiCon.getSearchableList(types, "");
        FilteredList<Searchable> filtered = new FilteredList<>(articles, p -> true);    // allows filtering
        SortedList<Searchable> sortable = new SortedList<>(filtered);

        // set up search
        String currType = types.size() != 1 ? "entries" : types.get(0).toString().toLowerCase();
        String plural = currType.equals("entries") ? "" : currType.equals("bench") ? "es" : "s";
        JFXTextField searchField = getTextField("Search " + articles.size() + " " + currType + plural + " by name", root.widthProperty().multiply(0.6));
        searchField.setOnKeyReleased(keyEvent ->
            filtered.setPredicate(p -> p.getName().toLowerCase().contains(searchField.getText().toLowerCase().trim()))
        );

        // set content
        table.setItems(sortable);
        sortable.comparatorProperty().bind(table.comparatorProperty());

        // set header + search bar
        HBox headerBox = new HBox();
        headerBox.spacingProperty().bind(root.widthProperty().multiply(0.015));
        headerBox.setBackground(p.backgroundMainPane);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text header = getTextHeader(headerText, p.colorTextHeader);

        if (modifiable) {
            headerBox.getChildren().addAll(Arrays.asList(header, searchField, edit));
        } else {
            headerBox.getChildren().addAll(Arrays.asList(header, searchField));
        }

        // set up grid
        GridPane grid = new GridPane();
        setPropGridPane(grid, new Insets(80, 50, 70, 50), 20);
        setNodeGridPane(grid, Arrays.asList(headerBox, table));

        GridPane.setMargin(headerBox, new Insets(0, 0, 10, 0));

        tablePane.getChildren().add(grid);

        return tablePane;
    }

    // SYNC-RELATED

    GridPane setSyncPane() {
        GridPane grid = new GridPane();
        setPropGridPane(grid, new Insets(80, 50, 20, 50), 0);

        JFXButton button = getButton("Serialize", 100, 100, p.backgroundMainButton, p.colorTextMainButton);
        button.setOnAction(e -> uiCon.save());

        grid.add(button, 0, 0);

        return grid;
    }

    // HELPER/MISC

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

    Text getTextSubHeader(String string, String color) {
        Text text = new Text(string);
        text.setFont(p.fontSubHeader);
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

    public void initSetUp() {
        uiCon.initSetUp();
    }

    JFXDialog getEditPane(StackPane root, List<String> rPaths, Parser.ObjectType type) {

        // create the dialog
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(root, dialogLayout, JFXDialog.DialogTransition.CENTER);
        dialog.setStyle("-fx-background-color: #1B1B1BBB");

        BorderPane dialogRoot = new BorderPane();
        dialogRoot.prefWidthProperty().bind(root.widthProperty());

        // set buttons

        // all types
        JFXButton selected = new JFXButton("Selected objects");

        // items and collections
        JFXButton notes = new JFXButton("Add notes");
        JFXButton recipe = new JFXButton("Get recipes");

        // collections only
        JFXButton mastery = new JFXButton("Set mastery");

        // items only
        JFXButton lootbox = new JFXButton("Add lootbox content");
        JFXButton decons = new JFXButton("Set deconstruction output");

        // benches only
        JFXButton name = new JFXButton("Set bench name");
        JFXButton match = new JFXButton("Match recipes");

        Button[] options;
        if (type == Parser.ObjectType.ITEM) {
            options = new Button[] {selected, decons, lootbox, notes, recipe};
        } else if (type == Parser.ObjectType.COLLECTION) {
            options = new Button[] {selected, mastery, notes, recipe};
        } else {
            options = new Button[] {selected, name, match};
        }

        // button configs
        selected.setOnAction(e -> dialogRoot.setCenter(getDialogPaneSelectedObjects(rPaths)));

        notes.setOnAction(e -> dialogRoot.setCenter(getDialogPaneAddNotes(rPaths)));
        recipe.setOnAction(e -> dialogRoot.setCenter(getDialogPaneMatchRecipes()));

        mastery.setOnAction(e -> dialogRoot.setCenter(getDialogPaneSetMastery(rPaths)));

        lootbox.setOnAction(e -> dialogRoot.setCenter(getDialogPaneAddLootbox(rPaths, true)));
        decons.setOnAction(e -> dialogRoot.setCenter(getDialogPaneAddLootbox(rPaths, false)));

        name.setOnAction(e -> dialogRoot.setCenter(getDialogPaneSetBench(rPaths)));
        match.setOnAction(e -> dialogRoot.setCenter(getDialogPaneMatchBenchRecipes(rPaths)));


        // control the pane based on buttons pressed
        dialogRoot.setLeft(getEditSideBar(root, options));
        dialogRoot.setCenter(getDialogPaneSelectedObjects(rPaths));

        String plural = rPaths.size() != 1 ? "s" : "";
        dialogLayout.setHeading(getTextHeader("Editing " + rPaths.size() + " object" + plural, p.colorTextDialog));
        dialogLayout.setBody(dialogRoot);

        return dialog;
    }

    VBox getEditSideBar(StackPane root, Button[] options) {
        // the overarching VBox
        VBox nav = new VBox();
        nav.prefHeightProperty().bind(root.heightProperty().multiply(0.6));

        for (Button button: options) {
            button.setPrefHeight(35);
            button.setPrefWidth(175);
            button.setAlignment(Pos.CENTER_LEFT);
        }

        nav.getChildren().addAll(options);
        return nav;
    }

    GridPane getDialogPaneSelectedObjects(List<String> rPaths) {
        GridPane grid = new GridPane();

        grid.setPadding(new Insets(0, 0, 0, 10));
        grid.setVgap(10);
        grid.setMinWidth(500);

        Text header = getTextSubHeader("Currently selected objects:", p.colorTextDialogButton);
        Text texts = new Text(String.join("\n", rPaths));
        grid.add(header, 0, 0);
        grid.add(texts, 0, 1);

        return grid;
    }

    AnchorPane getDialogPaneAddNotes(List<String> rPaths) {
        AnchorPane anchor = new AnchorPane();
        GridPane grid = new GridPane();
        anchor.getChildren().add(grid);

        grid.setPadding(new Insets(0, 0, 0, 10));
        grid.setVgap(10);
        grid.setMinWidth(500);

        JFXTextField noteField = getTextField("e.g. Hexion needs these", 350);
        JFXButton save = new JFXButton("Save");
        save.setOnAction(e -> uiCon.addNotes(rPaths, noteField.getText()));
        save.getStyleClass().add("animated-option-button");

        grid.add(getTextSubHeader("Note to be added:", p.colorTextDialogButton), 0, 0);
        grid.add(noteField, 0, 1);
        grid.add(getTextNormal("Note that this note is added to every selected object.", p.colorTextDialogButton), 0, 2);

        JFXNodesList fab = new JFXNodesList();
        fab.addAnimatedNode(save);

        anchor.getChildren().add(fab);
        AnchorPane.setRightAnchor(fab, 25.0);
        AnchorPane.setBottomAnchor(fab, 15.0);

        return anchor;
    }

    // TODO: add button to start instead of starting directly; or move it elsewhere
    GridPane getDialogPaneMatchRecipes() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0, 0, 0, 10));

        List<String> failed = uiCon.matchNewRecipes(dirPath);
        Text texts;
        if (failed != null) {
            texts = getTextNormal(String.join("\n", failed), p.colorTextDialog);
        } else {
            texts = getTextNormal("All recipes matched.", p.colorTextDialog);
        }

        grid.add(texts, 0, 0);
        grid.setMinWidth(500);

        return grid;
    }

    AnchorPane getDialogPaneSetMastery(List<String> rPaths) {
        AnchorPane anchor = new AnchorPane();
        GridPane grid = new GridPane();
        anchor.getChildren().add(grid);

        grid.setPadding(new Insets(0, 0, 0, 10));
        grid.setVgap(10);
        grid.setMinWidth(500);

        JFXTextField trove = getTextField("Example: 250", 250);
        JFXTextField geode = getTextField("Leave blank if not applicable", 250);

        grid.add(getTextSubHeader("Trove Mastery:", p.colorTextDialogButton), 0, 0);
        grid.add(trove, 0, 1);
        grid.add(getTextSubHeader("Geode Mastery:", p.colorTextDialogButton), 0, 2);
        grid.add(geode, 0, 3);
        grid.add(getTextNormal("Note that input have to be positive integers; leave blank if not applicable.", p.colorTextDialogButton), 0 ,4);

        JFXButton save = new JFXButton("Save");
        JFXSnackbar confirm = new JFXSnackbar(anchor);

        save.setOnAction(e -> {

            // behavior: save when all inputs are valid
            boolean status = true;
            String intRegex = "\\d+";
            if (((!trove.getText().isEmpty() && trove.getText().matches(intRegex)) || trove.getText().isEmpty()) &&
                    ((!geode.getText().isEmpty() && geode.getText().matches(intRegex)) || geode.getText().isEmpty())) {
                for (String collection: rPaths) {
                    if (!trove.getText().isEmpty()) {
                        uiCon.setTroveMastery(collection, Integer.parseInt(trove.getText()));
                    }
                    if (!geode.getText().isEmpty()) {
                        uiCon.setGeodeMastery(collection, Integer.parseInt(geode.getText()));
                    }
                }
                trove.clear();
                geode.clear();
            } else {
                if (!trove.getText().isEmpty() && !trove.getText().matches(intRegex)) {
                    trove.setUnFocusColor(Paint.valueOf(p.colorTextFieldError));
                }
                if (!geode.getText().isEmpty() && !geode.getText().matches(intRegex)) {
                    geode.setUnFocusColor(Paint.valueOf(p.colorTextFieldError));
                }
                status = false;
            }

            String text = status ? "Saved" : "Invalid input(s), try again";

            JFXSnackbar.SnackbarEvent confirmEvent = getSnackbarEvent(text);
            confirm.enqueue(confirmEvent);
        });
        save.getStyleClass().add("animated-option-button");

        JFXNodesList fab = new JFXNodesList();
        fab.addAnimatedNode(save);

        anchor.getChildren().add(fab);
        AnchorPane.setRightAnchor(fab, 25.0);
        AnchorPane.setBottomAnchor(fab, 15.0);

        return anchor;
    }

    AnchorPane getDialogPaneSetBench(List<String> rPaths) {
        AnchorPane anchor = new AnchorPane();
        GridPane grid = getDialogGrid();
        anchor.getChildren().add(grid);

        // get options for combo box
        Map<String, String> benchEntries = uiCon.getALlStringsFromFile("languages/en/prefabs_placeable_crafting");
        List<String> keys = benchEntries.keySet().stream().filter(value -> value.contains("name"))
                .filter(value -> value.contains("interactive"))
                .filter((value -> !value.contains("category")))
                .collect(Collectors.toList());


        // TODO: optimize code here to remove items from map instead of copying?
        Map<String, String> benchNames = new HashMap<>(200);
        for (String key: benchEntries.keySet()) {
            if (keys.contains(key)) {
                benchNames.put(key, benchEntries.get(key));
            }
        }

        JFXComboBox<String> choices = getComboBox(benchNames.values(), "Name");
        JFXAutoCompletePopup<String> autoCompletePopup = getAutoCompletePopup(benchNames.values(), choices);
        TextField field = getComboBoxEditor(choices, autoCompletePopup);

        // set up save button
        JFXButton save = new JFXButton("Save");
        JFXSnackbar confirm = new JFXSnackbar(anchor);
        save.setOnAction(event -> {
            String identifier = benchEntries.entrySet().stream()
                    .filter(e -> e.getValue().equals(field.getText()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            // even though it's a combo box, people can hit save because the combo box is editable

            if (identifier != null) {
                for (String rPath: rPaths) {
                    uiCon.setBenchName(rPath, identifier);
                }
            }

            String text = identifier != null ? "Saved" : "Invalid input, try again";

            JFXSnackbar.SnackbarEvent confirmEvent = getSnackbarEvent(text);
            confirm.enqueue(confirmEvent);

        });

        save.getStyleClass().add("animated-option-button");

        JFXNodesList fab = new JFXNodesList();
        fab.addAnimatedNode(save);

        anchor.getChildren().add(fab);
        AnchorPane.setRightAnchor(fab, 25.0);
        AnchorPane.setBottomAnchor(fab, 15.0);

        grid.add(getTextSubHeader("Add name to bench", p.colorTextDialogButton), 0, 0);
        grid.add(choices, 0, 1);

        return anchor;
    }

    AnchorPane getDialogPaneMatchBenchRecipes(List<String> rPaths) {
        AnchorPane anchor = new AnchorPane();
        GridPane grid = getDialogGrid();
        anchor.getChildren().add(grid);

        grid.add(getTextSubHeader("Match recipes for the selected benches", p.colorTextDialogButton), 0, 0);

        for (int i = 0; i < rPaths.size(); i++) {
            grid.add(getTextNormal(uiCon.getName(rPaths.get(i)) + " - " + uiCon.getBenchRecipeNumber(rPaths.get(i)) + " recipes", p.colorTextDialogButton), 0, i + 1);
        }

        JFXButton match = new JFXButton("Go");
        JFXSnackbar confirm = new JFXSnackbar(anchor);

        match.setOnAction(e -> {
            List<String> unmatched = new ArrayList<>();
            for (String rPath: rPaths) {
                unmatched.addAll(uiCon.matchBenchRecipe(rPath));
            }

            grid.getChildren().clear();

            String outputHeader = unmatched.isEmpty() ? "All recipes matched" : "Unmatched recipes:";
            grid.add(getTextSubHeader(outputHeader, p.colorTextDialogButton), 0, 0);
            grid.add(getTextNormal(String.join("\n", unmatched), p.colorTextDialogButton), 0, 1);

            String text = "Matching complete";

            JFXSnackbar.SnackbarEvent confirmEvent = getSnackbarEvent(text);
            confirm.enqueue(confirmEvent);
        });

        match.getStyleClass().add("animated-option-button");

        JFXNodesList fab = new JFXNodesList();
        fab.addAnimatedNode(match);

        anchor.getChildren().add(fab);
        AnchorPane.setRightAnchor(fab, 25.0);
        AnchorPane.setBottomAnchor(fab, 15.0);

        return anchor;
    }

    AnchorPane getDialogPaneAddLootbox(List<String> rPaths, boolean lootbox) {

        // if loot = true, lootbox, else is decon

        AnchorPane anchor = new AnchorPane();
        GridPane grid = getDialogGrid();
        anchor.getChildren().add(grid);
        grid.setHgap(10);

        AtomicInteger items = new AtomicInteger(1);

        // set up header
        String headerType = lootbox ? "Add lootbox" : "Set deconstruction";

        Text headerText = getTextSubHeader(headerType + " content", p.colorTextDialogButton);
        HBox headerBox = new HBox();

        JFXComboBox<String> rarity = new JFXComboBox<>();
        rarity.setPrefWidth(125);
        rarity.setFocusColor(Paint.valueOf(p.colorTextFieldFocus));
        rarity.getItems().setAll(Arrays.asList("Common", "Uncommon", "Rare"));

        JFXButton add = getButton("Add field", 75, 30, p.backgroundDialogButton, p.colorTextDialogButton);
        if (lootbox) {
            headerBox.getChildren().setAll(Arrays.asList(headerText, rarity, add));
        } else {
            headerBox.getChildren().setAll(Arrays.asList(headerText, add));
        }

        headerBox.setSpacing(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // set up data
        List<String> itemNames = new ArrayList<>();

        ObservableList<Searchable> nameAndRPathList = uiCon.getSearchableList(Collections.singletonList(Parser.ObjectType.ITEM), "");
        for (Searchable item: nameAndRPathList) {
            itemNames.add(item.getName() + " - " + item.getRPath());
        }

        List<JFXComboBox<String>> comboBoxes = new ArrayList<>();
        List<JFXAutoCompletePopup<String>> autoCompletePopups = new ArrayList<>();
        List<TextField> fields = new ArrayList<>();

        List<JFXTextField> values = new ArrayList<>();

        comboBoxes.add(getComboBox(itemNames, "Item"));
        values.add(getTextField("Quantity", 50));
        autoCompletePopups.add(getAutoCompletePopup(itemNames, comboBoxes.get(0)));
        fields.add(getComboBoxEditor(comboBoxes.get(0), autoCompletePopups.get(0)));

        // set up save button
        JFXButton save = new JFXButton("Save");
        JFXSnackbar confirm = new JFXSnackbar(anchor);
        save.setOnAction(event -> {

                // data validation
                boolean status = true;
                for (int i = 0; i < items.get(); i++) {
                    if (!itemNames.contains(comboBoxes.get(i).getValue())) {
                        status = false;
                        break;
                    }

                    String intRegex = lootbox ? "\\d*[-]?\\d+" : "\\d+";
                    if (!values.get(i).getText().matches(intRegex)) {
                        status = false;
                        break;
                    }
                }

                // data insertion, if data is valid
                if (status) {

                    // get the rPaths
                    List<String[]> loot = new ArrayList<>();
                    for (int i = 0; i < items.get(); i++) {
                        String item = comboBoxes.get(i).getValue();
                        String quantity = values.get(i).getText();

                        String itemRPath = item.split(" - ")[1];
                        loot.add(new String[]{itemRPath, quantity});
                    }

                    // add the loot
                    String lootboxRarity = rarity.getValue();

                    // if lootbox, add to lootbox, else add to decons
                    if (lootbox) {
                        for (String rPath: rPaths) {
                            uiCon.addLootboxContent(rPath, lootboxRarity.toLowerCase(), loot);
                        }
                    } else {
                        for (String rPath: rPaths) {
                            uiCon.addDeconContent(rPath, loot);
                        }
                    }

                }

                String text = status ? "Saved" : "Invalid input, try again";

                JFXSnackbar.SnackbarEvent confirmEvent = getSnackbarEvent(text);
                confirm.enqueue(confirmEvent);
            });


        // set up additional fields
        add.setOnAction(e -> {
            if (items.get() < 7) {
                items.getAndIncrement();

                comboBoxes.add(getComboBox(itemNames, "Item"));
                values.add(getTextField("Quantity", 50));
                autoCompletePopups.add(getAutoCompletePopup(itemNames, comboBoxes.get(items.get() - 1)));
                fields.add(getComboBoxEditor(comboBoxes.get(items.get() - 1), autoCompletePopups.get(items.get() - 1)));

                grid.add(comboBoxes.get(items.get() - 1), 0, items.get());
                grid.add(values.get(items.get() - 1), 1, items.get());
            }

        });


        save.getStyleClass().add("animated-option-button");

        JFXNodesList fab = new JFXNodesList();
        fab.addAnimatedNode(save);

        anchor.getChildren().add(fab);
        AnchorPane.setRightAnchor(fab, 25.0);
        AnchorPane.setBottomAnchor(fab, 15.0);

        grid.add(headerBox, 0, 0, 2, 1);
        grid.add(comboBoxes.get(0), 0, 1);
        grid.add(values.get(0), 1, 1);

        return anchor;
    }

    JFXSnackbar.SnackbarEvent getSnackbarEvent(String text) {
        HBox textBox = new HBox();
        textBox.getChildren().add(getTextSubHeader(text, p.colorTextDialogButton));
        textBox.setAlignment(Pos.CENTER_LEFT);
        textBox.setMinWidth(300);
        textBox.setMinHeight(35);
        textBox.setPadding(new Insets(0, 20, 0, 20));

        return new JFXSnackbar.SnackbarEvent(textBox, Duration.seconds(3.33), null);
    }

    JFXComboBox<String> getComboBox(Collection<String> content, String promptText) {
        JFXComboBox<String> options = new JFXComboBox<>();
        options.setEditable(true);
        options.setPrefWidth(300);
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

    GridPane getDialogGrid() {
        GridPane grid = new GridPane();

        grid.setPadding(new Insets(0, 0, 0, 10));
        grid.setVgap(10);
        grid.setMinWidth(500);

        return grid;
    }
}
