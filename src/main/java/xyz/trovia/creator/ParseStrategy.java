package xyz.trovia.creator;

import xyz.trovia.objects.Article;

import java.util.List;

interface ParseStrategy {

    Article parseObject(String splitString, String absPath);
}
