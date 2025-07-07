package org.framegen.core.generator;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.core.generator.props.ModelProps;
import org.framegen.core.model.Table;
import org.framegen.util.StrUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class ModelGenerator {

    private final Template template;
    private final PackageConfig packageConfig;
    private final Path codePath;
    private final Table table;

    public ModelGenerator(PackageConfig packageConfig, Path codePath, Table table) throws IOException {
        String templateName = GlobalConfigHolder.enableKotlin ? "model.kt" : "model";
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        // 使用类加载器加载模板
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        template = cfg.getTemplate(templateName + ".ftl");

        this.packageConfig = packageConfig;
        this.codePath = codePath;
        this.table = table;
    }

    private List<String> getImports() {
        List<String> imports = new ArrayList<>();
        table.getColumns().forEach(column -> {
            String codeType = column.getDataType();
            if (GlobalConfigHolder.enableKotlin) {

            } else {
                switch (codeType) {
                    case "BigDecimal":
                        imports.add("java.math.BigDecimal");
                        break;
                    case "LocalDate":
                        imports.add("java.time.LocalDate");
                        break;
                    case "LocalTime":
                        imports.add("java.time.LocalTime");
                        break;
                    case "LocalDateTime":
                        imports.add("java.time.LocalDateTime");
                        break;
                    default:
                        break;
                }
            }
        });
        if (GlobalConfigHolder.enableLombok) {
            imports.add("lombok.Data");
            imports.add("lombok.Builder");
        }

        return imports;
    }

    private List<String> getAnnotations() {
        List<String> annotations = new ArrayList<>();
        if (GlobalConfigHolder.enableLombok) {
            annotations.add("Data");
            annotations.add("Builder");
        }
        return annotations;
    }

    private String getClassName() {
        return StrUtil.toPascalCase(table.getTableName()) +
                (GlobalConfigHolder.enableKotlin ? ".kt" : ".java");
    }

    protected String getPackagePath() {
        if (null != packageConfig.getOrigin() && !packageConfig.getOrigin().isEmpty()) {
            return packageConfig.getOrigin() + "." + packageConfig.getModel();
        } else {
            return packageConfig.getModel();
        }
    }

    private ModelProps getData() {
        String modelPackage = getPackagePath();

        String tableComment;
        if (null != table.getTableComment() && !table.getTableComment().isEmpty()) {
            tableComment = table.getTableComment();
        } else {
            tableComment = table.getTableName();
        }

        String className = StrUtil.toPascalCase(table.getTableName());

        return ModelProps.builder()
                .packagePath(modelPackage)
                .imports(getImports())
                .tableComment(tableComment)
                .annotations(getAnnotations())
                .className(className)
                .columns(table.getColumns())
                .build();
    }

    protected void write(Object data, File outFile) throws TemplateException, IOException {
        Writer writer = new FileWriter(outFile);
        template.process(data, writer);
        writer.flush();
    }

    public void generate() throws TemplateException, IOException {

        Path modelDirPath = codePath.resolve(
                getPackagePath().replace(".", File.separator));

        // 创建输出目录
        if (!modelDirPath.toFile().exists()) modelDirPath.toFile().mkdirs();
        // 输出的文件路径
        Path modelFilePath = modelDirPath.resolve(getClassName());

        this.write(getData(), modelFilePath.toFile());
    }
}
