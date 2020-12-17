package xyz.trovia.creator.parsestrategies;

import xyz.trovia.objects.Article;

import java.util.List;

interface ParseStrategy {

    Article parseObject(String splitString, String absPath);
}
