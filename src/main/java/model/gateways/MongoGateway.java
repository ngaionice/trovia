package model.gateways;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import datamodel.objects.LangFile;
import model.objects.*;
import org.bson.Document;
import local.Variables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.List;


public class MongoGateway implements DatabaseGateway{

    // Gateway for interaction with MongoDB

    Variables variables = new Variables();
    MongoClient mongoClient;

    MongoDatabase database;
    MongoCollection<Document> collection;

    public MongoGateway() {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        mongoClient = MongoClients.create(variables.troviaUri);
        database = mongoClient.getDatabase("test");
        collection = database.getCollection("items");
    }

    public void setCollection(String collection) {
        this.collection = database.getCollection(collection);
    }

    /**
     * Inserts the input map of recipes into the database. If the recipe already exists on the database, then it gets replaced by the new version.
     *
     * @param recipes map of recipes, with relative paths as keys and Recipe objects as values
     */
    public void exportRecipes(Map<String, Recipe> recipes) {
        for (String rPath: recipes.keySet()) {
            Recipe item = recipes.get(rPath);

            String name = item.getName();
            String bench = item.getBench();
            String[][] costs = item.getCosts();
            String[] output = item.getOutput();

            Document doc = new Document("name", name).append("bench", bench).append("rPath", rPath);
            Document costsDoc = new Document();
            for (String[] cost: costs) {
                costsDoc.append(cost[0], cost[1]);
            }
            doc.append("costs", costsDoc);
            doc.append("output", new Document(output[0], output[1]));
            collection.replaceOne(Filters.eq("rPath", rPath), doc, new ReplaceOptions().upsert(true));
        }
    }

    public void exportItems(Map<String, Item> items) {
       for (String rPath: items.keySet()) {
            Item item = items.get(rPath);

            String name = item.getName();
            String desc = item.getDesc();
            List<Boolean> prop = Arrays.asList(item.isUnlocker(), item.isLootbox(), item.isDecon(), item.isCraftable());
            String[] unlocks = item.getUnlocks();
            Map<String, Integer> decons = item.getDecons();
            Map<String, Map<String, String>> loot = item.getLootbox();
            List<String> recipes = item.getRecipes();
            List<String> notes = item.getNotes();

            Document doc = new Document("name", name).append("desc", desc).append("rPath", rPath);

            if (notes != null && !notes.isEmpty()) {
                doc.append("notes", notes);
            }

            // if is unlocker
            if (prop.get(0)) {
                doc.append("unlocks", Arrays.asList(unlocks));
            }

            // if is lootbox
            if (prop.get(1)) {
                Document commonDoc = new Document();
                if (loot.get("common") != null && !loot.get("common").isEmpty()) {
                    for (String key: loot.get("common").keySet()) {
                        commonDoc.append(key, loot.get("common").get(key));
                    }
                }

                Document uncommonDoc = new Document();
                if (loot.get("uncommon") != null && !loot.get("uncommon").isEmpty()) {
                    for (String key: loot.get("uncommon").keySet()) {
                        uncommonDoc.append(key, loot.get("uncommon").get(key));
                    }
                }

                Document rareDoc = new Document();
                if (loot.get("rare") != null && !loot.get("rare").isEmpty()) {
                    for (String key: loot.get("rare").keySet()) {
                        rareDoc.append(key, loot.get("rare").get(key));
                    }
                }
                doc.append("common", commonDoc).append("uncommon", uncommonDoc).append("rare", rareDoc);
            }

            // if is decon
            if (prop.get(2)) {
                Document deconDoc = new Document();
                for (String key: decons.keySet()) {
                    deconDoc.append(key, decons.get(key));
                }
                doc.append("decons", deconDoc);
            }

            // if is craftable
            if (prop.get(3)) {
                doc.append("recipes", recipes);
            }
            collection.replaceOne(Filters.eq("rPath", rPath), doc, new ReplaceOptions().upsert(true));
        }
    }

    public void exportCollections(Map<String, Collection> collections) {
        for (String rPath: collections.keySet()) {
            Collection item = collections.get(rPath);

            String name = item.getName();
            String desc = item.getDesc();
            int troveMR = item.getTroveMR();
            int geodeMR = item.getGeodeMR();
            List<CollectionEnums.Type> types = item.getTypes();
            Map<CollectionEnums.Property, Double> properties = item.getProperties();
            Map<CollectionEnums.Buff, Double> buffs = item.getBuffs();
            List<String> recipes = item.getRecipes();
            List<String> notes = item.getNotes();

            Document doc = new Document("name", name).append("desc", desc).append("rPath", rPath)
                    .append("troveMR", troveMR).append("geodeMR", geodeMR);

            List<String> typeString = new ArrayList<>();
            for (CollectionEnums.Type type: types) {
                typeString.add(String.valueOf(type));
            }

            if (!types.isEmpty()) {
                doc.append("types", typeString);
            }

            if (!properties.isEmpty()) {
                Document propDoc = new Document();
                for (CollectionEnums.Property key: properties.keySet()) {
                    propDoc.append(String.valueOf(key), properties.get(key));
                }
                doc.append("properties", propDoc);
            }

            if (buffs != null && !buffs.isEmpty()) {
                Document buffDoc = new Document();
                for (CollectionEnums.Buff key: buffs.keySet()) {
                    buffDoc.append(String.valueOf(key), buffs.get(key));
                }
                doc.append("buffs", buffDoc);
            }

            if (recipes != null && !recipes.isEmpty()) {
                doc.append("recipes", recipes);
            }

            if (notes != null && !notes.isEmpty()) {
                doc.append("notes", notes);
            }
            collection.replaceOne(Filters.eq("rPath", rPath), doc, new ReplaceOptions().upsert(true));
        }
    }

    public void exportBenches(Map<String, Bench> benches) {
        for (String rPath: benches.keySet()) {
            Bench item = benches.get(rPath);

            String name = item.getName();
            boolean profession = item.isProfession();
            String professionName = item.getProfessionName();
            Map<String[], List<String>> categories = item.getAllRecipesByCategory();

            Document doc = new Document("name", name).append("profession", profession).append("rPath", rPath);

            if (professionName != null) {
                doc.append("professionName", professionName);
            }

            Document catDoc = new Document();
            for (String[] category: categories.keySet()) {
                System.out.println(category[0]);
                catDoc.append(category[0].replace("$", "%"), categories.get(category));
            }
            doc.append("categories", catDoc);

            collection.replaceOne(Filters.eq("rPath", rPath), doc, new ReplaceOptions().upsert(true));
        }
    }

    public void exportLangFile(Map<String, LangFile> files) {
        for (String rPath: files.keySet()) {
            LangFile item = files.get(rPath);

            String name = item.getName();
            Map<String, String> strings = item.getStrings();

            Document doc = new Document("name", name).append("rPath", rPath);
            Document stringDoc = new Document();
            for (String identifier: strings.keySet()) {
                stringDoc.append(identifier.replace("$", "%"), strings.get(identifier));
            }

            doc.append("strings", stringDoc);
            collection.replaceOne(Filters.eq("rPath", rPath), doc, new ReplaceOptions().upsert(true));
        }
    }
}
