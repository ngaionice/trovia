import com.mongodb.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Projections.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;


public class Exporter {
    Variables variables = new Variables();
    MongoClient mongoClient = MongoClients.create(variables.uriString);

    MongoDatabase database = mongoClient.getDatabase("recipe_test");
    MongoCollection<Document> collection = database.getCollection("recipes");

    public void testReadFunctionality() {
        try {
            Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
            for (String name: database.listCollectionNames()) {
                System.out.println(name);
            }
        } catch(Exception e) {
            System.out.println("Database failed to connect.");
        }
     }

    public void testCreateCollection() {
        testReadFunctionality();
    }

    /**
     * Takes in the output from Workflows.createRecipes, and insert it into the MongoDB database.
     *
     * @param recipes output from Workflows.createRecipes.
     */
    public void importRecipes(List<List<String[]>> recipes) {
        System.out.println("Starting recipe import.");
        for (List<String[]> recipe: recipes) {

            // appends the path of the name
            Document recipeDoc = new Document("path_name", recipe.get(0)[0]);

            // appends the path of the crafting station
            recipeDoc.append("path_bench", recipe.get(1)[0]);

            // appends everything else
            for (int i = 2; i < recipe.size(); i++) {
                String[] currItem = recipe.get(i);
                recipeDoc.append(currItem[0], currItem[1]);
            }

            // insert the document
            collection.insertOne(recipeDoc);
        }
    }

}
