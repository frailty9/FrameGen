package org.framegen.core.generator;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.framegen.config.PackageConfig;
import org.framegen.core.model.Table;
import org.framegen.util.StrUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class MapperXmlGenerator extends AbstractGenerator<Properties> {

    public MapperXmlGenerator(PackageConfig packageConfig, Path resourcePath, Table table) throws IOException {
        super("mapper.xml", packageConfig, resourcePath, table);
    }

    @Override
    protected List<String> getImports() {
        return null;
    }

    @Override
    protected List<String> getAnnotations() {
        return null;
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
    public void generate() throws TemplateException, IOException {

        Path modelDirPath = codePath.resolve(
                getPackagePath().replace(".", File.separator));

        // 创建输出目录
        if (!modelDirPath.toFile().exists())
            modelDirPath.toFile().mkdirs();
        // 输出的文件路径
        Path modelFilePath = modelDirPath.resolve(getClassName() + ".xml");

        Properties data = new Properties();
        data.setProperty("mapperClassPath", getPackagePath() + "." + getClassName());

        this.write(data, modelFilePath.toFile());
    }
}
