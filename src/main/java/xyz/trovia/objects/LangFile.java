package xyz.trovia.objects;

import java.util.List;

public class LangFile implements Article{

    // language file, can contain multiple

    String name; // file name (not the path!)
    List<String[]> strings; // list of string arrays, each array has the format of [path, actual string]

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
