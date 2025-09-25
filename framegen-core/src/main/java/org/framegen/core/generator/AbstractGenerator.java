package org.framegen.core.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import org.framegen.config.FrameworkConfig;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.core.generator.props.GeneratorProps;
import org.framegen.core.entity.Table;
import org.framegen.util.StrUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class AbstractGenerator<E> {

    // FreeMarker模板
    protected final Template template;
    // 包配置
    protected final PackageConfig packageConfig;
    // 框架配置
    protected final FrameworkConfig frameworkConfig;
    // 代码输出路径
    protected final Path codePath;
    // 表信息
    protected final Table table;
    // 类名后缀
    protected final String classNameSuffix;

    public AbstractGenerator(String baseTemplateName, String classNameSuffix, FrameworkConfig frameworkConfig,
            Path codePath, Table table, PackageConfig packageConfig)
            throws IOException {
        // 拼接模板文件名
        StringBuilder templateName = new StringBuilder().append(baseTemplateName);
        if (!"mapper.xml".equals(baseTemplateName) && GlobalConfigHolder.enableKotlin) {
            templateName.append(".kt");
        }
        templateName.append(".ftl");

        // 创建FreeMarker配置
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        // 使用类加载器加载模板
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        template = cfg.getTemplate(templateName.toString());

        this.classNameSuffix = classNameSuffix;
        this.packageConfig = packageConfig;
        this.frameworkConfig = frameworkConfig;
        this.codePath = codePath;
        this.table = table;
    }

    /**
     * 工具方法, 获取指定包的完整包路径
     *
     * @param function 传入PackageConfig属性的Getter
     * @return 完整包路径
     */
    protected String getFullPackage(Function<PackageConfig, String> function) {
        if (null != packageConfig.getOrigin() && !packageConfig.getOrigin().isEmpty()) {
            return packageConfig.getOrigin() + "." + function.apply(packageConfig);
        } else {
            return function.apply(packageConfig);
        }
    }

    // 生成代码的导入部分
    protected abstract List<String> getImports();

    // 生成代码的注释部分, 默认使用类名作为注释
    protected String getClassComment() {
        return getClassName();
    }

    // 生成代码的注解部分
    protected abstract List<String> getAnnotations();

    // 生成代码的类名
    protected String getClassName() {
        return StrUtil.removePrefix(table.getPascalCaseName()) + classNameSuffix;
    };

    // 生成代码的包路径
    protected abstract String getPackagePath();

    // 额外的数据, 默认为空, 在模板中引用时注意在属性'data'中, 不会自动解包到最上层
    protected E getMoreData() {
        return null;
    }

    // 输出代码
    protected void write(Object data, File outFile) throws TemplateException, IOException {
        Writer writer = new FileWriter(outFile);
        template.process(data, writer);
        writer.flush();
    }

    // 整理数据并准备输出
    public void generate() throws TemplateException, IOException {

        Path entityDirPath = codePath.resolve(
                getPackagePath().replace(".", File.separator));

        // 创建输出目录
        if (!entityDirPath.toFile().exists())
            entityDirPath.toFile().mkdirs();
        // 输出的文件路径
        Path entityFilePath = entityDirPath.resolve(getClassName() +
                (GlobalConfigHolder.enableKotlin ? ".kt" : ".java"));

        // 整理数据
        GeneratorProps.Builder<E> dataBuilder = GeneratorProps.builder();
        dataBuilder.packagePath(getPackagePath())
                .imports(getImports())
                .annotations(getAnnotations())
                .classComment(getClassName())
                .className(getClassName())
                .data(getMoreData());

        this.write(dataBuilder.build(), entityFilePath.toFile());
    }
}
