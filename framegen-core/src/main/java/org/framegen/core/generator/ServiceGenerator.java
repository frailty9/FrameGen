package org.framegen.core.generator;

import lombok.extern.slf4j.Slf4j;

import org.framegen.config.FrameworkConfig;
import org.framegen.config.PackageConfig;
import org.framegen.config.RepositoryFrameworkEnum;
import org.framegen.core.model.Table;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ServiceGenerator extends AbstractGenerator<Properties> {

    public ServiceGenerator(PackageConfig packageConfig, String classNameSuffix, FrameworkConfig frameworkConfig, Path codePath, Table table) throws IOException {
        super("service", classNameSuffix, frameworkConfig, codePath, table, packageConfig);
    }

    @Override
    protected void setImports() {
        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("com.baomidou.mybatisplus.extension.service.IService");
            imports.add(getFullPackage(PackageConfig::getModel) + "." + table.getPascalCaseName());
        }
    }

    @Override
    protected List<String> getAnnotations() {
        return new ArrayList<>();
    }

    @Override
    protected String getPackagePath() {
        return getFullPackage(PackageConfig::getService);
    }

    @Override
    protected Properties getMoreData() {
        Properties data = new Properties();
        data.setProperty("frameworkName", frameworkConfig.repositoryFramework.name());
        data.setProperty("modelClassName", table.getPascalCaseName());
        data.setProperty("hasImpl", String.valueOf(null != packageConfig.getServiceImpl()));
        return data;
    }
}
