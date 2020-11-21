import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        parser.convertDirectory("C:\\Users\\Julian\\Desktop\\recipes");
//        parser.factory("C:\\Users\\Julian\\Desktop\\recipes\\recipe_crafting_tome_ancientgears_legendary.binfab");
    }
}
