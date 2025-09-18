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
        if (count <= 0) return "";
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
            try { Thread.sleep(stepMillis); } catch (InterruptedException e) { break; }
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

    public static String readLine() {
        try (Scanner scanner = new Scanner(System.in)) {
            return scanner.nextLine().trim();
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                error("请输入有效数字！");
            }
        }
    }

    public static boolean readYesNo(String prompt) {
        return readYesNo(prompt, false);
    }

    public static boolean readYesNo(String prompt, boolean defaultToYes) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print(prompt + " (" + (defaultToYes ? "Y/n" : "y/N") + "): ");
            String input = scanner.nextLine().trim();
            
            if (defaultToYes && (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no"))) {
                return false;
            }
            if (!defaultToYes && (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes"))) {
                return true;
            }
            
            return defaultToYes;
        }
    }

    // ======== 菜单方法 ========

    public static int selectOne(String title, List<String> options) {
        title(title);
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, options.get(i));
        }
        System.out.println("  0. 退出");
        System.out.print("\n请输入选择: ");

        while (true) {
            try (Scanner scanner = new Scanner(System.in)) {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) return 0;
                if (choice >= 1 && choice <= options.size()) {
                    return choice;
                }
                error("请输入 0-" + options.size() + " 之间的数字");
            } catch (NumberFormatException e) {
                error("请输入数字");
            }
        }
    }

    public static Set<Integer> selectMultiple(String title, List<String> options) {
        title(title);
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, options.get(i));
        }
        System.out.println("  可输入：1 2 3 或 1,2,3 或 all");
        System.out.println("  排除模式：-1 -2 -3 或 -1,-2,-3");
        System.out.print("\n请输入选择: ");

        String input;
        try (Scanner scanner = new Scanner(System.in)) {
            input = scanner.nextLine().trim();
        }
        Set<Integer> selected = new LinkedHashSet<>();
        Set<Integer> excluded = new LinkedHashSet<>();

        if (input.equalsIgnoreCase("all")) {
            for (int i = 1; i <= options.size(); i++) selected.add(i);
            return selected;
        }

        String[] parts = input.split("[,，\\s]+");
        for (String part : parts) {
            if (part.isEmpty()) continue;
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

        return selected;
    }
}