package unused.model.managers;

import java.util.List;

public interface SearchManager {

    // has certain methods that allowing string searching

    /**
     * Returns the string identifier of the name of the object specified by the input relative path.
     * If no such Article exists, then returns null.
     *
     * @param rPath relative path of the object
     * @return a string identifier starting with "$"
     */
    String getName(String rPath);

    /**
     * Returns a list of string arrays containing names and relative paths of all stored Articles.
     *
     * Array format: [language file path of name, relative path]
     *
     * @param type "all", "new", "removed"
     * @return a list of string arrays
     */
    List<String[]> getAllNamesAndRPaths(String type);
}
