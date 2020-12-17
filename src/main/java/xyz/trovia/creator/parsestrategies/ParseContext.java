package xyz.trovia.creator.parsestrategies;

import xyz.trovia.creator.parsestrategies.ParseStrategy;
import xyz.trovia.objects.Article;

public class ParseContext {
    private ParseStrategy strategy;

    public ParseContext(ParseStrategy strategy) {
        this.strategy = strategy;
    }

    public Article parse(String splitString, String absPath) {
        return strategy.parseObject(splitString, absPath);
    }
}
