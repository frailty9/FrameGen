package org.framegen.core.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.framegen.config.PackageConfig;
import org.framegen.core.generator.props.GeneratorProps;
import org.framegen.core.model.Table;
import org.framegen.util.StrUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class MapperXmlGenerator {

    private final Template template;
    private final PackageConfig packageConfig;
    private final Path resourcePath;
    private final Table table;

    public MapperXmlGenerator(PackageConfig packageConfig, Path resourcePath, Table table) throws IOException {
        String templateName = "mapper.xml";
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        // 使用类加载器加载模板
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        template = cfg.getTemplate(templateName + ".ftl");

        this.packageConfig = packageConfig;
        this.resourcePath = resourcePath;
        this.table = table;
    }

    private String getFlieName() {
        return StrUtil.toPascalCase(table.getTableName()) + "Mapper";
    }

    protected String getPackagePath() {
        if (null != packageConfig.getOrigin() && !packageConfig.getOrigin().isEmpty()) {
            return packageConfig.getOrigin() + "." + packageConfig.getMapper();
        } else {
            return packageConfig.getMapper();
        }
    }

    private GeneratorProps<Properties> getData() {

        String className = StrUtil.toPascalCase(table.getTableName() + "_mapper");

        Properties data = new Properties();
        data.setProperty("mapperClassPath", getPackagePath() + "." + className);

        GeneratorProps.Builder<Properties> builder = GeneratorProps.builder();
        builder.data(data);

        return builder.build();
    }

    protected void write(Object data, File outFile) throws TemplateException, IOException {
        Writer writer = new FileWriter(outFile);
        template.process(data, writer);
        writer.flush();
    }

    public void generate() throws TemplateException, IOException {

        Path modelDirPath = resourcePath.resolve(
                getPackagePath().replace(".", File.separator));

        // 创建输出目录
        if (!modelDirPath.toFile().exists())
            modelDirPath.toFile().mkdirs();
        // 输出的文件路径
        Path modelFilePath = modelDirPath.resolve(getFlieName() + ".xml");

        this.write(getData(), modelFilePath.toFile());
    }
}
