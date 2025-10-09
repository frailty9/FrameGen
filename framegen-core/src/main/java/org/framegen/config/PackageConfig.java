package org.framegen.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成器输出包配置类
 */
@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class PackageConfig {

    // 公共前缀包名
    private String root;
    // 模型包名
    @lombok.Builder.Default
    private String model = "model";
    // 数据层包名
    private String dao;
    // 服务层包名
    private String service;
    // 服务实现层包名
    private String serviceImpl;
    // 控制层包名
    private String controller;

    public PackageConfig withDefaults(FrameworkConfig frameworkConfig) {
        if (null == dao && (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS
                || frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS)) {
            dao = "mapper";
        }
        if (null == service && frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            service = "service";
        }
        if (null == serviceImpl && frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            serviceImpl = "service.impl";
        }
        return new PackageConfig(root, model, dao, service, serviceImpl, controller);
    }
}
