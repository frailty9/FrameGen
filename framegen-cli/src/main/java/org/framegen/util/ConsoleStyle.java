package org.framegen.util;

import lombok.Getter;

/**
 * ANSI 终端样式枚举
 */
@Getter
public enum ConsoleStyle {
    RESET("\033[0m"), // 重置
    BOLD("\033[1m"), // 加粗
    DIM("\033[2m"), // 变暗
    
    RED("\033[31m"), // 红色
    GREEN("\033[32m"), // 绿色
    YELLOW("\033[33m"), // 黄色
    BLUE("\033[34m"), // 蓝色
    MAGENTA("\033[35m"), // 品红
    CYAN("\033[36m"), // 青色
    WHITE("\033[37m"), // 白色
    
    BG_RED("\033[41m"), // 背景色红色
    BG_GREEN("\033[42m"), // 背景色绿色
    BG_YELLOW("\033[43m"), // 背景色黄色
    BG_BLUE("\033[44m"); // 背景色蓝色

    private final String code;

    ConsoleStyle(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
