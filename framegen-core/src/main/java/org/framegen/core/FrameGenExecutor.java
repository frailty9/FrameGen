package org.framegen.core;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.framegen.config.FrameworkConfig;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.NamingSuffixConfig;
import org.framegen.config.PackageConfig;
import org.framegen.config.RepositoryFrameworkEnum;
import org.framegen.core.generator.*;
import org.framegen.core.entity.Table;

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

    public FrameGenExecutor(PackageConfig packageConfig, NamingSuffixConfig namingSuffixConfig, FrameworkConfig frameworkConfig, Path outRootPath) {
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

                // 生成Entity
                createEntity(table);
                // 生成数据层
                if (null != packageConfig.getMapper()) {
                    createMapper(table);
                    // 生成Mybatis映射文件
                    if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS ||
                            frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
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

    protected void createEntity(Table table) throws IOException, TemplateException {
        EntityGenerator entityGenerator = new EntityGenerator(packageConfig, namingSuffixConfig.getEntity(), frameworkConfig, codePath, table);
        entityGenerator.generate();
    }

    protected void createMapper(Table table) throws IOException, TemplateException {
        MapperGenerator mapperGenerator = new MapperGenerator(packageConfig, namingSuffixConfig.getPersistence(), frameworkConfig, codePath, table);
        mapperGenerator.generate();
    }

    protected void createMapperXml(Table table) throws IOException, TemplateException {
        MapperXmlGenerator mapperXmlGenerator = new MapperXmlGenerator(packageConfig, namingSuffixConfig.getPersistence(), frameworkConfig, resourcePath, table);
        mapperXmlGenerator.generate();
    }

    protected void createService(Table table) throws IOException, TemplateException {
        ServiceGenerator serviceGenerator = new ServiceGenerator(packageConfig, namingSuffixConfig.getService(), frameworkConfig, codePath, table);
        serviceGenerator.generate();
    }

    protected void createServiceImpl(Table table) throws IOException, TemplateException {
        ServiceImplGenerator serviceImplGenerator = new ServiceImplGenerator(packageConfig, namingSuffixConfig.getServiceImpl(), frameworkConfig, codePath, table);
        serviceImplGenerator.generate();
    }
}
