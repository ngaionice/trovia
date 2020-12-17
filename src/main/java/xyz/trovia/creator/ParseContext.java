package xyz.trovia.creator;

import xyz.trovia.objects.Article;

import java.util.List;

public class ParseContext {
    private ParseStrategy strategy;

    public ParseContext(ParseStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Article> parse(String splitString, String absPath) {
        return strategy.parseObject(splitString, absPath);
    }
}
