package managers;

import objects.LangFile;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LanguageManager implements Manager, Serializable {

    public LanguageManager() {
        langFileMap = new HashMap<>(300);

        // creates custom notes
        langFileMap.put("languages/en/prefabs_notes", new LangFile("prefabs_notes", "languages/en/prefabs_notes", new HashMap<>(100)));
    }

    // use rPath as the key, as it is unique to each Recipe
    Map<String, LangFile> langFileMap;

    // uses the identifier "$something" as key, for more efficient searching
    Map<String, String> stringsMap = new HashMap<>(20000);

    // used to track changes between local version and online database:
    Map<String, LangFile> addMap = new HashMap<>(50);
    Map<String, LangFile> removeMap = new HashMap<>(50);

    // used to track changes between local version and online database - strings version
    Map<String, String> addStringMap = new HashMap<>(1000);
    Map<String, String> removeStringMap = new HashMap<>(1000);

    // addMap is used to track new and edited language files
    // removeMap is used to track removed language files (relative to last serialized/synchronization state)

    // note that LangFiles should never have strings deleted, only added to them; else this will break ModelController.addNotes

    public void addLangFile(LangFile langFile) {

        String path = langFile.getRPath();
        // first check if language file with same key exists; if true, then we add it to removeMap before overwriting it
        if (langFileMap.containsKey(path)) {

            // track new strings - assumes new language file has more strings than old
            Map<String, String> original = langFileMap.get(path).getStrings();
            Map<String, String> updated = langFile.getStrings();
            updated.entrySet().removeAll(original.entrySet());
            addStringMap.putAll(updated);
        }

        // add the language file
        langFileMap.put(langFile.getRPath(), langFile);
        addMap.put(langFile.getRPath(), langFile);

        // add the strings in the language file
        stringsMap.putAll(langFile.getStrings());
    }

    // unlikely to be a method that is required, but included just in case
    public void removeLangFile(String rPath) {

        // remove all strings from stringsMap
        for (String string: langFileMap.get(rPath).getStrings().keySet()) {
            removeStringMap.put(string, stringsMap.get(string));
            stringsMap.remove(string);
        }

        // remove the language file
        removeMap.put(rPath, langFileMap.get(rPath));
        langFileMap.remove(rPath);
    }

    // getters

    public String getString(String identifier) {
        for (LangFile file: langFileMap.values()) {
            Map<String, String> strings = file.getStrings();
            if (strings.containsKey(identifier)) {
                return strings.get(identifier);
            }
        }
        return null;
    }

    public Map<String, String> getAllFileStrings(String rPath) {
        return langFileMap.get(rPath).getStrings();
    }

    public Map<String, String> getAllStrings() {
        return stringsMap;
    }

    public int getLangFileLength(String rPath) {
        return langFileMap.get(rPath).getStrings().size();
    }

    public Set<String> getAllNames() {
        return langFileMap.keySet();
    }

    // setters

    public void addString(String rPath, String key, String value) {
        stringsMap.put(key, value);
        addStringMap.put(key, value);
        langFileMap.get(rPath).getStrings().put(key, value);
        addMap.put(rPath, langFileMap.get(rPath));
    }
}
