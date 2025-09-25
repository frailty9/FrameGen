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
public class MapperGenerator extends AbstractGenerator<Properties> {

    public MapperGenerator(PackageConfig packageConfig, String classNameSuffix, FrameworkConfig frameworkConfig, Path codePath, Table table) throws IOException {
        super("mapper", classNameSuffix, frameworkConfig, codePath, table, packageConfig);
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();

        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS
                || frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("org.apache.ibatis.annotations.Mapper");
        }
        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("com.baomidou.mybatisplus.core.mapper.BaseMapper");
            imports.add(getFullPackage(PackageConfig::getEntity) + "." + table.getPascalCaseName());
        }
        if (frameworkConfig.isEnableSpring()) {
            imports.add("org.springframework.stereotype.Repository");
        }

        return imports;
    }

    @Override
    protected List<String> getAnnotations() {
        List<String> annotations = new ArrayList<>();

        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS
                || frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            annotations.add("Mapper");
        }
        if (frameworkConfig.isEnableSpring()) {
            annotations.add("Repository");
        }

        return annotations;
    }

    @Override
    protected String getPackagePath() {
        return getFullPackage(PackageConfig::getMapper);
    }

    @Override
    protected Properties getMoreData() {
        Properties data = new Properties();
        data.setProperty("frameworkName", frameworkConfig.repositoryFramework.name());
        data.setProperty("entityClassName", table.getPascalCaseName());
        return data;
    }
}
