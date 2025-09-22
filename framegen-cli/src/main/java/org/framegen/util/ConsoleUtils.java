package org.framegen.util;

import java.util.*;

/**
 * 原生控制台工具类（兼容 Java 8，无需 JLine）
 * 提供格式化输出、颜色、菜单、输入等常用功能
 */
public class ConsoleUtils {

    private static final boolean colorEnabled = true;

    // TODO: 判断是否支持 ANSI 颜色

    /**
     * Java 8 安全的字符串重复（替代 String.repeat()）
     */
    private static String repeat(String str, int count) {
        if (count <= 0)
            return "";
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    // ======== 输出方法 ========

    public static void info(String msg) {
        println("Info: " + msg, ConsoleStyle.BLUE);
    }

    public static void success(String msg) {
        println("Success: " + msg, ConsoleStyle.GREEN);
    }

    public static void warn(String msg) {
        println("Warning: " + msg, ConsoleStyle.YELLOW);
    }

    public static void error(String msg) {
        println("Error: " + msg, ConsoleStyle.RED);
    }

    public static void title(String text) {
        // 计算中文字符的数量
        int chineseCharCount = StrUtil.countChineseChars(text);
        // 计算行的长度，每个中文字符增加2个"="
        int lineLength = Math.max(2, text.length() + 4 + chineseCharCount * 2);

        String line = repeat("=", lineLength);
        println("\n" + line, ConsoleStyle.CYAN, ConsoleStyle.BOLD);
        println("  " + text + "  ", ConsoleStyle.CYAN, ConsoleStyle.BOLD);
        println(line, ConsoleStyle.CYAN);
    }

    public static void banner(String text) {
        int totalWidth = 50;
        int paddingLen = Math.max(0, (totalWidth - text.length()) / 2);
        String padding = repeat(" ", paddingLen);
        String line = "  " + padding + text + padding + (text.length() % 2 == 1 ? " " : "") + "  ";
        println(line, ConsoleStyle.WHITE, ConsoleStyle.BOLD, ConsoleStyle.BG_BLUE);
    }

    public static void delay(String text, long totalMillis) {
        long stepMillis = totalMillis / text.length();
        for (char c : text.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(stepMillis);
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void pause() {
        print("按回车键继续...", ConsoleStyle.DIM);
        try (Scanner scanner = new Scanner(System.in)) {
            scanner.nextLine();
        }
    }

    public static void println(String msg, ConsoleStyle... styles) {
        if (colorEnabled) {
            StringBuilder sb = new StringBuilder();
            for (ConsoleStyle style : styles) {
                sb.append(style);
            }
            sb.append(msg).append(ConsoleStyle.RESET);
            System.out.println(sb.toString());
        } else {
            msg = msg.replaceAll("[^\\x00-\\x7F]", "").trim();
            System.out.println(msg);
        }
    }

    public static void print(String msg, ConsoleStyle... styles) {
        if (colorEnabled) {
            StringBuilder sb = new StringBuilder();
            for (ConsoleStyle style : styles) {
                sb.append(style);
            }
            sb.append(msg).append(ConsoleStyle.RESET);
            System.out.print(sb.toString());
        } else {
            msg = msg.replaceAll("[^\\x00-\\x7F]", "").trim();
            System.out.print(msg);
        }
    }

    // ======== 输入方法 ========

    private static String readLine() {
        return ScannerHolder.getScanner().nextLine().trim();
    }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return readLine();
    }

    public static int readInt(String prompt, int defaultInt) {
        try {
            System.out.printf("%s[%d]: ", prompt, defaultInt);
            return Integer.parseInt(readLine());
        } catch (Exception e) {
            return defaultInt;
        }
    }

    public static boolean readYesNo(String prompt) {
        return readYesNo(prompt, false);
    }

    public static boolean readYesNo(String prompt, boolean defaultToYes) {
        System.out.printf("%s[%s]: ", prompt, defaultToYes ? "Y/n" : "y/N");
        String input = readLine();

        if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
            return false;
        }
        if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
            return true;
        }

        return defaultToYes;
    }

    // ======== 菜单方法 ========

    public static int selectOne(String title, List<String> options, int defaultOption) {
        title(title);
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, options.get(i));
        }
        if (0 != defaultOption)
            System.out.printf("\n请输入选择[%d]: ", defaultOption);
        else
            System.out.print("\n请输入选择: ");

        try {
            int choice = Integer.parseInt(readLine());
            if (choice >= 1 && choice <= options.size()) {
                return choice;
            }
            return defaultOption;
        } catch (Exception e) {
            return defaultOption;
        }
    }

    public static Set<Integer> selectMultiple(String title, List<String> options) {
        title(title);
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, options.get(i));
        }
        System.out.println("  可输入：1 2 3 或 1,2,3 或 all");
        System.out.println("  排除模式：-1 -2 -3 或 -1,-2,-3");
        System.out.print("\n请输入选择[all]: ");

        String input;
        input = readLine();
        Set<Integer> selected = new LinkedHashSet<>();
        Set<Integer> excluded = new LinkedHashSet<>();

        String[] parts = input.split("[,，\\s]+");
        for (String part : parts) {
            if (part.equalsIgnoreCase("all") || part.equalsIgnoreCase("a")) {
                break;
            }
            if (part.isEmpty())
                continue;
            try {
                int num = Integer.parseInt(part);
                if (num >= 1 && num <= options.size()) {
                    selected.add(num);
                } else if (num < 0 && num >= -options.size()) {
                    excluded.add(-num);
                } else {
                    warn("忽略无效选项: " + num);
                }
            } catch (NumberFormatException e) {
                warn("忽略无法解析的输入: " + part);
            }
        }

        if (!excluded.isEmpty()) {
            for (int i = 1; i <= options.size(); i++) {
                if (!excluded.contains(i)) {
                    selected.add(i);
                }
            }
        }

        // 全选或无效输入则直接返回空集合
        return selected;
    }
}