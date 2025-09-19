package org.framegen.core;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.config.RepositoryFrameworkEnum;
import org.framegen.core.generator.*;
import org.framegen.core.model.Table;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class FrameGenExecutor {
    protected final PackageConfig packageConfig;
    private final boolean enableMybatis;
    private final boolean enableMybatisPlus;
    protected final Path outRootPath;
    protected final Path codePath;
    protected final Path resourcePath;

    public FrameGenExecutor(PackageConfig packageConfig, boolean enableMybatis,
                            boolean enableMybatisPlus, Path outRootPath) {
        this.packageConfig = packageConfig;
        this.enableMybatis = enableMybatis;
        this.enableMybatisPlus = enableMybatisPlus;
        this.outRootPath = outRootPath;
        this.codePath = outRootPath.resolve(GlobalConfigHolder.enableKotlin ? "kotlin" : "java");
        this.resourcePath = outRootPath.resolve("resources");
    }

    public void execute(List<Table> tables) {
        try {
            // 设置全局持久层框架
            if (enableMybatisPlus) {
                GlobalConfigHolder.repositoryFramework = RepositoryFrameworkEnum.MYBATIS_PLUS;
            } else if (enableMybatis) {
                GlobalConfigHolder.repositoryFramework = RepositoryFrameworkEnum.MYBATIS;
            } else {
                GlobalConfigHolder.repositoryFramework = RepositoryFrameworkEnum.NATIVE_JDBC;
            }

            log.info("FrameGen: 输出路径: {}", outRootPath);
            log.debug("FrameGen: 代码路径: {}", codePath);

            for (Table table : tables) {
                log.info("FrameGen: 正在生成表: {}", table.getTableName());

                // 生成Model
                createModel(table);
                // 生成数据层
                if (null != packageConfig.getMapper()) {
                    createMapper(table);
                    // 生成Mybatis映射文件
                    if (this.enableMybatis || this.enableMybatisPlus) {
                        createMapperXml(table);
                    }
                }
                // 生成服务层
                if (null != packageConfig.getService()) {
                    createService(table);
                    // 生成服务实现类
                    if (null != packageConfig.getServiceImpl()) {
                        createServiceImpl(table);
                    }
                }
            }
        } catch (Exception e) {
            log.error("FrameGen: 发生错误: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    protected void createModel(Table table) throws IOException, TemplateException {
        ModelGenerator modelGenerator = new ModelGenerator(packageConfig, codePath, table);
        modelGenerator.generate();
    }

    protected void createMapper(Table table) throws IOException, TemplateException {
        MapperGenerator mapperGenerator = new MapperGenerator(packageConfig, codePath, table);
        mapperGenerator.generate();
    }

    protected void createMapperXml(Table table) throws IOException, TemplateException {
        MapperXmlGenerator mapperXmlGenerator = new MapperXmlGenerator(packageConfig, resourcePath, table);
        mapperXmlGenerator.generate();
    }

    protected void createService(Table table) throws IOException, TemplateException {
        ServiceGenerator serviceGenerator = new ServiceGenerator(packageConfig, codePath, table);
        serviceGenerator.generate();
    }

    protected void createServiceImpl(Table table) throws IOException, TemplateException {
        ServiceImplGenerator serviceImplGenerator = new ServiceImplGenerator(packageConfig, codePath, table);
        serviceImplGenerator.generate();
    }
}
