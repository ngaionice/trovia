package p.trovia.objects;

import java.util.List;

public class LangFile implements Article{

    // language file, can contain multiple

    String name; // file name (not the path!); e.g. prefabs_collections_aura
    List<String[]> strings; // list of string arrays, each array has the format of [path, actual string]
    // e.g. [$prefabs_item_aura_music_01_item_description, A fitting ode to the chaos of battle.]

    public LangFile(String name, List<String[]> strings) {
        this.name = name;
        this.strings = strings;
    }

    public String getName() {
        return name;
    }

    public List<String[]> getStrings() {
        return strings;
    }

}
