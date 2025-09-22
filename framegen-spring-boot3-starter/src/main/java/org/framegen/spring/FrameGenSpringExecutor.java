package org.framegen.spring;

import freemarker.template.TemplateException;
import org.framegen.config.FrameworkConfig;
import org.framegen.config.PackageConfig;
import org.framegen.core.FrameGenExecutor;
import org.framegen.core.model.Table;
import org.framegen.spring.generator.SpringMapperGenerator;
import org.framegen.spring.generator.SpringServiceImplGenerator;

import java.io.IOException;
import java.nio.file.Path;

public class FrameGenSpringExecutor extends FrameGenExecutor {

    public FrameGenSpringExecutor(PackageConfig packageConfig, FrameworkConfig frameworkConfig, Path outRootPath) {
        super(packageConfig, frameworkConfig, outRootPath);
    }

    @Override
    protected void createMapper(Table table) throws IOException, TemplateException {
        new SpringMapperGenerator(this.packageConfig, this.frameworkConfig, this.codePath, table).generate();
    }

    @Override
    protected void createServiceImpl(Table table) throws IOException, TemplateException {
        new SpringServiceImplGenerator(this.packageConfig, this.frameworkConfig, this.codePath, table).generate();
    }
}
