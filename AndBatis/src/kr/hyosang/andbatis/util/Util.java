package kr.hyosang.andbatis.util;

public class Util {
    public static String escapeString(String str) {
        return str.replaceAll("'", "\\\\'");
    }

}
