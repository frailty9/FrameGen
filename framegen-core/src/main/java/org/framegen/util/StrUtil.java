package org.framegen.util;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

public class StrUtil {

    @Getter
    private static String tableNamePrefix = null;

    public static void setTableNamePrefix(String tableNamePrefix) {
        if (StrUtil.tableNamePrefix == null) {
            tableNamePrefix = "";
        } else {
            tableNamePrefix = StrUtil.tableNamePrefix;
        }
    }

    /**
     * 大驼峰
     *
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
     *
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
     *
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
     * 返回字符串集合的最长共同语义前缀（按单词划分）
     * 支持下划线和驼峰命名法
     *
     * @param list 字符串集合
     * @return 共同前缀，无共同部分返回 ""
     */
    public static String getCommonPrefix(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        // 去除 null 字符串
        list.removeIf(Objects::isNull);
        if (list.isEmpty()) return "";
        // 分词
        List<List<String>> wordsList = new ArrayList<>();
        for (String s : list) {
            wordsList.add(splitToWords(s));
        }

        int minWordCount = wordsList.stream().mapToInt(List::size).min().orElse(0);
        List<String> commonWords = new ArrayList<>();

        for (int i = 0; i < minWordCount; i++) {
            String baseWord = wordsList.get(0).get(i);
            boolean allMatch = true;

            for (int j = 1; j < wordsList.size(); j++) {
                String currentWord = wordsList.get(j).get(i);
                if (!baseWord.equals(currentWord)) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                commonWords.add(baseWord); // 保留第一个字符串的原始大小写
            } else {
                break;
            }
        }

        if (commonWords.isEmpty()) return "";
        return rebuildWithOriginalStyle(commonWords, list.get(0), wordsList);
    }

    /*
     * 将字符串按命名规则拆分为单词
     * 支持：user_name, userName, UserName
     */
    private static List<String> splitToWords(String s) {
        List<String> words = new ArrayList<>();
        StringBuilder word = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '_') {
                if (word.length() > 0) {
                    words.add(word.toString());
                    word.setLength(0);
                }
            } else if (Character.isUpperCase(c)) {
                // 判断是否是新词：前一个字符是小写，或前一个是大写但当前不是连续大写结尾
                boolean isNewWord = word.length() > 0 &&
                        !Character.isUpperCase(word.charAt(word.length() - 1));

                // 特殊处理：如 APIGateway → [Api, Gateway]
                if (isNewWord || (word.length() == 1 && Character.isUpperCase(word.charAt(0)))) {
                    words.add(word.toString());
                    word.setLength(0);
                }
                word.append(c);
            } else {
                word.append(c);
            }
        }
        if (word.length() > 0) {
            words.add(word.toString());
        }
        return words;
    }

    /*
     * 将前缀
     */
    private static String rebuildWithOriginalStyle(List<String> words, String original, List<List<String>> originalWordsList) {
        // 找到第一个字符串的原始单词
        List<String> firstWords = originalWordsList.get(0);
        int size = Math.min(words.size(), firstWords.size());

        if (original.contains("_")) {
            List<String> result = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                result.add(firstWords.get(i)); // 完全保留原始拼写
            }
            return String.join("_", result);
        } else {
            StringBuilder sb = new StringBuilder(firstWords.get(0)); // 完全保留
            for (int i = 1; i < size; i++) {
                sb.append(firstWords.get(i)); // 驼峰拼接，但单词本身不变
            }
            return sb.toString();
        }
    }

    public static String removePrefix(String str) {
        if (str == null || tableNamePrefix == null) return str;
        if (tableNamePrefix.isEmpty()) return str;
        String stableNamePrefix = toPascalCase(tableNamePrefix);
        if (!str.startsWith(stableNamePrefix)) return str;
        return str.substring(stableNamePrefix.length());
    }
}
