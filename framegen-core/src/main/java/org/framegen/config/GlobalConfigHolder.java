package org.framegen.config;

public final class GlobalConfigHolder {

    public static boolean enableKotlin = false;

    public static boolean enableLombok = false;

    public static boolean enableMultiThread = false;

    public static RepositoryFrameworkEnum repositoryFramework = RepositoryFrameworkEnum.NATIVE_JDBC;

    private GlobalConfigHolder() {}
}
