package ui;

import com.jfoenix.controls.*;
import controllers.UIController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
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
    JFXTreeTableView<UIController.Searchable> articleView;
    JFXTreeView<String> dirView;

    Font sectionFont = Font.font("Roboto Medium", 15);
    Font normalFont = Font.font("Roboto Regular", 12);

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

    Background mainBackground = new Background(new BackgroundFill(Color.rgb(27, 27, 27), CornerRadii.EMPTY, Insets.EMPTY));
    Background buttonBackground = new Background(new BackgroundFill(Color.rgb(238, 238, 238), new CornerRadii(3), Insets.EMPTY));

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
        nav.getChildren().add(vBoxSetup(typeNav, options, "#c7c7c7"));

    }

    void callModify(BorderPane root, VBox nav, VBox mainNav) {

        // update center table
        root.setCenter(modifyDisplaySetUp());

        // remove previous nav
        nav.getChildren().clear();

        // make new nav
        VBox typeNav = new VBox();
        typeNav.setBackground(new Background(new BackgroundFill(Color.rgb(66, 66, 66), CornerRadii.EMPTY, Insets.EMPTY)));

        // buttons
        JFXButton benchBtn = new JFXButton("Modify Benches");
        JFXButton colBtn = new JFXButton("Modify Collections");
        JFXButton itemBtn = new JFXButton("Modify Items");

        // button actions
        benchBtn.setOnAction(event -> root.setCenter(notImplemented()));
        colBtn.setOnAction(event -> root.setCenter(notImplemented()));
        itemBtn.setOnAction(event -> root.setCenter(notImplemented()));

        Button[] options = new Button[]{benchBtn, colBtn, itemBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(vBoxSetup(typeNav, options, "#c7c7c7"));
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
        viewAllBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(root, allArticles, "All Articles")));
        itemBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(root, Collections.singletonList(Parser.ObjectType.ITEM), "Items")));
        colBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(root, Collections.singletonList(Parser.ObjectType.COLLECTION), "Collections")));
        recipeBtn.setOnAction(event -> root.setCenter(setPaneViewFiles(root, Collections.singletonList(Parser.ObjectType.BENCH), "Benches")));
        langBtn.setOnAction(event -> root.setCenter(notImplemented()));

        Button[] options = new Button[]{searchBtn, viewAllBtn, itemBtn, colBtn, recipeBtn, langBtn};

        nav.getChildren().add(mainNav);
        nav.getChildren().add(vBoxSetup(typeNav, options, "#c7c7c7"));

        // update center table
        root.setCenter(setPaneViewFiles(root, allArticles, "All Articles"));
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
        nav.getChildren().add(vBoxSetup(typeNav, options, "#c7c7c7"));
    }

    GridPane setPaneCreateSettings() {

        // clears previous saved paths
        uiCon.clearParseList();

        GridPane grid = new GridPane();
        grid.setBackground(mainBackground);
        grid.setPadding(new Insets(80, 50, 70, 50));
        grid.setHgap(20);

        Text settingsText = new Text("Parse Settings");
        settingsText.setFont(sectionFont);
        settingsText.setFill(Paint.valueOf("#fafafa"));

        Text dirText = new Text("Absolute path of main directory:");
        JFXTextField dirField = new JFXTextField();
        setUpTextField(dirText, dirField, "Current directory: ", dirPath);

        Text benchText = new Text("Filter for bench parsing: (filters out files without the keyword)");
        JFXTextField benchField = new JFXTextField();
        setUpTextField(benchText, benchField, "Current filter: ", benchFilter);

        Text colText = new Text("Filter for collection parsing: ");
        JFXTextField colField = new JFXTextField();
        setUpTextField(colText, colField, "Current filter: ", colFilter);

        Text itemText = new Text("Filter for item parsing: ");
        JFXTextField itemField = new JFXTextField();
        setUpTextField(itemText, itemField, "Current filter: ", itemFilter);

        Text recText = new Text("Filter for recipe parsing: ");
        JFXTextField recField = new JFXTextField();
        setUpTextField(recText, recField, "Current filter: ", recFilter);

        Text langText = new Text("Filter for language file parsing: ");
        JFXTextField langField = new JFXTextField();
        setUpTextField(langText, langField, "Current filter: ", langFilter);

        // TODO: add custom sub-directory path editing

        HBox saveBox = new HBox();
        saveBox.setAlignment(Pos.CENTER_RIGHT);

        JFXButton save = new JFXButton("Save");
        buttonSetup(save, 60);
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

    private void setUpTextField(Text text, JFXTextField textField, String promptText, String defaultText) {
        text.setFont(normalFont);
        text.setFill(Paint.valueOf("#c7c7c7"));

        textField.setPrefWidth(500);
        textField.setFocusColor(Paint.valueOf("#c7c7c7"));
        textField.setPromptText(promptText + defaultText);
    }

    TableView<String> modifyDisplaySetUp() {
        TableView<String> table = new TableView<>();
        table.setBackground(mainBackground);

        return table;

    }

    TableView<String> viewDisplaySetUp() {
        TableView<String> table = new TableView<>();
        table.setBackground(mainBackground);

        return table;

    }

    VBox vBoxSetup(VBox vBox, Button[] options, String buttonTextColor) {
        for (Button item : options) {
            item.setFont(normalFont);
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

    StackPane notImplemented() {

        Text display = new Text("This feature is not yet implemented.");
        display.setFill(Paint.valueOf("#c7c7c7"));
        display.setFont(normalFont);
        StackPane pane = new StackPane();
        pane.setBackground(mainBackground);
        pane.getChildren().add(display);

        return pane;
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

    GridPane setPaneViewFiles(BorderPane root, List<Parser.ObjectType> types, String headerText) {

        // set grid
        GridPane grid = new GridPane();
        grid.setBackground(mainBackground);
        grid.setPadding(new Insets(80, 50, 70, 50));
        grid.setHgap(20);

        // set up StackPane to hold dialog box and TableView
        StackPane tablePane = new StackPane();

        // set dialog
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(tablePane, dialogLayout, JFXDialog.DialogTransition.CENTER);

        // set table
        TableView<UIController.Searchable> table = new TableView<>();
        AtomicReference<ObservableList<UIController.Searchable>> articles = new AtomicReference<>(uiCon.getSearchableList(types, ""));

        table.setStyle("-fx-box-border: #1B1B1B;");
        table.prefWidthProperty().bind(root.widthProperty().multiply(0.7));
        table.prefHeightProperty().bind(root.heightProperty().multiply(0.6));
        table.getStyleClass().add("table-view");

        TableColumn<UIController.Searchable, String> nameCol = new TableColumn<>("Name");
        TableColumn<UIController.Searchable, String> rPathCol = new TableColumn<>("Relative path");
        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
//        nameCol.setResizable(false);
        rPathCol.prefWidthProperty().bind(table.widthProperty().multiply(0.5999));
//        rPathCol.setResizable(false);

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        rPathCol.setCellValueFactory(new PropertyValueFactory<>("rPath"));

        table.setRowFactory(view -> {
            TableRow<UIController.Searchable> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    TablePosition pos = table.getSelectionModel().getSelectedCells().get(0);
                    int selectedRow = pos.getRow();

                    UIController.Searchable article = table.getItems().get(selectedRow);

                    VBox content;
                    if (article.getRPath().contains("item")) {
                        content = uiCon.getItemContent(article.getRPath());
                    } else if (article.getRPath().contains("collection")) {
                        content = uiCon.getCollectionContent(article.getRPath());
                    } else {
                        content = uiCon.getBenchContent(article.getRPath());
                    }


                    JFXButton button = new JFXButton("Close");
                    buttonSetup(button, 60);
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
        table.getColumns().setAll(nameCol, rPathCol);

        tablePane.getChildren().add(table);
        dialog.setDialogContainer(tablePane);

        // set header + search bar
        HBox headerBox = new HBox();
        headerBox.setBackground(mainBackground);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text header = new Text(headerText);
        header.setFont(sectionFont);
        header.setFill(Paint.valueOf("#fafafa"));

        JFXTextField searchField = new JFXTextField();
        searchField.setPromptText("Search by name");
        searchField.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
        searchField.setFocusColor(Paint.valueOf("#c7c7c7"));

        JFXButton searchButton =  new JFXButton();
        searchButton.setText("Go");
        searchButton.setPrefWidth(35);
        searchButton.setPrefHeight(30);
        searchButton.setAlignment(Pos.CENTER);
        searchButton.setBackground(mainBackground);
        searchButton.setTextFill(Paint.valueOf("#424242"));

        searchButton.setOnAction(event -> {

            articles.set(uiCon.getSearchableList(types, searchField.getText()));
            table.setItems(articles.get());
            table.refresh();
        });

        headerBox.getChildren().addAll(Arrays.asList(header, spacer, searchField, searchButton));



        // add items to grid
        grid.add(headerBox, 0, 0);
        grid.add(tablePane, 0, 1);

        GridPane.setMargin(headerBox, new Insets(0, 0, 10, 0));

        return grid;
    }

    GridPane setPaneCreateDirectory(BorderPane root, String subPath, String filter, Parser.ObjectType type) {

        StackPane currPane = new StackPane();
        currPane.setBackground(mainBackground);

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
        Text headerText = new Text("Parse " + type + plural);
        headerText.setFont(sectionFont);
        headerText.setFill(Paint.valueOf("#fafafa"));

        // create progress bar and status text
        JFXProgressBar progressBar = new JFXProgressBar();
        progressBar.getStyleClass().add("jfx-progress-bar");
        progressBar.prefWidthProperty().bind(root.widthProperty().multiply(0.6));
        progressBar.setProgress(0);

        final Text progressText = new Text("Status");
        progressText.setFont(Font.font("Roboto Medium", 11));
        progressText.setFill(Paint.valueOf("#C7C7C7"));

        VBox progressBox = new VBox();
        progressBox.setAlignment(Pos.BOTTOM_LEFT);
        progressBox.getChildren().add(progressText);
        progressBox.getChildren().add(progressBar);
        progressBox.prefHeightProperty().bind(root.heightProperty().multiply(0.005));

        // create button to start parsing - needs to show dialog box when there are failed files
        JFXButton startParse = new JFXButton("Parse");
        buttonSetup(startParse, 60);
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

                        JFXButton button = new JFXButton("Close");
                        buttonSetup(button, 60);
                        button.setOnAction(event1 -> dialog.close());

                        dialogLayout.setHeading(new Text("Incomplete parsing"));
                        dialogLayout.setBody(uiCon.getFailedContent());
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
//        grid.setGridLinesVisible(true); // debug
        grid.setBackground(mainBackground);
        grid.setPadding(new Insets(80, 50, 20, 50));

        grid.add(headerText, 0, 0);
        grid.add(currPane, 0, 1);
        grid.add(buttonBox, 0, 2);
        grid.add(progressBox, 0, 3);

        GridPane.setMargin(currPane, new Insets(30, 30, 30, 0));
        GridPane.setMargin(progressBox, new Insets(35, 30, 10, 0));

        return grid;
    }

    void buttonSetup(JFXButton button, int buttonWidth) {
        button.setPrefWidth(buttonWidth);
        button.setPrefHeight(35);
        button.setAlignment(Pos.CENTER);
        button.setBackground(buttonBackground);

    }




}
