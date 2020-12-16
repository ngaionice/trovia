package xyz.trovia.parsing;

import java.util.List;

public interface ParseStrategy {

    List<Class<?>> parse(String splitString);
}
