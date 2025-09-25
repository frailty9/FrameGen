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
    private String origin;
    // 模型包名
    private String entity;
    // 数据层包名
    private String mapper;
    // 服务层包名
    private String service;
    // 服务实现层包名
    private String serviceImpl;
    // 控制层包名
    private String controller;

    public void applyDefault(FrameworkConfig frameworkConfig) {
        if (null == entity) {
            entity = "entity";
        }
        if (null == mapper && (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS
                || frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS)) {
            mapper = "mapper";
        }
        if (null == service && frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            service = "service";
        }
        if (null == serviceImpl && frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            serviceImpl = "service.impl";
        }
    }
}
