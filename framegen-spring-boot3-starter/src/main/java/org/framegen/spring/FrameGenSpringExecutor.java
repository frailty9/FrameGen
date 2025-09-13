package org.framegen.spring;

import freemarker.template.TemplateException;
import org.framegen.config.PackageConfig;
import org.framegen.core.FrameGenExecutor;
import org.framegen.core.model.Table;
import org.framegen.spring.generator.SpringMapperGenerator;
import org.framegen.spring.generator.SpringServiceImplGenerator;

import java.io.IOException;
import java.nio.file.Path;

public class FrameGenSpringExecutor extends FrameGenExecutor {

    public FrameGenSpringExecutor(PackageConfig packageConfig, boolean enableMybatis, boolean enableMybatisPlus, Path outRootPath) {
        super(packageConfig, enableMybatis, enableMybatisPlus, outRootPath);
    }

    @Override
    protected void createMapper(Table table) throws IOException, TemplateException {
        new SpringMapperGenerator(this.packageConfig, this.outRootPath, table).generate();
    }

    @Override
    protected void createServiceImpl(Table table) throws IOException, TemplateException {
        new SpringServiceImplGenerator(this.packageConfig, this.outRootPath, table).generate();
    }
}
