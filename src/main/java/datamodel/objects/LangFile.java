package datamodel.objects;

import java.util.Map;

public class LangFile implements Article {

    // language file, can contain multiple

    private final String name; // file name (not the path!); e.g. prefabs_collections_aura
    private final String rPath;   // relative path of this file
    private final Map<String, String> strings; // map of strings, each entry has the format of key: $string_key; value: string
    // e.g. key: $prefabs_item_aura_music_01_item_description; value: A fitting ode to the chaos of battle.

    public LangFile(String name, String rPath, Map<String, String> strings) {
        this.name = name;
        this.rPath = rPath;
        this.strings = strings;
    }

    public String getName() {
        return name;
    }

    public String getRPath() {
        return rPath;
    }

    public Map<String, String> getStrings() {
        return strings;
    }

}
