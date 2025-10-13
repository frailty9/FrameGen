package org.framegen.config;

public enum FileWriteMode {
    OVERWRITE,      // 覆盖模式：直接写入，覆盖原有文件
    SKIP_IF_EXISTS  // 跳过模式：若文件已存在则不覆盖
}
