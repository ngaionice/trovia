package unused.model.gateways;

import datamodel.objects.LangFile;
import unused.model.objects.*;

import java.util.Map;

public interface DatabaseGateway {

    void exportBenches(Map<String, Bench> benches);

    void exportCollections(Map<String, Collection> collections);

    void exportItems(Map<String, Item> items);

    void exportLangFile(Map<String, LangFile> files);

    void exportRecipes(Map<String, Recipe> recipes);
}
