package com.dioextreme.nully.utils;

public class TextUtils
{
    public static <T> String getBold(T text)
    {
        return "**%s**".formatted(text);
    }

    public static String getCodeFormattedText(String text)
    {
        return "```%s```\n".formatted(text);
    }

    public static String getHyperlink(String text, String url)
    {
        return "[%s](%s)".formatted(text, url);
    }

    public static String getPluralized(String str, long count)
    {
        return count == 1? str : str + 's';
    }
}
