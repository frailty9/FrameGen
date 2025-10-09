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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Boolean enableSpring;
        private Boolean enableSolon;
        private RepositoryFrameworkEnum repositoryFramework = RepositoryFrameworkEnum.NATIVE_JDBC;

        private Builder() {}

        public Builder enableSpring() {
            enableSpring = Boolean.TRUE;
            return this;
        }

        public Builder enableSolon() {
            enableSolon = Boolean.TRUE;
            return this;
        }

        public Builder enableMybatis() {
            verifyRepositoryFramework();
            repositoryFramework = RepositoryFrameworkEnum.MYBATIS;
            return this;
        }

        public Builder enableMybatisPlus() {
            verifyRepositoryFramework();
            repositoryFramework = RepositoryFrameworkEnum.MYBATIS_PLUS;
            return this;
        }

        public FrameworkConfig build() {
            return new FrameworkConfig(enableSpring, enableSolon, repositoryFramework);
        }

        private void verifyRepositoryFramework() {
            if (repositoryFramework != RepositoryFrameworkEnum.NATIVE_JDBC) {
                throw new IllegalArgumentException("您不能同时选择多个持久层框架");
            }
        }
    }

    public void setEnableSpring(Boolean enableSpring) {
        this.enableSpring = enableSpring;
        verifyAppFramework();
    }

    public void setEnableSolon(Boolean enableSolon) {
        this.enableSolon = enableSolon;
        verifyAppFramework();
    }

    public void enableMybatis() {
        verifyRepositoryFramework();
        repositoryFramework = RepositoryFrameworkEnum.MYBATIS;
    }

    public void enableMybatisPlus() {
        verifyRepositoryFramework();
        repositoryFramework = RepositoryFrameworkEnum.MYBATIS_PLUS;
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
