package datamodel.objects;

import java.util.List;
import java.util.Map;

public class Bench implements Article {

    String name; // the string identifier of the bench's name
    String rPath;
    Map<String, Integer> order;
    Map<String, List<String>> categories;

    public Bench(String name, String rPath, Map<String, Integer> order, Map<String, List<String>> categories) {
        this.name = name;
        this.rPath = rPath;
        this.order = order;
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public String getRPath() {
        return rPath;
    }

    public Map<String, Integer> getOrder() {
        return order;
    }

    public Map<String, List<String>> getCategories() {
        return categories;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bench)) return false;
        Bench b = (Bench) o;
        return name.equals(b.getName()) && rPath.equals(b.getRPath()) && categories.equals(b.getCategories()) && order.equals(b.getOrder());
    }
}
