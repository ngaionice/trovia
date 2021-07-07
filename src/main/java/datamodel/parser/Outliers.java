package datamodel.parser;

import java.util.Collections;
import java.util.List;

public class Outliers {

    // for outliers (read bugs from devs)
    public List<String> strings = Collections.singletonList("84 03 08 2E 70 72 65 66 61 62 73 5F 69 74 65 6D 5F 74 6F 6D 65 5F 62 6F 6F 73 74 65 72 5F 6A 61 64 65 63 6C 6F 76 65 72 5F 69 74 65 6D 5F 6E 61 6D 65 18 23 4C 65 67 65 6E 64 61 72 79 20 54 6F 6D 65 3A 20 4A 61 64 65 20 43 6C 6F 76 65 72 20 4A 6F 75 72 6E 61 6C ");
    public List<String> replacements = Collections.singletonList("84 03 08 2E 24 70 72 65 66 61 62 73 5F 69 74 65 6D 5F 74 6F 6D 65 5F 62 6F 6F 73 74 65 72 5F 6A 61 64 65 63 6C 6F 76 65 72 5F 69 74 65 6D 5F 6E 61 6D 65 18 23 4C 65 67 65 6E 64 61 72 79 20 54 6F 6D 65 3A 20 4A 61 64 65 20 43 6C 6F 76 65 72 20 4A 6F 75 72 6E 61 6C ");
}
