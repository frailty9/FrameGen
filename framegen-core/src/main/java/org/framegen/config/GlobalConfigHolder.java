package org.framegen.config;

public final class GlobalConfigHolder {

    public static boolean enableKotlin = false;

    public static boolean enableLombok = false;

    public static boolean enableMultiThread = false;

    public static String tableNamePrefix = null;

    public static FileWriteMode fileWriteMode = FileWriteMode.SKIP_IF_EXISTS;

    public static IdType idType = IdType.NONE;

    private GlobalConfigHolder() {}
}
