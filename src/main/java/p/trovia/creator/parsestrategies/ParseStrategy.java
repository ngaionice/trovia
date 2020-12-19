package p.trovia.creator.parsestrategies;

import p.trovia.objects.Article;

interface ParseStrategy {

    Article parseObject(String splitString, String absPath);
}
