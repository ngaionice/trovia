package xyz.trovia.creator;

import java.util.List;

interface ParseStrategy {

    List<?> parseObject(String splitString, String absPath);
}
