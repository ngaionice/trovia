package p.trovia.objects;

import java.util.List;

public class LangFile implements Article{

    // language file, can contain multiple

    String name; // file name (not the path!); e.g. prefabs_collections_aura
    String rPath;   // relative path of this file
    List<String[]> strings; // list of string arrays, each array has the format of [path, actual string]
    // e.g. [$prefabs_item_aura_music_01_item_description, A fitting ode to the chaos of battle.]

    public LangFile(String name, String rPath, List<String[]> strings) {
        this.name = name;
        this.rPath = rPath;
        this.strings = strings;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getRPath() {
        return rPath;
    }

    public List<String[]> getStrings() {
        return strings;
    }

}
