package gateways;

import managers.*;

import java.io.*;

public class LocalGatway {

    // Gateway for Serialization

    public Manager importManager(String path) {
        try {
            // read the object from file
            InputStream file = new FileInputStream(path); // String path should be "fileName.ser"
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);
            // method to deserialize object
            Manager manager = (Manager) input.readObject();
            input.close();
            System.out.println("Manager imported.");
            return manager;
        } catch (IOException | ClassNotFoundException i) {
            // i.printStackTrace();
            System.out.println("An existing Manager could not be read/found, returning a new Manager.");
            if (path.contains("bench")) {
                return new BenchManager();
            } else if (path.contains("collection")) {
                return new CollectionManager();
            } else if (path.contains("item")) {
                return new ItemManager();
            } else if (path.contains("lang")) {
                return new LanguageManager();
            } else {
                return new RecipeManager();
            }
        }
    }

    /**
     * Serializes the input Manager to the specified file path.
     *
     * @param path     the file path of the to-be serialized Manager
     * @param manager the Manager to be serialized
     */
    public void exportManager(String path, Manager manager) {
        try {
            // save the object to file
            OutputStream file = new FileOutputStream(path);
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);
            // method to serialize object
            output.writeObject(manager);
            output.close();
            System.out.println("Manager has been serialized to " + path + ".");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

}

