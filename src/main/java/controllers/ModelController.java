package controllers;

import gateways.SerGateway;
import managers.*;
import objects.*;
import objects.Collection;
import parser.Parser;
import parser.parsestrategies.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ModelController {

    BenchManager benchM;
    CollectionManager colM;
    ItemManager itemM;
    LanguageManager langM;
    RecipeManager recM;
    Parser p = new Parser();
    TextPresenter pr = new TextPresenter();
    SerGateway gateway = new SerGateway();

    // HELPER?

    // PARSING

    public String createObject(String absPath, Parser.ObjectType type) throws IOException {
        switch (type) {
            case ITEM:
                try {
                    itemM.addItem((Item) p.createObject(absPath, type));
                    pr.createSuccess(absPath, type);
                    return null;
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                    return absPath;
                }
            case BENCH:
            case PROFESSION:
                try {
                    benchM.addBench((Bench) p.createObject(absPath, type));
                    pr.createSuccess(absPath, type);
                    return null;
                } catch(ParseException e) {
                    System.out.println(e.getMessage());
                    return absPath;
                }
            case RECIPE:
                try {
                    recM.addRecipe((Recipe) p.createObject(absPath, type));
                    pr.createSuccess(absPath, type);
                    return null;
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                    return absPath;
                }
            case COLLECTION:
                try {
                    colM.addCollection((Collection) p.createObject(absPath, type));
                    pr.createSuccess(absPath, type);
                    return null;
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                    return absPath;
                }
            case LANG_FILE:
                try {
                    langM.addLangFile((LangFile) p.createObject(absPath, type));
                    pr.createSuccess(absPath, type);
                    return null;
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                    return absPath;
                }
            default: return absPath;
        }
    }

    // SETTERS - BENCH & RECIPE

    public void setBenchName(String rPath, String identifier) {
        benchM.setName(rPath, identifier);
    }

    public List<String> matchBenchToRecipes(String rPath) {
        List<String> recipes = benchM.getAllRecipes(rPath);
        List<String> unmatched = new ArrayList<>();
        String benchName = benchM.getName(rPath);
        for (String recipe: recipes) {
            if (recM.getName("recipes/" + recipe.toLowerCase()) != null) {
                recM.setBench("recipes/" + recipe.toLowerCase(), benchName);
            } else {
                unmatched.add(recipe);
            }
        }
        return unmatched;
    }

    /**
     * Match all newly-added Recipes to their respective Items and Collections. Returns a list of failed recipes, or null if all recipes were matched.
     */
    public List<String> matchNewRecipes() {
        boolean allMatched = true;
        List<String> failed = new ArrayList<>();
        for (String rPath: recM.getNewRPaths()) {
            String outputRPath = recM.getOutput(rPath)[0];
            System.out.println("Target rPath:" + outputRPath);

            // consider switching to a switch statement when placeables get implemented too
            if (outputRPath.contains("item")) {
                if (itemM.getName(outputRPath) == null) {
                    allMatched = false;
                    failed.add(outputRPath);
                } else {
                    itemM.addRecipe(outputRPath, rPath);
                }
            } else if (outputRPath.contains("collection")){
                if (colM.getName(outputRPath) == null) {
                    allMatched = false;
                    failed.add(outputRPath);
                }
                colM.addRecipe(outputRPath, rPath);
            } // currently ignores placeables
            else {
                System.out.println("Ignored " + outputRPath);
            }
        }
        if (allMatched) {
            return null;
        } else {
            return failed;
        }
    }

    // SETTERS - ITEM & COLLECTION

    /**
     * Add notes to an Item or a Collection. Also adds the note to language file "languages/en/prefabs_notes".
     *
     * @param rPath relative path of the item
     */
    public void addNotes(String rPath, String value) {
        String notesLangFile = "languages/en/prefabs_notes";

        // format: $prefab_item_aura_music_01_1
        String key = "$prefab_" + rPath.replaceAll("/", "_") + "_" + langM.getLangFileLength(notesLangFile);
        langM.addString(notesLangFile, key, value);
        if (rPath.contains("item")) {
            itemM.addNotes(rPath, key);
        } else {
            colM.addNotes(rPath, key);
        }
    }

    public void addLootboxCommon(String rPath, List<String[]> loot) {
        itemM.addLootBoxCommon(rPath, loot);
    }

    public void addLootboxUncommon(String rPath, List<String[]> loot) {
        itemM.addLootBoxUncommon(rPath, loot);
    }

    public void addLootboxRare(String rPath, List<String[]> loot) {
        itemM.addLootBoxRare(rPath, loot);
    }

    public void addDeconContent(String rPath, List<String[]> loot) {
        Map<String, Integer> map = new HashMap<>(5);
        for (String[] item: loot) {
            map.put(item[0], Integer.parseInt(item[1]));
        }
        itemM.setDecon(rPath, map);
    }

    public void setTroveMastery(String rPath, int mastery) {
        colM.setTroveMR(rPath, mastery);
    }

    public void setGeodeMastery(String rPath, int mastery) {
        colM.setGeodeMR(rPath, mastery);
    }

    // GETTERS - GENERAL

    /**
     * Returns the name of the object referred to by the input relative path. Returns null if not found.
     *
     * @param rPath relative path of the object to be searched for
     * @return the name of the object, obtained from a LangFile
     */
    public String getName(String rPath) {
        List<SearchManager> searchables = Arrays.asList(benchM, colM, itemM);
        for (SearchManager manager: searchables) {
            if (manager.getName(rPath) != null) {
                return langM.getString(manager.getName(rPath));
            }
        }
        return null;
    }

    public String getString(String identifier) {
        String string = langM.getString(identifier);
        if (string != null && !string.equals("")) {
            return string;
        }
        return "Not available.";
    }

    /**
     * Returns all file paths in the input directory that contains the input filter.
     *
     * @param absPath   absolute path of the directory
     * @param filter    the string to filter by
     * @return          list of paths containing the filter
     */
    public List<String> getPathsWithFilter(String absPath, String filter) {
        if (absPath == null) {
            return null;
        }

        File dir = new File(absPath);   // since presenter checks that the input path is a directory, we can assume that here
        List<String> paths = Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .map(File::getPath)
                .collect(Collectors.toList());
        return paths.stream().filter(value -> value.contains(filter)).collect(Collectors.toList());
    }

    /**
     * Returns a list of string arrays. Each string array contains the name and relative path of the Article (in that order).
     * Article types included are specified by the input list of SearchManagers.
     *
     * @param artTypes list of Article types in strings
     * @return         list of string arrays
     */
    public List<String[]> getNameAndRPathList(List<Parser.ObjectType> artTypes) {

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

    // GETTERS - ITEM

    public String getItemDesc(String rPath) {
        if (itemM.getDesc(rPath) != null) {
            String desc = langM.getString(itemM.getDesc(rPath).toLowerCase());
            if (desc != null) {
                return desc;
            }
        }
        return "Not available.";
    }

    public String getItemDescIdentifier(String rPath) {
        if (itemM.getDesc(rPath) != null && !itemM.getDesc(rPath).equals("")) {
            return itemM.getDesc(rPath);
        }
        return "Not available.";
    }

    public List<String> getItemRecipes(String rPath) {
        return itemM.getRecipe(rPath);
    }

    public List<String> getItemNotes(String rPath) {
        return itemM.getNotes(rPath);
    }

    public Map<String, Integer> getDecons(String rPath) {
        return itemM.getDecons(rPath);
    }

    public Map<String, Map<String, String>> getLootbox(String rPath) {
        return itemM.getLootbox(rPath);
    }

    // GETTERS - COLLECTION

    public String getCollectionDesc(String rPath) {
        if (colM.getDesc(rPath) != null) {
            String desc = langM.getString(colM.getDesc(rPath).toLowerCase());
            if (desc != null) {
                return desc;
            }
        }
        return "Not available.";
    }

    public String getCollectionDescIdentifier(String rPath) {
        if (colM.getDesc(rPath) != null && !colM.getDesc(rPath).equals("")) {
            return colM.getDesc(rPath);
        }
        return "Not available.";
    }

    public Map<CollectionEnums.Property, Double> getCollectionProperties(String rPath) {
        return colM.getProperties(rPath);
    }

    public Map<CollectionEnums.Buff, Double> getCollectionBuffs(String rPath) {
        return colM.getBuffs(rPath);
    }

    public Integer[] getCollectionMastery(String rPath) {
        return colM.getMastery(rPath);
    }

    public List<String> getCollectionRecipes(String rPath) {
        return colM.getRecipe(rPath);
    }

    public List<String> getCollectionNotes(String rPath) {
        return colM.getNotes(rPath);
    }

    // GETTERS - BENCH

    public List<String> getBenchRecipes(String rPath) {
        return benchM.getAllRecipes(rPath);
    }

    // GETTERS - LANG FILE

    public Map<String, String> getAllStringsFromFile(String rPath) {
        return langM.getAllFileStrings(rPath);
    }

    // FILE STORAGE

    void exportDataLocal() {
        gateway.exportManager("bench.ser", benchM);
        gateway.exportManager("collection.ser", colM);
        gateway.exportManager("item.ser", itemM);
        gateway.exportManager("language.ser", langM);
        gateway.exportManager("recipe.ser", recM);
    }

    void importDataLocal() {
        benchM = (BenchManager) gateway.importManager("bench.ser");
        colM = (CollectionManager) gateway.importManager("collection.ser");
        itemM = (ItemManager) gateway.importManager("item.ser");
        langM = (LanguageManager) gateway.importManager("language.ser");
        recM = (RecipeManager) gateway.importManager("recipe.ser");
    }








}
