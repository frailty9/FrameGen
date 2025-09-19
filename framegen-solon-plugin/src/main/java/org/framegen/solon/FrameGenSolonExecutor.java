package org.framegen.solon;

import freemarker.template.TemplateException;
import org.framegen.config.PackageConfig;
import org.framegen.core.FrameGenExecutor;
import org.framegen.core.model.Table;
import org.framegen.solon.generator.SolonServiceImplGenerator;

import java.io.IOException;
import java.nio.file.Path;


public class FrameGenSolonExecutor extends FrameGenExecutor {

    public FrameGenSolonExecutor(PackageConfig packageConfig, boolean enableMybatis, boolean enableMybatisPlus, Path outRootPath) {
        super(packageConfig, enableMybatis, enableMybatisPlus, outRootPath);
    }

    @Override
    protected void createServiceImpl(Table table) throws IOException, TemplateException {
        new SolonServiceImplGenerator(this.packageConfig, this.codePath, table).generate();
    }
}
