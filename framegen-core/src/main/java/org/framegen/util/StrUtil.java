package org.framegen.util;

public class StrUtil {

    /**
     * 大驼峰
     * @param name 原始字符串, 由-或_分隔的单词
     * @return 大驼峰化后的字符串
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
            }
        }
        return sb.toString();
    }

    /**
     * 小驼峰
     * @param name 原始字符串, 由-或_分隔的单词
     * @return 小驼峰化后的字符串
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
            }
        }
        return sb.toString();
    }

}
