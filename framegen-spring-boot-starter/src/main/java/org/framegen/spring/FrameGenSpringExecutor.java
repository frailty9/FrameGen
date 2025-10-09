package org.framegen.spring;

import org.framegen.config.FrameworkConfig;
import org.framegen.config.NamingSuffixConfig;
import org.framegen.config.PackageConfig;
import org.framegen.core.FrameGenExecutor;

import java.nio.file.Path;

/**
 * 保留的拓展接口
 */
public class FrameGenSpringExecutor extends FrameGenExecutor {

    public FrameGenSpringExecutor(PackageConfig packageConfig, NamingSuffixConfig namingSuffixConfig, FrameworkConfig frameworkConfig, Path outRootPath) {
        super(packageConfig, namingSuffixConfig, frameworkConfig, outRootPath);
    }

    // @Override
    // protected void createMapper(Table table) throws IOException, TemplateException {
    //     new SpringMapperGenerator(this.packageConfig, this.frameworkConfig, this.codePath, table).generate();
    // }

    // @Override
    // protected void createServiceImpl(Table table) throws IOException, TemplateException {
    //     new SpringServiceImplGenerator(this.packageConfig, this.frameworkConfig, this.codePath, table).generate();
    // }
}
