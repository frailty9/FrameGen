package org.framegen.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

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

    /**
     * 统计字符串包含汉字的个数
     * @param text 待统计的字符串
     * @return 汉字的个数
     */
    public static int countChineseChars(String text) {
        // 使用正则表达式匹配中文字符
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher matcher = pattern.matcher(text);

        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * 获取字符串列表的共同前缀
     * @param strings 字符串列表
     * @return 共同前缀，如果没有则返回null
     */
    public static String getCommonPrefix(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return "";
        }
        
        String prefix = strings.get(0);
        for (String str : strings) {
            while (!str.startsWith(prefix)) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) {
                    return "";
                }
            }
        }
        return prefix;
    }
}
