package controllers;

import parser.Parser;

import java.util.List;

public class TextPresenter {

    void createSuccess(String path, Parser.ObjectType type) {
        System.out.println(type.toString() + " creation at " + path + " was successful.");
    }

    void searchFailure(String rPath) {
        System.out.println("No matching string was found for " + rPath + ".");
    }

    void matchRecipeSuccess() {
        System.out.println("All new recipes have been matched.");
    }

    void matchRecipeFailure(List<String> failures) {
        System.out.println("The following recipes were not matched:\n");
        for (String item: failures) {
            System.out.println(item);
        }
    }

    void promptInput(String text) {
        System.out.println("Enter the " + text + " you would like to add:");
    }

    void confirmInput(String text) {
        System.out.println("Confirm that your input is correct:");
        System.out.println(text);
        System.out.println("Enter y to confirm your input, or n to change your input.");
    }
}
