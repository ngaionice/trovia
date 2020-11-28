import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.MongoCredential;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;

public class MongoHandler {

    MongoClient mongoClient = new MongoClient("localhost", 27017);

    MongoDatabase database = mongoClient.getDatabase("myMongoDb");


}
