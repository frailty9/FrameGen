package org.framegen.config;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工程框架配置类
 */
@Data
@NoArgsConstructor
public final class FrameworkConfig {
    private Boolean enableSpring;
    private Boolean enableSolon;
    // ======
    private Boolean enableMybatis;
    private Boolean enableMybatisPlus;

    public FrameworkConfig(Boolean enableSpring, Boolean enableSolon, Boolean enableMybatis, Boolean enableMybatisPlus) {
        this.enableSpring = enableSpring;
        this.enableSolon = enableSolon;
        this.enableMybatis = enableMybatis;
        this.enableMybatisPlus = enableMybatisPlus;

        verifyData();
    }

    public static class Builder {
        private Boolean enableSpring;
        private Boolean enableSolon;
        private Boolean enableMybatis;
        private Boolean enableMybatisPlus;

        public Builder enableSpring() {
            enableSpring = Boolean.TRUE;
            return this;
        }

        public Builder enableSolon() {
            enableSolon = Boolean.TRUE;
            return this;
        }

        public Builder enableMybatis() {
            enableMybatis = Boolean.TRUE;
            return this;
        }

        public Builder enableMybatisPlus() {
            enableMybatisPlus = Boolean.TRUE;
            return this;
        }

        public FrameworkConfig build() {
            return new FrameworkConfig(enableSpring, enableSolon, enableMybatis, enableMybatisPlus);
        }
    }

    public void setEnableSpring(Boolean enableSpring) {
        this.enableSpring = enableSpring;
        verifyData();
    }

    public void setEnableSolon(Boolean enableSolon) {
        this.enableSolon = enableSolon;
        verifyData();
    }

    private void verifyData() {
        if (Boolean.TRUE.equals(enableSpring) && Boolean.TRUE.equals(enableSolon)) {
            throw new IllegalArgumentException("您不能同时开启 Spring 和 Solon");
        }
    }

    // === 模拟 boolean 类型的 getter 方法 ===

    public boolean isEnableSpring() {
        return Boolean.TRUE.equals(enableSpring);
    }

    public boolean isEnableSolon() {
        return Boolean.TRUE.equals(enableSolon);
    }

    public boolean isEnableMybatis() {
        return Boolean.TRUE.equals(enableMybatis);
    }

    public boolean isEnableMybatisPlus() {
        return Boolean.TRUE.equals(enableMybatisPlus);
    }
}
