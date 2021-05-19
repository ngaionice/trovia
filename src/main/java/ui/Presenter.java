package ui;

import com.jfoenix.controls.*;
import model.ModelController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import ui.searchables.Searchable;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import datamodel.parser.Parser;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Presenter {

    DesignProperties p;
    ModelController con;
    PresenterElementHelper elements;
    PresenterLogicHelper logic;
    JFXTreeView<String> dirView;

    String benchSubPath = "\\prefabs\\placeable\\crafting";
    String colSubPath = "\\prefabs\\collections";
    String itemSubPath = "\\prefabs\\item";
    String langSubPath = "\\languages\\en";
    String profSubPath = "\\prefabs\\professions";
    String recSubPath = "\\prefabs\\recipes";

    public Presenter(DesignProperties p) {
        this.p = p;
        elements = new PresenterElementHelper(p);
        con = new ModelController();
        logic = new PresenterLogicHelper(con);

        con.importDataLocal();
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
        pColBtn.setOnAction(event -> root.setCenter(setPaneCreateDirectory(root, colSubPath, logic.getColFilter(), Parser.ObjectType.COLLECTION)));
        pBenchBtn.setOnAction(event -> root.setCenter(setPaneCreateDirectory(root, benchSubPath, logic.getBenchFilter(), Parser.ObjectType.BENCH)));
        pItemBtn.setOnAction(event -> root.setCenter(setPaneCreateDirectory(root, itemSubPath, logic.getItemFilter(), Parser.ObjectType.ITEM)));
        pRecBtn.setOnAction(event -> root.setCenter(setPaneCreateDirectory(root, recSubPath, logic.getRecFilter(), Parser.ObjectType.RECIPE)));
        pLangBtn.setOnAction(event -> root.setCenter(setPaneCreateDirectory(root, langSubPath, logic.getLangFilter(), Parser.ObjectType.STRING)));
        pProfBtn.setOnAction(event -> root.setCenter(setPaneCreateDirectory(root, profSubPath, "", Parser.ObjectType.PROFESSION)));
        settingsBtn.setOnAction(event -> root.setCenter(setPaneCreateSettings()));

        // set-up the pane
        Button[] options = new Button[]{pBenchBtn, pColBtn, pItemBtn, pProfBtn, pRecBtn, pLangBtn, cGearBtn, settingsBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(elements.setPropVBox(typeNav, options, "#c7c7c7"));

    }

    void callView(BorderPane root, VBox nav, VBox mainNav) {

        // remove previous nav
        nav.getChildren().clear();

        // make new nav
        VBox typeNav = new VBox();
        typeNav.setBackground(p.backgroundMainSidebar);

        // buttons
        JFXButton articles = new JFXButton("All");
        JFXButton items = new JFXButton("Items");
        JFXButton collections = new JFXButton("Collections");
        JFXButton benches = new JFXButton("Benches");
        JFXButton languages = new JFXButton("Language files");

        // button actions
        List<Parser.ObjectType> allArticles = Arrays.asList(Parser.ObjectType.BENCH, Parser.ObjectType.COLLECTION, Parser.ObjectType.ITEM);

        articles.setOnAction(event -> root.setCenter(setPaneViewFiles(root, allArticles, "All Entries", false, "all")));
        items.setOnAction(event -> root.setCenter(setPaneViewFiles(root, Collections.singletonList(Parser.ObjectType.ITEM), "Items", true, "all")));
        collections.setOnAction(event -> root.setCenter(setPaneViewFiles(root, Collections.singletonList(Parser.ObjectType.COLLECTION), "Collections", true, "all")));
        benches.setOnAction(event -> root.setCenter(setPaneViewFiles(root, Collections.singletonList(Parser.ObjectType.BENCH), "Benches", true, "all")));
        languages.setOnAction(event -> root.setCenter(notImplemented()));

        Button[] options = new Button[]{articles, items, collections, benches, languages};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(elements.setPropVBox(typeNav, options, p.colorTextNormal));

        // update center table
        root.setCenter(setPaneViewFiles(root, allArticles, "All Articles", false, "all"));
    }

    void callSync(BorderPane root, VBox nav, VBox mainNav) {

        List<Parser.ObjectType> allArticles = Arrays.asList(Parser.ObjectType.BENCH, Parser.ObjectType.COLLECTION, Parser.ObjectType.ITEM);

        // update center table
        root.setCenter(setPaneViewFiles(root, allArticles, "New Entries", false, "new"));

        // remove previous nav
        nav.getChildren().clear();

        // make new nav
        VBox typeNav = new VBox();
        typeNav.setBackground(p.backgroundMainSidebar);

        // buttons
        JFXButton addBtn = new JFXButton("Review new entries");
        JFXButton removeBtn = new JFXButton("Review deleted entries");
        JFXButton syncBtn = new JFXButton("Sync database");

        // button actions
        addBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(root, allArticles, "New Entries", false, "new")));
        removeBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(root, allArticles, "Removed Entries", false, "removed")));
        syncBtn.setOnAction(event -> root.setCenter(setSyncPane()));

        Button[] options = new Button[]{addBtn, removeBtn, syncBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(elements.setPropVBox(typeNav, options, p.colorTextNormal));
    }

    // CREATE-RELATED

    AnchorPane setPaneCreateSettings() {

        // clears previous saved paths
        logic.clearParseList();

        GridPane grid = new GridPane();
        elements.setPropGridPane(grid, new Insets(80, 50, 70, 50), 20);

        int textFieldWidth = 500;

        Text settingsText = elements.getTextH1("Parse Settings", p.colorTextHeader);

        Text dirText = elements.getTextH3("Absolute path of main directory:", p.colorTextNormal);
        JFXTextField dirField = elements.getTextField("Current directory: " + logic.getDirPath(), textFieldWidth);

        // bench, col, item, rec, lang
        List<Text> subheaders = new ArrayList<>();
        List<JFXTextField> fields = new ArrayList<>();

        List<String> vars = Arrays.asList("Filter for bench parsing: (filters out files without the keyword)",
                "Filter for collection parsing: ",
                "Filter for item parsing: ",
                "Filter for recipe parsing:",
                "Filter for language file parsing: ");
        AtomicReference<List<String>> filters = new AtomicReference<>(Arrays.asList(logic.getBenchFilter(), logic.getColFilter(), logic.getItemFilter(), logic.getRecFilter(), logic.getLangFilter()));
        
        // get subheaders and text fields
        for (int i = 0; i < vars.size(); i++) {
            subheaders.add(elements.getTextH3(vars.get(i), p.colorTextNormal));
            fields.add(elements.getTextField("Current filter: " + filters.get().get(i), textFieldWidth));
        }

        JFXButton save = elements.getButton("Save", 60, 35, p.backgroundMainButton, p.colorTextMainButton);

        save.setOnAction(event -> {
            boolean saved = logic.saveParseSettings(dirField.getText(), fields.get(0).getText(), fields.get(1).getText(), fields.get(2).getText(), fields.get(3).getText(), fields.get(4).getText());

            if (saved) {
                dirField.clear();
                dirField.setPromptText("Current directory: " + logic.getDirPath());
            }

            filters.set(Arrays.asList(logic.getBenchFilter(), logic.getColFilter(), logic.getItemFilter(), logic.getRecFilter(), logic.getLangFilter()));
            for (int i = 0; i < fields.size(); i++) {
                fields.get(i).setPromptText("Current filter: " + filters.get().get(i));
                fields.get(i).clear();
            }
        });

        List<Node> gridItems = new ArrayList<>(Arrays.asList(settingsText, dirText, dirField));
        for (int i = 0; i < fields.size(); i++) {
            gridItems.add(subheaders.get(i));
            gridItems.add(fields.get(i));
        }

        // add normal items
        for (int i = 0; i < gridItems.size(); i++) {
            grid.add(gridItems.get(i), 0, i);
            RowConstraints con = new RowConstraints();
            con.setPrefHeight(30);
            grid.getRowConstraints().add(con);
        }

        return elements.getAnchorPane(grid, save, false);
    }

    GridPane setPaneCreateDirectory(BorderPane root, String subPath, String filter, Parser.ObjectType type) {

        StackPane currPane = new StackPane();
        currPane.setBackground(p.backgroundMainPane);

        // clears previous saved paths
        logic.clearParseList();

        // update dirView to show the directory, and put it into the StackPane
        boolean isCollection = type == Parser.ObjectType.COLLECTION;
        CheckBoxTreeItem<String> content = logic.getFileTree(logic.getDirPath() + subPath, filter, isCollection);
        content.setExpanded(true);

        dirView = elements.getTreeView(root, content);
        currPane.getChildren().add(dirView);

        // create header and progress indicators
        String plural = type == Parser.ObjectType.BENCH ? "es" : "s";
        Text headerText = elements.getTextH1("Parse " + type + plural, p.colorTextHeader);

        JFXProgressBar progressBar = new JFXProgressBar();
        final Text progressText = new Text("Status");
        VBox progressBox = elements.getProgressBox(root, progressText, progressBar);

        // create button to start parsing - needs to show dialog box when there are failed files
        JFXButton startParse = elements.getButton("Parse", 60, 35, p.backgroundMainButton, p.colorTextMainButton);

        startParse.setOnAction(event -> {

            // add button
            Task<Void> task = logic.getParseTask(type);
            progressBar.progressProperty().bind(task.progressProperty());
            progressText.textProperty().bind(task.messageProperty());

            // adds background thread that runs the task, and shows the dialog box after the task is done
            Thread thread = new Thread(() -> {
                task.run();

                Platform.runLater(() -> {
                    if (!logic.getFailedPaths().isEmpty()) {
                        JFXButton button = elements.getButton("Close", 60, 35, p.backgroundDialog, p.colorTextDialog);
                        JFXDialog dialog = elements.getDialog(currPane, new Text("Incomplete parsing"), elements.getFailedContent(logic.getDirPath(), logic.getFailedPaths()), button);

                        button.setOnAction(event1 -> dialog.close());
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
        elements.setPropGridPane(grid, new Insets(80, 50, 20, 50), 0);
        elements.setNodeGridPane(grid, Arrays.asList(headerText, currPane, buttonBox, progressBox));

        GridPane.setMargin(currPane, new Insets(30, 30, 30, 0));
        GridPane.setMargin(progressBox, new Insets(35, 30, 10, 0));

        return grid;
    }

    // VIEW-RELATED

    StackPane setPaneViewFiles(BorderPane root, List<Parser.ObjectType> types, String headerText, boolean modifiable, String mapType) {

        // set up StackPane to hold dialog box and TableView
        StackPane tablePane = new StackPane();
        GridPane grid = new GridPane();

        // set up dialog
        AtomicReference<JFXDialog> dialogInfo = new AtomicReference<>(new JFXDialog());

        // set up table and map of selected items
        TableView<Searchable> table = elements.getTableView(root);
        Map<Searchable, BooleanProperty> checkedRows = new HashMap<>();

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
        {nameCol.getStyleClass().add("text-col");
        rPathCol.getStyleClass().add("text-col");}

        // set up row
        table.setRowFactory(view -> {
            TableRow<Searchable> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {

                    int selectedRow = table.getSelectionModel().getSelectedCells().get(0).getRow();
                    Searchable article = table.getItems().get(selectedRow);
                    VBox content =
                            article.getRPath().contains("item") ? elements.getItemContent(article.getRPath(), con) :
                            article.getRPath().contains("collection") ? elements.getCollectionContent(article.getRPath(), con, logic) :
                                    elements.getBenchContent(article.getRPath(), con);

                    JFXButton button = elements.getButton("Close", 60, 35, p.backgroundDialogButton, p.colorTextDialogButton);
                    button.setOnAction(event1 -> dialogInfo.get().close());

                    dialogInfo.set(elements.getDialog(tablePane, elements.getTextH2(article.getName(), p.colorTextDialogButton), content, button));
                    dialogInfo.get().show();
                }
            });
            return row;
        });

        // if modifiable is true, add the checkbox column and the edit button
        if (modifiable) {
            TableColumn<Searchable, Void> checkCol = new TableColumn<>();

            CheckBox checkAll = new CheckBox();
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
            JFXButton edit = new JFXButton("Edit");
            AnchorPane anchor = elements.getAnchorPane(grid, edit, false);
            tablePane.getChildren().add(anchor);

            AtomicReference<List<String>> rPathsToEdit = new AtomicReference<>(new ArrayList<>());

            edit.setOnAction(evt -> {
                rPathsToEdit.set(checkedRows.entrySet().stream().filter(e -> e.getValue().get()).map(Map.Entry::getKey)
                        .map(Searchable::getRPath).collect(Collectors.toList()));
                JFXDialog dialogEdit = getEditPane(tablePane, rPathsToEdit.get(), types.get(0));
                dialogEdit.setStyle("-fx-background-color: #1B1B1BBB");

                dialogEdit.show();
            });
        } else {
            table.getColumns().setAll(Arrays.asList(nameCol, rPathCol));
            tablePane.getChildren().add(grid);
        }

        // get and set content
        ObservableList<Searchable> articles = logic.getSearchableList(types, "", mapType);
        FilteredList<Searchable> filtered = new FilteredList<>(articles, p -> true);    // allows filtering
        SortedList<Searchable> sortable = new SortedList<>(filtered);

        // set up search
        String currType = types.size() != 1 ? "entries" : types.get(0).toString().toLowerCase();
        String plural = currType.equals("entries") ? "" : currType.equals("bench") ? "es" : "s";
        JFXTextField searchField = elements.getTextField("Search " + articles.size() + " " + currType + plural + " by name", root.widthProperty().multiply(0.6));
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

        Text header = elements.getTextH1(headerText, p.colorTextHeader);
        headerBox.getChildren().addAll(Arrays.asList(header, searchField));

        // set up grid
        elements.setPropGridPane(grid, new Insets(80, 50, 70, 50), 20);
        elements.setNodeGridPane(grid, Arrays.asList(headerBox, table));

        GridPane.setMargin(headerBox, new Insets(0, 0, 10, 0));

        return tablePane;
    }

    GridPane getDialogPaneSelectedObjects(List<String> rPaths) {
        GridPane grid = new GridPane();

        grid.setPadding(new Insets(0, 0, 0, 10));
        grid.setVgap(10);
        grid.setMinWidth(500);

        Text header = elements.getTextH3("Currently selected objects:", p.colorTextDialogButton);
        TextArea texts = elements.getTextArea(String.join("\n", rPaths));
        grid.add(header, 0, 0);
        grid.add(texts, 0, 1);

        return grid;
    }

    // SYNC-RELATED

    GridPane setSyncPane() {
        GridPane grid = new GridPane();
        elements.setPropGridPane(grid, new Insets(80, 50, 20, 50), 0);
        grid.setVgap(10);

        Text header = elements.getTextH1("Save changes", p.colorTextHeader);

        JFXButton ser = elements.getButton("Serialize", 200, 35, p.backgroundMainButton, p.colorTextMainButton);
        JFXButton mongoAll = elements.getButton("Sync all data to MongoDB", 200, 35, p.backgroundMainButton, p.colorTextMainButton);
        JFXButton mongoChanges = elements.getButton("Sync changes to MongoDB", 200, 35, p.backgroundMainButton, p.colorTextMainButton);
        JFXButton clear = elements.getButton("Clear changed list", 200, 35, p.backgroundMainButton, p.colorTextMainButton);

        JFXProgressBar progressBar = new JFXProgressBar();
        final Text progressText = new Text("Status");
        VBox progressBox = elements.getProgressBox(grid, progressText, progressBar);
        progressBar.setProgress(0);

        ser.setOnAction(e -> con.exportDataLocal());
        mongoAll.setOnAction(e -> {

            Task<Void> task = logic.getSyncTask(true);
            progressBar.setProgress(-1);
            progressText.textProperty().bind(task.messageProperty());

            // adds background thread that syncs
            Thread thread = new Thread(() -> {
                task.run();
                Platform.runLater(() -> progressBar.setProgress(1));
            });
            thread.start();
        });
        mongoChanges.setOnAction(e -> {

            Task<Void> task = logic.getSyncTask(false);
            progressBar.setProgress(-1);
            progressText.textProperty().bind(task.messageProperty());

            // adds background thread that syncs
            Thread thread = new Thread(() -> {
                task.run();
                Platform.runLater(() -> progressBar.setProgress(1));
            });
            thread.start();
        });
        clear.setOnAction(e -> con.clearChanges());

        elements.setNodeGridPane(grid, Arrays.asList(header, ser, mongoAll, mongoChanges, clear, progressBox));
        GridPane.setMargin(header, new Insets(0, 0, 100, 0));
        GridPane.setMargin(progressBox, new Insets(200, 0, 0, 0));
        return grid;
    }

    // MODIFY-RELATED

    JFXDialog getEditPane(StackPane root, List<String> rPaths, Parser.ObjectType type) {

        // create the dialog
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(root, dialogLayout, JFXDialog.DialogTransition.CENTER);
        dialogLayout.setBackground(p.backgroundDialog);

        BorderPane dialogRoot = new BorderPane();
        dialogRoot.prefWidthProperty().bind(root.widthProperty());

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

        Button[] options =
                type == Parser.ObjectType.ITEM ? new Button[] {selected, decons, lootbox, notes, recipe} :
                type == Parser.ObjectType.COLLECTION ? new Button[] {selected, mastery, notes, recipe} :
                        new Button[] {selected, name, match};

        // button configs
        selected.setOnAction(e -> dialogRoot.setCenter(getDialogPaneSelectedObjects(rPaths)));

        notes.setOnAction(e -> dialogRoot.setCenter(getDialogPaneAddNotes(rPaths)));
        recipe.setOnAction(e -> dialogRoot.setCenter(getDialogPaneMatchRecipes()));

        mastery.setOnAction(e -> dialogRoot.setCenter(getDialogPaneSetMastery(rPaths)));

        lootbox.setOnAction(e -> dialogRoot.setCenter(getDialogPaneAddItemProperty(rPaths, true)));
        decons.setOnAction(e -> dialogRoot.setCenter(getDialogPaneAddItemProperty(rPaths, false)));

        name.setOnAction(e -> dialogRoot.setCenter(getDialogPaneSetBench(rPaths)));
        match.setOnAction(e -> dialogRoot.setCenter(getDialogPaneMatchBenchRecipes(rPaths)));


        // control the pane based on buttons pressed
        dialogRoot.setLeft(getEditSideBar(root, options));
        dialogRoot.setCenter(getDialogPaneSelectedObjects(rPaths));

        String plural = rPaths.size() != 1 ? "s" : "";
        dialogLayout.setHeading(elements.getTextH2("Editing " + rPaths.size() + " object" + plural, p.colorTextDialog));
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

    AnchorPane getDialogPaneAddNotes(List<String> rPaths) {
        GridPane grid = elements.getDialogGrid();

        JFXTextField noteField = elements.getTextField("e.g. Hexion needs these", 350);
        JFXButton save = new JFXButton("Save");
        AnchorPane anchor = elements.getAnchorPane(grid, save, true);
        JFXSnackbar notif = new JFXSnackbar(anchor);

        save.setOnAction(e -> {
            logic.addNotes(rPaths, noteField.getText());
            JFXSnackbar.SnackbarEvent status = elements.getSnackbarEvent("Note saved");
            notif.enqueue(status);
        });

        grid.add(elements.getTextH3("Note to be added:", p.colorTextDialogButton), 0, 0);
        grid.add(elements.getTextNormal("Note that this note is added to every selected object.", p.colorTextDialogButton), 0, 1);
        grid.add(noteField, 0, 2);

        return anchor;
    }

    AnchorPane getDialogPaneMatchRecipes() {
        GridPane grid = elements.getDialogGrid();

        JFXButton match = new JFXButton("Go");
        AnchorPane anchor = elements.getAnchorPane(grid, match, true);
        JFXSnackbar notif = new JFXSnackbar(anchor);
        match.setOnAction(e -> {
            List<String> failed = logic.matchNewRecipes();
            grid.add(failed != null ? elements.getTextArea(String.join("\n", failed)) :
                    elements.getTextNormal("All recipes matched.", p.colorTextDialog), 0, 1);

            String text = failed == null ? "All recipes matched" : "Some recipes were not matched";
            JFXSnackbar.SnackbarEvent status = elements.getSnackbarEvent(text);
            notif.enqueue(status);
        });

        grid.add(elements.getTextH3("Match newly added recipes", p.colorTextDialogButton), 0, 0);
        return anchor;
    }

    AnchorPane getDialogPaneSetMastery(List<String> rPaths) {
        GridPane grid = elements.getDialogGrid();

        JFXTextField trove = elements.getTextField("Example: 250", 250);
        JFXTextField geode = elements.getTextField("Leave blank if not applicable", 250);
        List<Node> nodes = Arrays.asList(elements.getTextH3("Trove Mastery:", p.colorTextDialogButton), trove,
                elements.getTextH3("Geode Mastery:", p.colorTextDialogButton), geode,
                elements.getTextNormal("Note that input have to be positive integers; leave blank if not applicable.", p.colorTextDialogButton));

        elements.setNodeGridPane(grid, nodes);

        JFXButton save = new JFXButton("Save");
        AnchorPane anchor = elements.getAnchorPane(grid, save, true);
        JFXSnackbar confirm = new JFXSnackbar(anchor);

        save.setOnAction(e -> { // behavior: save when all inputs are valid
            boolean status = true;
            String intRegex = "\\d+";

            // TODO: factor out this logic to logic helper
            if (((!trove.getText().isEmpty() && trove.getText().matches(intRegex)) || trove.getText().isEmpty()) &&
                    ((!geode.getText().isEmpty() && geode.getText().matches(intRegex)) || geode.getText().isEmpty())) {
                for (String collection: rPaths) {
                    if (!trove.getText().isEmpty()) {
                        con.setTroveMastery(collection, Integer.parseInt(trove.getText()));
                    }
                    if (!geode.getText().isEmpty()) {
                        con.setGeodeMastery(collection, Integer.parseInt(geode.getText()));
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

            String text = status ? "Mastery set" : "Invalid input(s), try again";
            JFXSnackbar.SnackbarEvent confirmEvent = elements.getSnackbarEvent(text);
            confirm.enqueue(confirmEvent);
        });

        return anchor;
    }

    AnchorPane getDialogPaneSetBench(List<String> rPaths) {
        GridPane grid = elements.getDialogGrid();

        // get options for combo box
        Map<String, String> benchEntries = con.getAllStringsFromFile("languages/en/prefabs_placeable_crafting");
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

        JFXComboBox<String> choices = elements.getComboBox(benchNames.values(), "Name", 300.0);
        JFXAutoCompletePopup<String> autoCompletePopup = elements.getAutoCompletePopup(benchNames.values(), choices);
        TextField field = elements.getComboBoxEditor(choices, autoCompletePopup);

        // set up save button
        JFXButton save = new JFXButton("Save");
        AnchorPane anchor = elements.getAnchorPane(grid, save, true);
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
                    con.setBenchName(rPath, identifier);
                }
            }

            String text = identifier != null ? "Bench name set" : "Invalid input, try again";
            JFXSnackbar.SnackbarEvent confirmEvent = elements.getSnackbarEvent(text);
            confirm.enqueue(confirmEvent);
        });

        grid.add(elements.getTextH3("Add name to bench", p.colorTextDialogButton), 0, 0);
        grid.add(choices, 0, 1);

        return anchor;
    }

    AnchorPane getDialogPaneMatchBenchRecipes(List<String> rPaths) {

        GridPane grid = elements.getDialogGrid();
        grid.add(elements.getTextH3("Match recipes for the selected benches", p.colorTextDialogButton), 0, 0);

        for (int i = 0; i < rPaths.size(); i++) {
            grid.add(elements.getTextNormal(con.getName(rPaths.get(i)) + " - " + con.getBenchRecipes(rPaths.get(i)).size() + " recipes", p.colorTextDialogButton), 0, i + 1);
        }

        JFXButton match = new JFXButton("Go");
        AnchorPane anchor = elements.getAnchorPane(grid, match, true);
        JFXSnackbar confirm = new JFXSnackbar(anchor);

        match.setOnAction(e -> {
            List<String> unmatched = new ArrayList<>();
            for (String rPath: rPaths) {
                unmatched.addAll(con.matchBenchToRecipes(rPath));
            }

            grid.getChildren().clear();

            String outputHeader = unmatched.isEmpty() ? "All recipes matched" : "Unmatched recipes:";
            grid.add(elements.getTextH3(outputHeader, p.colorTextDialogButton), 0, 0);
            grid.add(elements.getTextNormal(String.join("\n", unmatched), p.colorTextDialogButton), 0, 1);

            String text = "Matching complete";

            JFXSnackbar.SnackbarEvent confirmEvent = elements.getSnackbarEvent(text);
            confirm.enqueue(confirmEvent);
        });

        return anchor;
    }

    AnchorPane getDialogPaneAddItemProperty(List<String> rPaths, boolean lootbox) {
        GridPane grid = elements.getDialogGrid();
        grid.setHgap(10);

        AtomicInteger items = new AtomicInteger(1);

        // set up header
        String headerType = lootbox ? "Add lootbox" : "Set deconstruction";

        Text headerText = elements.getTextH3(headerType + " content", p.colorTextDialogButton);
        HBox headerBox = new HBox();

        JFXComboBox<String> rarity = elements.getComboBox(Arrays.asList("Common", "Uncommon", "Rare"), "Rarity", 125);
        JFXButton add = elements.getButton("Add field", 75, 30, p.backgroundDialogButton, p.colorTextDialogButton);

        headerBox.getChildren().setAll(lootbox ? Arrays.asList(headerText, rarity, add) : Arrays.asList(headerText, add));

        headerBox.setSpacing(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // set up data
        List<String> itemNames = new ArrayList<>();

        ObservableList<Searchable> nameAndRPathList = logic.getSearchableList(Collections.singletonList(Parser.ObjectType.ITEM), "", "all");
        for (Searchable item: nameAndRPathList) {
            itemNames.add(item.getName() + " - " + item.getRPath());
        }

        List<JFXComboBox<String>> comboBoxes = new ArrayList<>();
        List<JFXAutoCompletePopup<String>> autoCompletePopups = new ArrayList<>();
        List<TextField> fields = new ArrayList<>();

        List<JFXTextField> quantities = new ArrayList<>();

        comboBoxes.add(elements.getComboBox(itemNames, "Item", 300.0));
        quantities.add(elements.getTextField("Quantity", 50));
        autoCompletePopups.add(elements.getAutoCompletePopup(itemNames, comboBoxes.get(0)));
        fields.add(elements.getComboBoxEditor(comboBoxes.get(0), autoCompletePopups.get(0)));

        // set up save button
        JFXButton save = new JFXButton("Save");
        AnchorPane anchor = elements.getAnchorPane(grid, save, true);
        JFXSnackbar confirm = new JFXSnackbar(anchor);

        save.setOnAction(event -> {
            boolean status = logic.itemPropertyInputValidation(comboBoxes, itemNames, quantities, items.get(), lootbox);
            if (status) {
                String lootboxRarity = rarity.getValue().toLowerCase();
                logic.itemPropertyInsert(comboBoxes, rPaths, quantities, items.get(), lootbox, lootboxRarity);
            }

            String text = status ? "Saved" : "Invalid input, try again";
            JFXSnackbar.SnackbarEvent confirmEvent = elements.getSnackbarEvent(text);
            confirm.enqueue(confirmEvent);
        });

        add.setOnAction(e -> {
            if (items.get() < 7) {
                items.getAndIncrement();

                comboBoxes.add(elements.getComboBox(itemNames, "Item", 300.0));
                quantities.add(elements.getTextField("Quantity", 50));
                autoCompletePopups.add(elements.getAutoCompletePopup(itemNames, comboBoxes.get(items.get() - 1)));
                fields.add(elements.getComboBoxEditor(comboBoxes.get(items.get() - 1), autoCompletePopups.get(items.get() - 1)));

                grid.add(comboBoxes.get(items.get() - 1), 0, items.get());
                grid.add(quantities.get(items.get() - 1), 1, items.get());
            }
        });

        grid.add(headerBox, 0, 0, 2, 1);
        grid.add(comboBoxes.get(0), 0, 1);
        grid.add(quantities.get(0), 1, 1);

        return anchor;
    }

    // MISC

    // exists for view to use only
    void setPropVBox(VBox vBox, Button[] options, String buttonTextColor) {
        elements.setPropVBox(vBox, options, buttonTextColor);
    }

    StackPane notImplemented() {

        Text display = new Text("This feature is not yet implemented.");
        display.setFill(Paint.valueOf(p.colorTextHeader));
        display.setFont(p.fontH2);
        StackPane pane = new StackPane();
        pane.setBackground(p.backgroundMainPane);
        pane.getChildren().add(display);

        return pane;
    }



    

    
}
