package datamodel.objects;

import java.util.List;
import java.util.Map;

public class Bench implements Article {

    String name; // the string identifier of the bench's name
    String rPath;
    Map<String[], List<String>> categories; // key: [category path, category #]; value: list of recipe file names

    public Bench(String name, String rPath, Map<String[], List<String>> categories) {
        this.name = name;
        this.rPath = rPath;
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRPath() {
        return rPath;
    }

    public Map<String[], List<String>> getCategories() {
        return categories;
    }

    public void setCategories(Map<String[], List<String>> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bench)) return false;
        Bench b = (Bench) o;
        return b.getName().equals(name) && b.getRPath().equals(rPath) && b.getCategories().equals(categories);
    }
}
