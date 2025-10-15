package org.framegen.core.generator;

import lombok.extern.slf4j.Slf4j;
import org.framegen.config.FrameworkConfig;
import org.framegen.config.PackageConfig;
import org.framegen.config.RepositoryFrameworkEnum;
import org.framegen.core.model.Column;
import org.framegen.core.model.Table;

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
    protected void setImports() {
        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS
                || frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("org.apache.ibatis.annotations.Mapper");
            imports.add(getFullPackage(PackageConfig::getModel) + "." + table.getPascalCaseName());
        }
        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS) {
            imports.add("java.util.List");
        }
        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("com.baomidou.mybatisplus.core.mapper.BaseMapper");
        }
        if (frameworkConfig.isEnableSpring()) {
            imports.add("org.springframework.stereotype.Repository");
        }
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
        return getFullPackage(PackageConfig::getDao);
    }

    @Override
    protected Properties getMoreData() {
        Properties data = new Properties();

        Column primaryColumn = table.getPrimaryColumn();

        data.setProperty("frameworkName", frameworkConfig.repositoryFramework.name());
        data.setProperty("modelClassName", table.getPascalCaseName());
        data.setProperty("primaryJType", primaryColumn.getDataType());
        data.setProperty("primaryVarName", primaryColumn.getVariableName());
        return data;
    }
}
