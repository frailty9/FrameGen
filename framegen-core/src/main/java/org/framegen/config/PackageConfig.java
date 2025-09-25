package org.framegen.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成器输出包配置类
 */
@Data
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String origin;
        private String entity;
        private String mapper;
        private String service;
        private String serviceImpl;
        private String controller;

        public Builder origin(String origin) {
            this.origin = origin;
            return this;
        }

        public Builder entity(String entity) {
            this.entity = entity;
            return this;
        }

        public Builder mapper(String mapper) {
            this.mapper = mapper;
            return this;
        }

        public Builder service(String service) {
            this.service = service;
            return this;
        }

        public Builder serviceImpl(String serviceImpl) {
            this.serviceImpl = serviceImpl;
            return this;
        }

        public Builder controller(String controller) {
            this.controller = controller;
            return this;
        }

        public PackageConfig build() {
            return new PackageConfig(origin, entity, mapper, service, serviceImpl, controller);
        }
    }

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
