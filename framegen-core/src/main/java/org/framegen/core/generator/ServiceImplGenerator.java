package org.framegen.core.generator;

import lombok.extern.slf4j.Slf4j;

import org.framegen.config.FrameworkConfig;
import org.framegen.config.PackageConfig;
import org.framegen.config.RepositoryFrameworkEnum;
import org.framegen.core.model.Table;
import org.framegen.util.StrUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ServiceImplGenerator extends AbstractGenerator<Properties> {

    public ServiceImplGenerator(PackageConfig packageConfig, FrameworkConfig frameworkConfig, Path codePath,
            Table table) throws IOException {
        super("serviceImpl", packageConfig, frameworkConfig, codePath, table);
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();

        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl");
            imports.add(getFullPackage(PackageConfig::getMapper) + "." + StrUtil.toPascalCase(table.getTableName())
                    + "Mapper");
            imports.add(getFullPackage(PackageConfig::getModel) + "." + StrUtil.toPascalCase(table.getTableName()));
            imports.add(getFullPackage(PackageConfig::getService) + "." + StrUtil.toPascalCase(table.getTableName())
                    + "Service");
        }
        if (frameworkConfig.isEnableSolon()) {
            imports.add("org.noear.solon.annotation.Component");
        }
        if (frameworkConfig.isEnableSpring()) {
            imports.add("org.springframework.stereotype.Service");
        }

        return imports;
    }

    @Override
    protected List<String> getAnnotations() {
        List<String> annotations = new ArrayList<>();

        if (frameworkConfig.isEnableSolon()) {
            annotations.add("Component");
        }
        if (frameworkConfig.isEnableSpring()) {
            annotations.add("Service");
        }

        return annotations;
    }

    @Override
    protected String getClassName() {
        return StrUtil.toPascalCase(table.getTableName()) + "ServiceImpl";
    }

    @Override
    protected String getPackagePath() {
        return getFullPackage(PackageConfig::getServiceImpl);
    }

    @Override
    protected Properties getMoreData() {
        Properties data = new Properties();
        data.setProperty("frameworkName", frameworkConfig.repositoryFramework.name());
        data.setProperty("modelClassName", StrUtil.toPascalCase(table.getTableName()));
        data.setProperty("mapperClassName", StrUtil.toPascalCase(table.getTableName()) + "Mapper");
        data.setProperty("interfaceName", StrUtil.toPascalCase(table.getTableName()) + "Service");
        return data;
    }
}
