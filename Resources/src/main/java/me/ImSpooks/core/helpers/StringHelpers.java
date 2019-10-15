package me.ImSpooks.core.helpers;

/**
 * Created by Nick on 14 okt. 2019.
 * Copyright © ImSpooks
 */
public class StringHelpers {

    public static String capitalize(String input) {
        String[] string = input.split(" ");
        StringBuilder output = new StringBuilder();

        for (String s : string) {
            output.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase()).append(" ");
        }

        return output.substring(0, output.length() - 1);
    }

    public static String firstUpper(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String firstLower(String input) {
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    public static String addChar(String str, char ch, int position) {
        StringBuilder sb = new StringBuilder(str);
        if (position >= 0)
            sb.insert(position, ch);
        else
            sb = new StringBuilder(ch + str);
        return sb.toString();
    }
}
