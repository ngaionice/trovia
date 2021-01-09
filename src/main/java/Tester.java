import model.ModelController;
import model.gateways.MongoGateway;
import model.objects.CollectionEnums;

public class Tester {

    public static void main(String[] args) {

        ModelController con = new ModelController();
        con.importDataLocal();

        // DB testing
        MongoGateway db = new MongoGateway();

//        db.setCollection("items");
//        db.exportItems(con.getAllItems());
//
//        db.setCollection("recipes");
//        db.exportRecipes(con.getAllRecipes());

//        db.setCollection("collections");
//        db.exportCollections(con.getAllCollections());


    }
}
