package ui2;

import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;
import datamodel.CollectionEnums;
import datamodel.objects.ArticleTable;
import datamodel.objects.ObservableStrings;
import datamodel.parser.Parser;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Presenter {

    Stage stage;
    Scene scene;
    BorderPane root;
    UIController controller;
    TextArea logger;

    public Presenter(Stage stage, Scene sc, BorderPane root) {
        this.stage = stage;
        this.scene = sc;
        this.root = root;
        this.controller = new UIController();
        this.logger = getLogger();
    }

    public VBox getNavBar() {
        VBox navBox = new VBox();
        Region spacer = new Region();
        JFXButton parseButton = new JFXButton("Parse");
        JFXButton editButton = new JFXButton("Edit");
        JFXButton syncButton = new JFXButton("Sync");
        JFXButton reviewButton = new JFXButton("Review");
        JFXButton loadButton = new JFXButton("Load");
        JFXButton createButton = new JFXButton("Create");
        JFXButton logsButton = new JFXButton("Logs");
        JFXButton quitButton = new JFXButton("Quit");
        Separator separator = new Separator();
        Separator separator2 = new Separator();

        List<Button> actionButtons = Arrays.asList(parseButton, editButton, syncButton, reviewButton);
        List<Button> databaseButtons = Arrays.asList(loadButton, createButton);
        List<JFXButton> buttonList = Arrays.asList(parseButton, editButton, syncButton, reviewButton, loadButton, createButton, logsButton, quitButton);
        String[] buttonIds = new String[]{"button-parse", "button-edit", "button-sync", "button-review", "button-load", "button-create", "button-logs", "button-quit"};

        spacer.prefHeightProperty().bind(scene.heightProperty().multiply(0.125));
        for (int i = 0; i < buttonIds.length; i++) {
            buttonList.get(i).getStyleClass().add("nav-button");
            buttonList.get(i).setGraphic(new FontIcon());
            buttonList.get(i).setId(buttonIds[i]);
        }
        navBox.getChildren().addAll(buttonList);
        navBox.getChildren().add(4, separator);
        navBox.getChildren().add(7, separator2);
        navBox.getChildren().add(0, spacer);
        navBox.setId("nav-box");
        separator.getStyleClass().add("nav-separator");
        separator2.getStyleClass().add("nav-separator");

        // set button actions
        parseButton.setOnAction(e -> setParseScreen());
        editButton.setOnAction(e -> setEditScreen());
        syncButton.setOnAction(e -> setSyncScreen());
        reviewButton.setOnAction(e -> setReviewScreen());

        loadButton.setOnAction(e -> {
            boolean enable = controller.loadDatabase(stage, logger);
            if (enable) {
                controller.enableActionButtons(actionButtons);
                controller.disableActionButtons(databaseButtons);
            }
        });
        createButton.setOnAction(e -> {
            boolean enable = controller.createDatabase(stage, logger);
            if (enable) {
                controller.enableActionButtons(actionButtons);
                controller.disableActionButtons(databaseButtons);
            }
        });

        logsButton.setOnAction(e -> setLogsScreen());
        quitButton.setOnAction(e -> runQuitSequence());

        controller.disableActionButtons(actionButtons);

        return navBox;
    }

    private void setParseScreen() {
        BorderPane screenRoot = new BorderPane();
        HBox header = new HBox();
        Text headerText = new Text("Parse");

        header.getChildren().add(headerText);

        JFXTabPane tabs = new JFXTabPane();
        Tab benches = getParseTab("Benches", Parser.ObjectType.BENCH);
        Tab collections = getParseTab("Collections", Parser.ObjectType.COLLECTION);
        Tab items = getParseTab("Items", Parser.ObjectType.ITEM);
        Tab placeables = getParseTab("Placeables", Parser.ObjectType.PLACEABLE);
        Tab professions = getParseTab("Professions", Parser.ObjectType.PROFESSION);
        Tab recipes = getParseTab("Recipes", Parser.ObjectType.RECIPE);
        Tab strings = getParseTab("Strings", Parser.ObjectType.STRING);
        tabs.getTabs().addAll(benches, collections, items, placeables, professions, recipes, strings);

        // debatable design choice: clear all selections on tab switch
        // issue: different types of paths on different tabs, ideally one list for each but might clog things up even more
//        tabs.getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {
//            if (!newValue.equals(oldValue)) {
//                controller.clearSelectedPaths();
//            }
//        }));

        header.getStyleClass().add("header");
        headerText.getStyleClass().add("header-text");

        screenRoot.setTop(header);
        screenRoot.setCenter(tabs);
        screenRoot.setBottom(getLogsRegion());
        root.setCenter(screenRoot);
    }

    private Tab getParseTab(String name, Parser.ObjectType type) {
        Tab tab = new Tab(name);

        StackPane root = new StackPane();
        AnchorPane anchor = new AnchorPane();
        GridPane grid = new GridPane();
        JFXTextField directory = new JFXTextField();
        JFXButton dirButton = new JFXButton();
        JFXTextField filter = new JFXTextField();
        JFXButton filterButton = new JFXButton();
        TreeView<String> tree = new TreeView<>();
        JFXButton startButton = new JFXButton();
        JFXProgressBar progressBar = new JFXProgressBar();
        Text progressText = new Text("Status");
        VBox progressBox = new VBox();

        directory.setPromptText("Directory");
        directory.setDisable(true);
        filter.setPromptText("Filter");
        filter.setText(controller.getFilterText(type));
        progressBar.setProgress(0);

        dirButton.setOnAction(e -> controller.setParseDirectory(stage, directory, tree, type));
        filterButton.setOnAction(e -> controller.updateParseDirectory(filter, directory, tree, type));
        startButton.setOnAction(e -> controller.parse(progressBar, progressText, logger, type));

        root.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");
        grid.getStyleClass().add("grid-content");
        directory.getStyleClass().add("text-field-dir");
        filter.getStyleClass().add("text-field-filter");
        tree.getStyleClass().add("dir-view");
        dirButton.getStyleClass().addAll("button-inline", "color-subtle");
        filterButton.getStyleClass().addAll("button-inline", "color-subtle");
        startButton.getStyleClass().addAll("floating-button", "button-start");
        dirButton.setId("button-set-dir");
        filterButton.setId("button-update");
        progressText.getStyleClass().add("text-normal");
        progressText.setId("text-progress");

        dirButton.setGraphic(new FontIcon());
        filterButton.setGraphic(new FontIcon());
        startButton.setGraphic(new FontIcon());
        tree.setCellFactory(CheckBoxTreeCell.forTreeView());
        tree.prefWidthProperty().bind(root.widthProperty().multiply(0.80));
        tree.prefHeightProperty().bind(root.heightProperty().multiply(0.68));
        progressBar.prefWidthProperty().bind(root.widthProperty().multiply(0.80));

        tab.setContent(root);
        root.getChildren().add(anchor);
        anchor.getChildren().add(grid);
        anchor.getChildren().add(startButton);
        progressBox.getChildren().addAll(progressText, progressBar);
        grid.add(directory, 0, 0);
        grid.add(dirButton, 1, 0);
        grid.add(filter, 2, 0);
        grid.add(filterButton, 3, 0);
        grid.add(progressBox, 0, 1, 4, 1);
        grid.add(tree, 0, 2, 4, 1);

        setMaxAnchor(grid);
        setFabAnchor(startButton);

        JFXDepthManager.setDepth(anchor, 1);
        return tab;
    }

    private void setEditScreen() {
        BorderPane screenRoot = new BorderPane();
        HBox header = new HBox();
        Text headerText = new Text("Edit");

        header.getChildren().add(headerText);

        JFXTabPane tabs = new JFXTabPane();
        Tab benches = new Tab("Benches");
        Tab collections = new Tab("Collections");
        Tab items = new Tab("Items");
        Tab placeables = new Tab("Placeables");
        Tab recipes = new Tab("Recipes");
        Tab strings = new Tab("Strings");
        tabs.getTabs().addAll(benches, collections, items, placeables, recipes, strings);

        tabs.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            // load tab on switch, don't pre-load
            // index mapping
            setEditTabContent(newValue);
        }));

        tabs.getSelectionModel().selectFirst();

        header.getStyleClass().add("header");
        headerText.getStyleClass().add("header-text");

        screenRoot.setTop(header);
        screenRoot.setCenter(tabs);
        root.setCenter(screenRoot);
    }

    private void setEditTabContent(Tab tab) {
        BorderPane root = new BorderPane();
        StackPane tablePane = new StackPane();
        StackPane backingTablePane = new StackPane();
        StackPane backingGridPane = new StackPane();
        GridPane sidebar = new GridPane();

        backingGridPane.getChildren().add(sidebar);
        backingTablePane.getChildren().add(tablePane);

        root.setCenter(backingTablePane);
        root.setRight(backingGridPane);

        backingTablePane.getStyleClass().add("pane-background");
        backingGridPane.getStyleClass().add("pane-background-no-left");
        tablePane.getStyleClass().add("card-backing");
        sidebar.getStyleClass().addAll("card-backing", "grid-sidebar");

        JFXDepthManager.setDepth(tablePane, 1);
        JFXDepthManager.setDepth(sidebar, 1);

        sidebar.prefWidthProperty().bind(root.widthProperty().multiply(0.2));

        String name = tab.getText();
        switch (name) {
            case "Benches":
                setEditTabTable(tablePane, UIController.TabType.BENCH);
                setEditTabBenchSidebar(sidebar);
                break;
            case "Collections":
                setEditTabTable(tablePane, UIController.TabType.COLLECTION);
                setEditTabCollectionSidebar(sidebar);
                break;
            case "Items":
                setEditTabTable(tablePane, UIController.TabType.ITEM);
                setEditTabItemSidebar(sidebar);
                break;
            case "Placeables":
                setEditTabTable(tablePane, UIController.TabType.PLACEABLE);
                setEditTabPlaceableSidebar(sidebar);
                break;
            case "Recipes":
                setEditTabTable(tablePane, UIController.TabType.RECIPE);
                setEditTabRecipeSidebar(sidebar);
                break;
            case "Strings":
                setEditTabStringsTable(tablePane);
                break;
        }

        tab.setContent(root);
    }

    private void setSyncScreen() {

    }

    private void setReviewScreen() {

    }

    private void setLogsScreen() {
        BorderPane screenRoot = new BorderPane();
        StackPane stack = new StackPane();
        AnchorPane anchor = new AnchorPane();
        HBox header = new HBox();
        Text headerText = new Text("Logs");
        JFXButton dumpButton = new JFXButton();

        dumpButton.setOnAction(e -> controller.dumpLogs(stage, logger));

        header.getChildren().add(headerText);
        stack.getChildren().add(anchor);
        anchor.getChildren().add(logger);
        anchor.getChildren().add(dumpButton);

        header.getStyleClass().add("header");
        headerText.getStyleClass().add("header-text");
        stack.getStyleClass().add("pane-background");
        anchor.getStyleClass().add("card-backing");
        dumpButton.getStyleClass().add("floating-button");
        dumpButton.setId("button-dump");

        dumpButton.setGraphic(new FontIcon());

        setMaxAnchor(logger);
        setFabAnchor(dumpButton);

        JFXDepthManager.setDepth(anchor, 1);

        screenRoot.setTop(header);
        screenRoot.setCenter(stack);
        root.setCenter(screenRoot);
    }

    private StackPane getLogsRegion() {
        StackPane root = new StackPane();
        StackPane loggerWrapper = new StackPane();
        root.getStyleClass().add("pane-background-no-top");
        root.getChildren().add(loggerWrapper);
        loggerWrapper.getChildren().add(logger);
        JFXDepthManager.setDepth(loggerWrapper, 1);

        return root;
    }

    private TextArea getLogger() {
        TextArea logger = new JFXTextArea();
        logger.setEditable(false);
        logger.setWrapText(true);
        logger.setId("logger");
        return logger;
    }

    private void runQuitSequence() {
        // need to add more stuff such as checking data is saved, etc
        Platform.exit();
    }

    // HELPER METHODS

    private void setMaxAnchor(Node node) {
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
    }

    private void setFabAnchor(Node node) {
        AnchorPane.setRightAnchor(node, 36.0);
        AnchorPane.setBottomAnchor(node, 36.0);
    }

    private void setEditTabTable(StackPane tablePane, UIController.TabType type) {
        TableView<ArticleTable> table = new TableView<>();
        table.prefWidthProperty().bind(stage.widthProperty().multiply(0.6));
        TableColumn<ArticleTable, String> rPathColumn = new TableColumn<>("Relative Path");
        TableColumn<ArticleTable, String> nameColumn = new TableColumn<>("Name");

        table.getColumns().setAll(!type.equals(UIController.TabType.RECIPE) ? Arrays.asList(nameColumn, rPathColumn) : Collections.singletonList(rPathColumn));
        tablePane.getChildren().add(table);
        controller.setEditTabTable(table, rPathColumn, nameColumn, type);
        String plural = type.toString().equals("bench") ? "es" : "s";
        table.setPlaceholder(new Label("No " + type + plural + " imported"));
    }

    private void setEditTabStringsTable(StackPane tablePane) {
        BorderPane border = new BorderPane();
        JFXComboBox<String> comboBox = new JFXComboBox<>();
        TableView<UIController.KVPair> table = new TableView<>();
        table.prefWidthProperty().bind(stage.widthProperty().multiply(0.6));
        TableColumn<UIController.KVPair, String> idCol = new TableColumn<>("Identifier");
        TableColumn<UIController.KVPair, String> contentCol = new TableColumn<>("Content");

        table.getColumns().setAll(Arrays.asList(idCol, contentCol));
        table.setPlaceholder(new Label("No strings imported."));

        controller.setEditTabStringsTable(comboBox, table, idCol, contentCol);

        BorderPane.setMargin(comboBox, new Insets(0, 0, 4, 0));
        idCol.prefWidthProperty().bind(table.widthProperty().multiply(0.32));
        contentCol.prefWidthProperty().bind(table.widthProperty().multiply(0.64));
        border.prefHeightProperty().bind(tablePane.heightProperty());

        border.setTop(comboBox);
        border.setCenter(table);
        tablePane.getChildren().add(border);
    }

    private void setEditTabBenchSidebar(GridPane sidebar) {
        JFXTextField rPathField = new JFXTextField();
        JFXTextField nameField = new JFXTextField();
        JFXTextField professionNameField = new JFXTextField();
        JFXComboBox<String> categoryDropdown = new JFXComboBox<>();
        JFXListView<String> categories = new JFXListView<>();

        rPathField.setPromptText("Relative Path");
        nameField.setPromptText("Name");
        professionNameField.setPromptText("Profession Name (if applicable)");
        categoryDropdown.setPromptText("Category");

        Arrays.asList(rPathField, nameField, professionNameField, categoryDropdown).forEach(item -> item.getStyleClass().add("sidebar-text"));
        categories.getStyleClass().add("sidebar-list");

        sidebar.add(rPathField, 0, 0);
        sidebar.add(nameField, 0, 1);
        sidebar.add(professionNameField, 0, 2);
        sidebar.add(categoryDropdown, 0, 3);
        sidebar.add(categories, 0, 4);

        controller.setEditTabBenchSidebar(rPathField, nameField, professionNameField, categoryDropdown, categories);
    }

    private void setEditTabCollectionSidebar(GridPane sidebar) {
        JFXTextField rPathField = new JFXTextField();
        JFXTextField nameField = new JFXTextField();
        JFXTextField descField = new JFXTextField();
        JFXTextField troveMRField = new JFXTextField();
        JFXTextField geodeMRField = new JFXTextField();
        JFXTextArea notes = new JFXTextArea();
        VBox typesBox = new VBox();
        Text typesLabel = new Text("Collection types");
        ListView<String> types = new ListView<>();
        VBox propertiesBox = new VBox();
        Text propertiesLabel = new Text("Type properties");
        TableView<UIController.KVPair> properties = new TableView<>();
        VBox buffsBox = new VBox();
        Text buffsLabel = new Text("Buffs granted");
        TableView<UIController.KVPair> buffs = new TableView<>();

        TableColumn<UIController.KVPair, String> propCol = new TableColumn<>("Property");
        TableColumn<UIController.KVPair, Double> propValCol = new TableColumn<>("Value");
        TableColumn<UIController.KVPair, String> buffCol = new TableColumn<>("Buff");
        TableColumn<UIController.KVPair, Double> buffValCol = new TableColumn<>("Value");

        typesBox.getChildren().addAll(typesLabel, types);
        propertiesBox.getChildren().addAll(propertiesLabel, properties);
        buffsBox.getChildren().addAll(buffsLabel, buffs);
        properties.getColumns().setAll(Arrays.asList(propCol, propValCol));
        buffs.getColumns().setAll(Arrays.asList(buffCol, buffValCol));

        rPathField.setPromptText("Relative Path");
        nameField.setPromptText("Name");
        descField.setPromptText("Description");
        troveMRField.setPromptText("Trove Mastery");
        geodeMRField.setPromptText("Geode Mastery");
        notes.setPromptText("Notes");
        properties.setEditable(true);
        buffs.setEditable(true);
        propValCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        buffValCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        Arrays.asList(rPathField, nameField, descField, troveMRField, geodeMRField).forEach(item -> item.getStyleClass().add("sidebar-text"));
        Arrays.asList(typesLabel, propertiesLabel, buffsLabel).forEach(item -> item.getStyleClass().add("text-normal"));
        types.getStyleClass().add("sidebar-list-short");
        Arrays.asList(properties, buffs).forEach(item -> item.getStyleClass().add("sidebar-list-medium"));
        types.prefWidthProperty().bind(sidebar.widthProperty().multiply(0.5));
        propCol.prefWidthProperty().bind(properties.widthProperty().multiply(0.8));
        propValCol.prefWidthProperty().bind(properties.widthProperty().multiply(0.16));
        buffCol.prefWidthProperty().bind(buffs.widthProperty().multiply(0.8));
        buffValCol.prefWidthProperty().bind(buffs.widthProperty().multiply(0.16));


        sidebar.add(rPathField, 0, 0, 2, 1);
        sidebar.add(nameField, 0, 1, 2, 1);
        sidebar.add(descField, 0, 2, 2, 1);
        sidebar.add(troveMRField, 0, 3);
        sidebar.add(geodeMRField, 1, 3);
        sidebar.add(typesBox, 0, 4, 2, 1);
        sidebar.add(propertiesBox, 0, 5, 2, 1);
        sidebar.add(buffsBox, 0, 6, 2, 1);
        sidebar.add(notes, 0, 7, 2, 1);

        controller.setEditTabCollectionSidebar(rPathField, nameField, descField, troveMRField, geodeMRField,
                types, properties, propCol, propValCol, buffs, buffCol, buffValCol, notes);
    }

    private void setEditTabItemSidebar(GridPane sidebar) {
        JFXTextField rPathField = new JFXTextField();
        JFXTextField nameField = new JFXTextField();
        JFXTextField descField = new JFXTextField();
        JFXCheckBox tradableBox = new JFXCheckBox("Tradable");
        VBox deconBox = new VBox();
        Text deconLabel = new Text("Deconstructs into");
        TableView<UIController.KVPair> decons = new TableView<>();
        TableColumn<UIController.KVPair, String> deconCol = new TableColumn<>("Article");
        TableColumn<UIController.KVPair, Integer> deconValCol = new TableColumn<>("Qty");
        VBox lootBox = new VBox();
        Text lootLabel = new Text("Lootbox contents (if applicable)");
        JFXComboBox<String> lootComboBox = new JFXComboBox<>();
        TableView<UIController.KVPair> loot = new TableView<>();
        TableColumn<UIController.KVPair, String> lootCol = new TableColumn<>("Object");
        TableColumn<UIController.KVPair, String> lootValCol = new TableColumn<>("Qty");
        JFXTextArea notes = new JFXTextArea();

        deconBox.getChildren().addAll(deconLabel, decons);
        lootBox.getChildren().addAll(lootLabel, lootComboBox, loot);
        decons.getColumns().addAll(Arrays.asList(deconCol, deconValCol));
        loot.getColumns().addAll(Arrays.asList(lootCol, lootValCol));

        rPathField.setPromptText("Relative Path");
        nameField.setPromptText("Name");
        descField.setPromptText("Description");
        notes.setPromptText("Notes");
        decons.setEditable(true);
        loot.setEditable(true);

        Arrays.asList(rPathField, nameField, descField, lootComboBox).forEach(item -> item.getStyleClass().add("sidebar-text"));
        Arrays.asList(deconLabel, lootLabel).forEach(item -> item.getStyleClass().add("text-normal"));
        decons.getStyleClass().add("sidebar-list-medium");
        loot.getStyleClass().add("sidebar-list-long");
        deconCol.prefWidthProperty().bind(decons.widthProperty().multiply(0.8));
        deconValCol.prefWidthProperty().bind(decons.widthProperty().multiply(0.16));
        lootCol.prefWidthProperty().bind(loot.widthProperty().multiply(0.8));
        lootValCol.prefWidthProperty().bind(loot.widthProperty().multiply(0.16));

        sidebar.add(rPathField, 0, 0);
        sidebar.add(nameField, 0, 1);
        sidebar.add(descField, 0, 2);
        sidebar.add(tradableBox, 0, 3);
        sidebar.add(deconBox, 0, 4);
        sidebar.add(lootBox, 0, 5);
        sidebar.add(notes, 0, 6);

        controller.setEditTabItemSidebar(rPathField, nameField, descField, tradableBox, decons, deconCol, deconValCol, lootComboBox, loot, lootCol, lootValCol, notes);
    }

    private void setEditTabPlaceableSidebar(GridPane sidebar) {
        JFXTextField rPathField = new JFXTextField();
        JFXTextField nameField = new JFXTextField();
        JFXTextField descField = new JFXTextField();
        JFXCheckBox tradableBox = new JFXCheckBox("Tradable");
        JFXTextArea notes = new JFXTextArea();

        rPathField.setPromptText("Relative Path");
        nameField.setPromptText("Name");
        descField.setPromptText("Description");
        notes.setPromptText("Notes");

        Arrays.asList(rPathField, nameField, descField).forEach(item -> item.getStyleClass().add("sidebar-text"));

        sidebar.add(rPathField, 0, 0);
        sidebar.add(nameField, 0, 1);
        sidebar.add(descField, 0, 2);
        sidebar.add(tradableBox, 0, 3);
        sidebar.add(notes, 0, 4);

        controller.setEditTabPlaceableSidebar(rPathField, nameField, descField, tradableBox, notes);
    }

    private void setEditTabRecipeSidebar(GridPane sidebar) {
        JFXTextField rPathField = new JFXTextField();
        JFXTextField nameField = new JFXTextField();
        VBox costBox = new VBox();
        Text costLabel = new Text("Costs");
        TableView<UIController.KVPair> costs = new TableView<>();
        VBox outputBox = new VBox();
        Text outputLabel = new Text("Output");
        TableView<UIController.KVPair> output = new TableView<>();
        TableColumn<UIController.KVPair, String> costNameCol = new TableColumn<>("Item");
        TableColumn<UIController.KVPair, Integer> costValCol = new TableColumn<>("Qty");
        TableColumn<UIController.KVPair, String> outputNameCol = new TableColumn<>("Output");
        TableColumn<UIController.KVPair, Integer> outputValCol = new TableColumn<>("Qty");

        costBox.getChildren().addAll(costLabel, costs);
        outputBox.getChildren().addAll(outputLabel, output);
        costs.getColumns().addAll(Arrays.asList(costNameCol, costValCol));
        output.getColumns().addAll(Arrays.asList(outputNameCol, outputValCol));

        rPathField.setPromptText("Relative Path");
        nameField.setPromptText("Name");
        costs.setEditable(true);
        costValCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        outputValCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        Arrays.asList(rPathField, nameField).forEach(item -> item.getStyleClass().add("sidebar-text"));
        Arrays.asList(costLabel, outputLabel).forEach(item -> item.getStyleClass().add("text-normal"));
        costs.getStyleClass().add("sidebar-list-medium");
        output.getStyleClass().add("sidebar-list-short");
        costNameCol.prefWidthProperty().bind(costs.widthProperty().multiply(0.8));
        costValCol.prefWidthProperty().bind(costs.widthProperty().multiply(0.16));
        outputNameCol.prefWidthProperty().bind(output.widthProperty().multiply(0.8));
        outputValCol.prefWidthProperty().bind(output.widthProperty().multiply(0.16));

        sidebar.add(rPathField, 0, 0);
        sidebar.add(nameField, 0, 1);
        sidebar.add(costBox, 0, 2);
        sidebar.add(outputBox, 0, 3);

        controller.setEditTabRecipeSidebar(rPathField, nameField, costs, costNameCol, costValCol, output, outputNameCol, outputValCol);
    }
}
