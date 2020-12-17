package xyz.trovia.creator;

import java.util.List;

interface ParseStrategy {

    List<?> parse(String splitString, String absPath);
}
