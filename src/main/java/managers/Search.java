package managers;

public interface Search {

    // has certain methods that allowing string searching

    /**
     * Returns the string identifier of the name of the object specified by the input relative path.
     * If no such Article exists, then returns null.
     *
     * @param rPath relative path of the object
     * @return a string identifier starting with "$"
     */
    String getName(String rPath);
}
