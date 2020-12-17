package xyz.trovia.creator;

import xyz.trovia.objects.Article;

import java.util.List;

interface ParseStrategy {

    List<Article> parseObject(String splitString, String absPath);
}
