package org.framegen.core.generator;

import lombok.extern.slf4j.Slf4j;

import org.framegen.config.FrameworkConfig;
import org.framegen.config.PackageConfig;
import org.framegen.config.RepositoryFrameworkEnum;
import org.framegen.core.entity.Table;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ServiceGenerator extends AbstractGenerator<Properties> {

    public ServiceGenerator(PackageConfig packageConfig, FrameworkConfig frameworkConfig, Path codePath, Table table) throws IOException {
        super("service", "Service", frameworkConfig, codePath, table, packageConfig);
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();

        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("com.baomidou.mybatisplus.extension.service.IService");
            imports.add(getFullPackage(PackageConfig::getEntity) + "." + table.getPascalCaseName());
        }

        return imports;
    }

    @Override
    protected List<String> getAnnotations() {
        return new ArrayList<>();
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
        data.setProperty("frameworkName", frameworkConfig.repositoryFramework.name());
        data.setProperty("entityClassName", table.getPascalCaseName());
        data.setProperty("hasImpl", String.valueOf(null != packageConfig.getServiceImpl()));
        return data;
    }
}
