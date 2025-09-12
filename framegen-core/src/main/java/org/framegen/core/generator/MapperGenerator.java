package org.framegen.core.generator;

import lombok.extern.slf4j.Slf4j;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.config.RepositoryFrameworkEnum;
import org.framegen.core.model.Table;
import org.framegen.util.StrUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class MapperGenerator extends AbstractGenerator<Properties> {

    public MapperGenerator(PackageConfig packageConfig, Path codePath, Table table) throws IOException {
        super("mapper", packageConfig, codePath, table);
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();

        if (GlobalConfigHolder.repositoryFramework == RepositoryFrameworkEnum.MYBATIS
                || GlobalConfigHolder.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("org.apache.ibatis.annotations.Mapper");
        }
        if (GlobalConfigHolder.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("com.baomidou.mybatisplus.core.mapper.BaseMapper");
            imports.add(getFullPackage(PackageConfig::getModel) + "." + StrUtil.toPascalCase(table.getTableName()));
        }

        return imports;
    }

    @Override
    protected List<String> getAnnotations() {
        List<String> annotations = new ArrayList<>();

        if (GlobalConfigHolder.repositoryFramework == RepositoryFrameworkEnum.MYBATIS
                || GlobalConfigHolder.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            annotations.add("Mapper");
        }

        return annotations;
    }

    @Override
    protected String getClassName() {
        return StrUtil.toPascalCase(table.getTableName()) + "Mapper";
    }

    @Override
    protected String getPackagePath() {
        return getFullPackage(PackageConfig::getMapper);
    }

    @Override
    protected Properties getMoreData() {
        Properties data = new Properties();
        data.setProperty("frameworkName", GlobalConfigHolder.repositoryFramework.name());
        data.setProperty("modelClassName", StrUtil.toPascalCase(table.getTableName()));
        return data;
    }
}
