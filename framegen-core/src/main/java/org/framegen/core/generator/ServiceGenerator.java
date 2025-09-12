package org.framegen.core.generator;

import lombok.extern.slf4j.Slf4j;
import org.framegen.config.GlobalConfigHolder;
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
public class ServiceGenerator extends AbstractGenerator<Properties> {

    public ServiceGenerator(PackageConfig packageConfig, Path codePath, Table table) throws IOException {
        super("service", packageConfig, codePath, table);
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();

        if (GlobalConfigHolder.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("com.baomidou.mybatisplus.extension.service.IService");
            imports.add(getFullPackage(PackageConfig::getModel) + "." + StrUtil.toPascalCase(table.getTableName()));
        }

        return imports;
    }

    @Override
    protected List<String> getAnnotations() {
        return new ArrayList<>();
    }

    @Override
    protected String getClassName() {
        return StrUtil.toPascalCase(table.getTableName()) + "Service";
    }

    @Override
    protected String getPackagePath() {
        if (null != packageConfig.getOrigin() && !packageConfig.getOrigin().isEmpty()) {
            return packageConfig.getOrigin() + "." + packageConfig.getService();
        } else {
            return packageConfig.getService();
        }
    }

    @Override
    protected Properties getMoreData() {
        Properties data = new Properties();
        data.setProperty("frameworkName", GlobalConfigHolder.repositoryFramework.name());
        data.setProperty("modelClassName", StrUtil.toPascalCase(table.getTableName()));
        data.setProperty("hasImpl", String.valueOf(null != packageConfig.getServiceImpl()));
        return data;
    }
}
