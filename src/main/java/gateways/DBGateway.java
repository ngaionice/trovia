package gateways;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import local.Variables;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.List;


public class DBGateway {

    // Gateway for interaction with MongoDB

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
     * OUTDATED
     * Takes in the output from createRecipes, and insert it into the MongoDB database.
     *
     * @param recipes output from createRecipes.
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
