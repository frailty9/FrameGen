package org.framegen.core.generator;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.framegen.config.FrameworkConfig;
import org.framegen.config.PackageConfig;
import org.framegen.core.entity.Column;
import org.framegen.core.entity.Table;
import org.framegen.util.StrUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class MapperXmlGenerator extends AbstractGenerator<Map<String, Object>> {

    public MapperXmlGenerator(PackageConfig packageConfig, String classNameSuffix, FrameworkConfig frameworkConfig, Path resourcePath, Table table) throws IOException {
        super("mapper.xml", classNameSuffix, frameworkConfig, resourcePath, table, packageConfig);
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

        Column primaryColumn = table.getPrimaryColumn();

        Map<String, Object> data = new HashMap<>();
        data.put("mapperClassPath", getPackagePath() + "." + getClassName());
        data.put("entityClassPath", getFullPackage(PackageConfig::getEntity) +
                "." + StrUtil.removePrefix(table.getPascalCaseName()));
        data.put("tableName", table.getTableName());
        data.put("fields", table.getColumns().stream().map(Column::getFieldName).collect(Collectors.toList()));
        data.put("entityFieldJNames", table.getColumns().stream().map(Column::getVariableName).collect(Collectors.toList()));
        data.put("primaryJType", primaryColumn.getDataType());
        data.put("primaryVarName", primaryColumn.getVariableName());
        data.put("isAutoIncrement", primaryColumn.getExtra().contains("auto_increment"));

        this.write(data, entityFilePath.toFile());
    }
}
