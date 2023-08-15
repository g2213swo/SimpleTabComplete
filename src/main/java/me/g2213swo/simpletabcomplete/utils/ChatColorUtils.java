package me.g2213swo.simpletabcomplete.utils;

public class ChatColorUtils {

    public static String stripColor(String string) {
        return string.replaceAll("\u00a7[0-9A-Fa-fk-orx]", "");
    }

}