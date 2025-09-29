package org.framegen.core;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.framegen.config.FrameworkConfig;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.NamingSuffixConfig;
import org.framegen.config.PackageConfig;
import org.framegen.core.generator.DaoGenerator;
import org.framegen.core.model.Table;
import org.framegen.core.generator.ModelGenerator;
import org.framegen.core.generator.MapperGenerator;
import org.framegen.core.generator.MapperXmlGenerator;
import org.framegen.core.generator.ServiceGenerator;
import org.framegen.core.generator.ServiceImplGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class FrameGenExecutor {
    protected final PackageConfig packageConfig;
    protected final FrameworkConfig frameworkConfig;
    protected final NamingSuffixConfig namingSuffixConfig;
    protected Path outRootPath;
    protected Path codePath;
    protected Path resourcePath;

    public FrameGenExecutor(PackageConfig packageConfig, NamingSuffixConfig namingSuffixConfig,
                            FrameworkConfig frameworkConfig, Path outRootPath) {
        this.packageConfig = packageConfig;
        this.frameworkConfig = frameworkConfig;
        this.namingSuffixConfig = namingSuffixConfig;
        setOutRootPath(outRootPath);
    }

    public void setOutRootPath(Path outRootPath) {
        this.outRootPath = outRootPath;
        this.codePath = outRootPath.resolve(GlobalConfigHolder.enableKotlin ? "kotlin" : "java");
        this.resourcePath = outRootPath.resolve("resources");
    }

    public void execute(List<Table> tables) {
        try {

            log.info("FrameGen: 输出路径: {}", outRootPath);
            log.debug("FrameGen: 代码路径: {}", codePath);

            for (Table table : tables) {
                log.info("FrameGen: 正在生成表: {}", table.getTableName());

                // 生成Model
                generateModel(table);

                // 生成数据层
                if (null != packageConfig.getDao()) {

                    switch (frameworkConfig.repositoryFramework) {
                        case NATIVE_JDBC:
                            // 生成Dao
//                            generateDao(table);
                            break;

                        case MYBATIS:
                        case MYBATIS_PLUS:
                            generateMapper(table);
                            generateMapperXml(table);
                            break;
                    }
                }
                // 生成服务层
                if (null != packageConfig.getService()) {
                    generateService(table);
                    // 生成服务实现类
                    if (null != packageConfig.getServiceImpl()) {
                        generateServiceImpl(table);
                    }
                }
            }
        } catch (Exception e) {
            log.error("FrameGen: 发生错误: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    protected void generateModel(Table table) throws IOException, TemplateException {
        ModelGenerator modelGenerator = new ModelGenerator(packageConfig, namingSuffixConfig.getModel(),
                frameworkConfig, codePath, table);
        modelGenerator.generate();
    }

    protected void generateDao(Table table) throws IOException, TemplateException {
        DaoGenerator daoGenerator = new DaoGenerator(packageConfig, namingSuffixConfig.getDao(),
                frameworkConfig, codePath, table);
        daoGenerator.generate();
    }

    protected void generateMapper(Table table) throws IOException, TemplateException {
        MapperGenerator mapperGenerator = new MapperGenerator(packageConfig, namingSuffixConfig.getDao(),
                frameworkConfig, codePath, table);
        mapperGenerator.generate();
    }

    protected void generateMapperXml(Table table) throws IOException, TemplateException {
        MapperXmlGenerator mapperXmlGenerator = new MapperXmlGenerator(packageConfig,
                namingSuffixConfig.getDao(), frameworkConfig, resourcePath, table);
        mapperXmlGenerator.generate();
    }

    protected void generateService(Table table) throws IOException, TemplateException {
        ServiceGenerator serviceGenerator = new ServiceGenerator(packageConfig, namingSuffixConfig.getService(),
                frameworkConfig, codePath, table);
        serviceGenerator.generate();
    }

    protected void generateServiceImpl(Table table) throws IOException, TemplateException {
        ServiceImplGenerator serviceImplGenerator = new ServiceImplGenerator(packageConfig,
                namingSuffixConfig.getServiceImpl(), frameworkConfig, codePath, table);
        serviceImplGenerator.generate();
    }
}
