package datamodel.objects;

import java.util.Map;

public class CollectionIndex implements Article {

    String rPath;
    String type;
    Map<String, String> names;
    Map<String, Map<String, String>> categories;

    public CollectionIndex(String rPath, String type, Map<String, String> nameMap, Map<String, Map<String, String>> categories) {
        this.rPath = rPath;
        this.type = type;
        this.names = nameMap;
        this.categories = categories;
    }

    public String getRPath() {
        return rPath;
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getNames() {
        return names;
    }

    public Map<String, Map<String, String>> getCategories() {
        return categories;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNames(Map<String, String> names) {
        this.names = names;
    }

    public void setCategories(Map<String, Map<String, String>> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CollectionIndex)) return false;
        CollectionIndex ci = (CollectionIndex) o;
        return rPath.equals(ci.getRPath()) && type.equals(ci.getType()) && names.equals(ci.getNames()) && categories.equals(ci.getCategories());
    }
}
