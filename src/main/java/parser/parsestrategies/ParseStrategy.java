package parser.parsestrategies;

import objects.Article;

interface ParseStrategy {

    Article parseObject(String splitString, String absPath) throws ParseException;
}
