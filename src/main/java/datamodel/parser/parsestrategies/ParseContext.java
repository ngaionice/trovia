package datamodel.parser.parsestrategies;

import datamodel.objects.Article;

public class ParseContext {
    private final ParseStrategy strategy;

    public ParseContext(ParseStrategy strategy) {
        this.strategy = strategy;
    }

    public Article parse(String splitString, String absPath, boolean useRPath) throws ParseException {
        return strategy.parseObject(splitString, absPath, useRPath);
    }
}
