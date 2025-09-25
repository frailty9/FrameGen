package org.framegen.core.generator;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import org.framegen.config.FrameworkConfig;
import org.framegen.config.PackageConfig;
import org.framegen.core.entity.Table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class MapperXmlGenerator extends AbstractGenerator<Properties> {

    public MapperXmlGenerator(PackageConfig packageConfig, FrameworkConfig frameworkConfig, Path resourcePath, Table table) throws IOException {
        super("mapper.xml", "Mapper", frameworkConfig, resourcePath, table, packageConfig);
    }

    @Override
    protected List<String> getImports() {
        return Collections.emptyList();
    }

    @Override
    protected List<String> getAnnotations() {
        return Collections.emptyList();
    }

    @Override
    protected String getPackagePath() {
        return getFullPackage(PackageConfig::getMapper);
    }

    @Override
    public void generate() throws TemplateException, IOException {

        Path entityDirPath = codePath.resolve(
                getPackagePath().replace(".", File.separator));

        // 创建输出目录
        if (!entityDirPath.toFile().exists())
            entityDirPath.toFile().mkdirs();
        // 输出的文件路径
        Path entityFilePath = entityDirPath.resolve(getClassName() + ".xml");

        Properties data = new Properties();
        data.setProperty("mapperClassPath", getPackagePath() + "." + getClassName());

        this.write(data, entityFilePath.toFile());
    }
}
