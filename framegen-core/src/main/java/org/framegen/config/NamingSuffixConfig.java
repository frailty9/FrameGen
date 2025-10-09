package org.framegen.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名后缀配置类
 */
@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public final class NamingSuffixConfig {

    // 实体类名后缀
    @lombok.Builder.Default
    private String model = "";
    // 持久层类名后缀
    private String dao;
    // 服务类名后缀
    @lombok.Builder.Default
    private String service = "Service";
    // 服务实现类名后缀
    @lombok.Builder.Default
    private String serviceImpl = "ServiceImpl";
    // 控制器类名后缀
    @lombok.Builder.Default
    private String controller = "Controller";

    /**
     * 根据框架配置补充默认值（特别是 dao）
     */
    public NamingSuffixConfig withDefaults(FrameworkConfig frameworkConfig) {
        String resolvedDao = (this.dao != null) ? this.dao :
                (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS ||
                        frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS)
                        ? "Mapper" : "Dao";

        return new NamingSuffixConfig(
                this.model,
                resolvedDao,
                this.service,
                this.serviceImpl,
                this.controller
        );
    }
}