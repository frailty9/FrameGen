package org.framegen.core.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.config.RepositoryFrameworkEnum;
import org.framegen.core.generator.props.GeneratorProps;
import org.framegen.core.model.Table;
import org.framegen.util.StrUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

@Slf4j
public class ServiceImplGenerator {
    private final Template template;
    private final PackageConfig packageConfig;
    private final Path codePath;
    private final Table table;

    public ServiceImplGenerator(PackageConfig packageConfig, Path codePath, Table table) throws IOException {
        String templateName = GlobalConfigHolder.enableKotlin ? "serviceImpl.kt" : "serviceImpl";
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

        if (GlobalConfigHolder.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl");
            imports.add(getFullPackage(PackageConfig::getMapper) + "." + StrUtil.toPascalCase(table.getTableName()) + "Mapper");
            imports.add(getFullPackage(PackageConfig::getModel) + "." + StrUtil.toPascalCase(table.getTableName()));
            imports.add(getFullPackage(PackageConfig::getService) + "." + StrUtil.toPascalCase(table.getTableName()) + "Service");
        }

        return imports;
    }

    private String getFullPackage(Function<PackageConfig, String> function) {
        if (null != packageConfig.getOrigin() && !packageConfig.getOrigin().isEmpty()) {
            return packageConfig.getOrigin() + "." + function.apply(packageConfig);
        } else {
            return function.apply(packageConfig);
        }
    }

    private List<String> getAnnotations() {
        List<String> annotations = new ArrayList<>();
        return annotations;
    }

    private String getFlieName() {
        return StrUtil.toPascalCase(table.getTableName()) + "ServiceImpl";
    }

    protected String getPackagePath() {
        return getFullPackage(PackageConfig::getServiceImpl);
    }

    private GeneratorProps<Properties> getData() {

        String className = StrUtil.toPascalCase(table.getTableName() + "_service_impl");

        Properties data = new Properties();
        data.setProperty("frameworkName", GlobalConfigHolder.repositoryFramework.name());
        data.setProperty("modelClassName", StrUtil.toPascalCase(table.getTableName()));
        data.setProperty("mapperClassName", StrUtil.toPascalCase(table.getTableName()) + "Mapper");
        data.setProperty("interfaceName", StrUtil.toPascalCase(table.getTableName()) + "Service");

        GeneratorProps.Builder<Properties> builder = GeneratorProps.builder();
        builder.packagePath(getPackagePath())
                .imports(getImports())
                .annotations(getAnnotations())
                .classComment(getFlieName())
                .className(className)
                .data(data);

        return builder.build();
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
        if (!modelDirPath.toFile().exists())
            modelDirPath.toFile().mkdirs();
        // 输出的文件路径
        Path modelFilePath = modelDirPath.resolve(getFlieName() +
                (GlobalConfigHolder.enableKotlin ? ".kt" : ".java"));

        this.write(getData(), modelFilePath.toFile());
    }
}
