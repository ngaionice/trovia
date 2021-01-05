package controllers;

import managers.*;
import objects.*;
import objects.Collection;
import parser.Parser;
import parser.parsestrategies.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LogicController {

    Scanner sc = new Scanner(System.in);
    BenchManager benchM = new BenchManager();
    CollectionManager colM = new CollectionManager();
    ItemManager itemM = new ItemManager();
    LanguageManager langM = new LanguageManager();
    RecipeManager recM = new RecipeManager();
    Parser p = new Parser();
    TextPresenter pr = new TextPresenter();

    // create objects:

    // create gear listings
    // create upgrade trees eventually?

    // modify objects:

    // add/change mastery to items (batch and individual)
    // add lootbox stuff to items
    // add decon stuff to items

    // HELPER?

    /**
     * Returns the name of the object referred to by the input relative path.
     *
     * @param rPath relative path of the object to be searched for
     * @return the name of the object, obtained from a LangFile
     */
    private String getName(String rPath) {
        List<SearchManager> searchables = Arrays.asList(benchM, colM, itemM);
        for (SearchManager manager: searchables) {
            if (manager.getName(rPath) != null) {
                return langM.getString(manager.getName(rPath));
            }
        }
        pr.searchFailure(rPath);
        return null;
    }

    // PARSING

    void createObject(String absPath, Parser.ObjectType type) throws IOException, ParseException {
        switch (type) {
            case ITEM:
                itemM.addItem((Item) p.createObject(absPath, type));
                pr.createSuccess(absPath, type);
                break;
            case BENCH:
            case PROFESSION:
                benchM.addBench((Bench) p.createObject(absPath, type));
                pr.createSuccess(absPath, type);
                break;
            case RECIPE:
                recM.addRecipe((Recipe) p.createObject(absPath, type));
                pr.createSuccess(absPath, type);
                break;
            case COLLECTION:
                colM.addCollection((Collection) p.createObject(absPath, type));
                pr.createSuccess(absPath, type);
                break;
            case LANG_FILE:
                langM.addLangFile((LangFile) p.createObject(absPath, type));
                pr.createSuccess(absPath, type);
        }
    }

    // MODIFY

    private void matchBenchRecipes(String rPath) {
        List<String> recipes = benchM.getAllRecipes(rPath);
        String benchName = benchM.getName(rPath);
        for (String recipe: recipes) {
            recM.setBench("recipes/" + recipe, benchName);
        }
        // TODO: log this process somewhere
    }

    /**
     * Add notes to an Item or a Collection. Also adds the note to language file "languages/en/prefabs_notes".
     *
     * @param rPath relative path of the item
     */
    private void addNotes(String rPath) {
        String notesLangFile = "languages/en/prefabs_notes";

        // format: $prefab_item_aura_music_01_1
        String key = "$prefab_" + rPath.replaceAll("/", "_") + "_" + langM.getLangFileLength(notesLangFile);
        String value = getInput();
        langM.addString(notesLangFile, key, value);
        if (rPath.contains("item")) {
            itemM.addNotes(rPath, key);
        } else {
            colM.addNotes(rPath, key);
        }
    }

    /**
     * Match all newly-added Recipes to their respective Items and Collections.
     */
    private void matchNewRecipes() {
        boolean allMatched = true;
        List<String> failed = new ArrayList<>();
        for (String rPath: recM.getNewRPaths()) {
            String outputRPath = recM.getOutput(rPath)[0];

            // consider switching to a switch statement when placeables get implemented too
            if (outputRPath.contains("item")) {
                if (itemM.getName(outputRPath) == null) {
                    allMatched = false;
                    failed.add(outputRPath);
                } else {
                    itemM.addRecipe(outputRPath, rPath);
                }
            } else {
                if (colM.getName(outputRPath) == null) {
                    allMatched = false;
                    failed.add(outputRPath);
                }
                colM.addRecipe(outputRPath, rPath);
            }
        }
        if (allMatched) {
            pr.matchRecipeSuccess();
        } else {
            pr.matchRecipeFailure(failed);
            // TODO: log these rPaths somewhere
        }
    }

    // FILE MANAGEMENT

    /**
     * Returns all Files in a directory given by the input absolute path. Returns null if the input path is null.
     *
     * @param absPath absolute path of the directory
     * @return  array of File
     */
    File[] getFiles(String absPath) {
        if (absPath == null) {
            return null;
        }

        // since presenter checks that the input path is a directory, we can assume that here
        File dir = new File(absPath);
        return dir.listFiles();
    }

    /**
     * Returns a list of Strings, which is a list of path names from the input File array.
     *
     * @param files array of File
     * @return list of Strings converted from the array
     */
    List<String> getPaths(File[] files) {
        return Arrays.stream(files).map(File::getPath).collect(Collectors.toList());
    }

    /**
     * Returns a list of Strings, where the Strings are from the input list and contain the input filter string.
     *
     * @param paths   list of Strings
     * @param filter  the string to filter by
     * @return        list of Strings, keeping only the strings from the input list containing the filter string
     */
    List<String> filterOutWithout(List<String> paths, String filter) {
        List<String> newList = new ArrayList<>();
        for (String item: paths) {
            if (item.contains(filter)) {
                newList.add(item);
            }
        }
        return newList;
    }

    /**
     * Returns a list of string arrays. Each string array contains the name and relative path of the Article (in that order).
     * Article types included are specified by the input list of SearchManagers.
     *
     * @param artTypes list of Article types in strings
     * @return         list of string arrays
     */
     List<String[]> getNameAndRPathList(List<Parser.ObjectType> artTypes) {

        // the ArrayList that will hold the entries
        List<SearchManager> managers = new ArrayList<>();

        // add the managers selected
        if (artTypes.contains(Parser.ObjectType.BENCH)) {
            managers.add(benchM);
        }

        if (artTypes.contains(Parser.ObjectType.COLLECTION)) {
            managers.add(colM);
        }

        if (artTypes.contains(Parser.ObjectType.ITEM)) {
            managers.add(itemM);
        }

        // add the entries to the ArrayList
        List<String[]> entryList = new ArrayList<>();
        for (SearchManager item: managers) {
            if (item != null) {
                List<String[]> list = item.getAllNamesAndRPaths();
                if (list != null) {
                    entryList.addAll(list);
                }
            }
        }

        // convert the string identifiers to their actual strings
        for (String[] entry: entryList) {
            if (entry.length != 0) {
                if (langM.getString(entry[0]) != null) {
                    entry[0] = langM.getString(entry[0]);
                } else {
                    entry[0] = "Name not available";
                }

            }
        }

        return entryList;
    }

    // GENERAL

    /**
     * Prints out all the options that a user has, then prompts the user to select an option.
     * Only returns when the user has selected a valid option.
     *
     * @param options options for the users
     * @return user's choice as an integer
     */
    private int selectOption(List<String> options) {
        System.out.println("Select an option:");
        for (int i = 1; i < (options.size() + 1); i++) {
            System.out.println(i + " - " + options.get(i - 1));
        }
        System.out.println("-1" + " - " + "Return to the previous page.");

        String newInput = sc.nextLine();
        String intRegex = "(-1)|[1-9]+";
        while (!newInput.matches(intRegex) || -1 > Integer.parseInt(newInput) || options.size() < Integer.parseInt(newInput)) {
            System.out.println("Please select a valid option.");
            newInput = sc.nextLine();
        }
        return Integer.parseInt(newInput);
    }

    /**
     * Get string input from user.
     *
     * @return the string inputted by the user
     */
    private String getInput() {
        pr.promptInput("new note");
        boolean confirmed = false;
        String text;
        do {
            text = sc.nextLine();
            pr.confirmInput(text);
            String confirm = sc.nextLine();
            if (confirm.equals("y")) {
                confirmed = true;
            }
        } while (!confirmed);
        return text;
    }

    // RELAYING MODULES

    String getItemDesc(String rPath) {
        return langM.getString(itemM.getDesc(rPath).toLowerCase());
    }

    String getItemDescIdentifier(String rPath) {
        return itemM.getDesc(rPath);
    }

    String getCollectionDesc(String rPath) {
        return langM.getString(colM.getDesc(rPath).toLowerCase());
    }

    String getCollectionDescIdentifier(String rPath) {
        return colM.getDesc(rPath);
    }

    List<String> getBenchRecipes(String rPath) {
        return benchM.getAllRecipes(rPath);
    }
}
