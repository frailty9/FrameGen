package org.framegen.spring.generator;

import org.framegen.config.FrameworkConfig;
import org.framegen.config.PackageConfig;
import org.framegen.core.generator.ServiceImplGenerator;
import org.framegen.core.model.Table;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SpringServiceImplGenerator extends ServiceImplGenerator {

    public SpringServiceImplGenerator(PackageConfig packageConfig, FrameworkConfig frameworkConfig, Path codePath, Table table) throws IOException {
        super(packageConfig, frameworkConfig, codePath, table);
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = super.getImports();
        imports.add("org.springframework.stereotype.Service");
        return imports;
    }

    @Override
    protected List<String> getAnnotations() {
        List<String> annotations = super.getAnnotations();
        annotations.add("Service");
        return annotations;
    }
}
