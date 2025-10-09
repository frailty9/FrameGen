package org.framegen.config;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 工程框架配置类
 */
@NoArgsConstructor
public final class FrameworkConfig {
    @Getter
    private Boolean enableSpring;
    @Getter
    private Boolean enableSolon;

    public RepositoryFrameworkEnum repositoryFramework = RepositoryFrameworkEnum.NATIVE_JDBC;

    public FrameworkConfig(Boolean enableSpring, Boolean enableSolon, RepositoryFrameworkEnum repositoryFramework) {
        this.enableSpring = enableSpring;
        this.enableSolon = enableSolon;

        verifyAppFramework();
    }

    public void enableSpring() {
        this.enableSpring = Boolean.TRUE;
        verifyAppFramework();
    }

    public void enableSolon() {
        this.enableSolon = Boolean.TRUE;
        verifyAppFramework();
    }

    public void enableMybatis() {
        verifyRepositoryFramework();
        this.repositoryFramework = RepositoryFrameworkEnum.MYBATIS;
    }

    public void enableMybatisPlus() {
        verifyRepositoryFramework();
        this.repositoryFramework = RepositoryFrameworkEnum.MYBATIS_PLUS;
    }

    private void verifyAppFramework() {
        if (Boolean.TRUE.equals(enableSpring) && Boolean.TRUE.equals(enableSolon)) {
            throw new IllegalArgumentException("您不能同时选择启用 Spring 和 Solon");
        }
    }

    private void verifyRepositoryFramework() {
        if (repositoryFramework != RepositoryFrameworkEnum.NATIVE_JDBC) {
            throw new IllegalArgumentException("您不能同时选择多个持久层框架");
        }
    }

    // === 模拟 boolean 类型的 getter 方法 ===

    public boolean isEnableSpring() {
        return Boolean.TRUE.equals(enableSpring);
    }

    public boolean isEnableSolon() {
        return Boolean.TRUE.equals(enableSolon);
    }
}
