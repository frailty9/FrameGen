package org.framegen.util;

public class StrUtil {

    /**
     * 大驼峰
     */
    public static String toPascalCase(String name) {
        if (name == null || name.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_' || c == '-') {
                nextUpper = true;
            } else if (nextUpper || (i == 0 && !Character.isUpperCase(c))) {
                sb.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                sb.append(c);
                nextUpper = false;
            }
        }
        return sb.toString();
    }

    /**
     * 小驼峰
     */
    public static String toCamelCase(String name) {
        if (name == null || name.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_' || c == '-') {
                nextUpper = true;
            } else if (nextUpper || (i == 0 && !Character.isLowerCase(c))) {
                sb.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                sb.append(c);
                nextUpper = false;
            }
        }
        return sb.toString();
    }

}
