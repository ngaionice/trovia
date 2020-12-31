package parser.parsestrategies;

import objects.Article;

public class ParseContext {
    private ParseStrategy strategy;

    public ParseContext(ParseStrategy strategy) {
        this.strategy = strategy;
    }

    public Article parse(String splitString, String absPath) throws ParseException {
        return strategy.parseObject(splitString, absPath);
    }
}
