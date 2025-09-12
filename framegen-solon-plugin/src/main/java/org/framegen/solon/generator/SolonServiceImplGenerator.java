package org.framegen.solon.generator;

import org.framegen.config.PackageConfig;
import org.framegen.core.generator.ServiceImplGenerator;
import org.framegen.core.model.Table;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SolonServiceImplGenerator extends ServiceImplGenerator {

    public SolonServiceImplGenerator(PackageConfig packageConfig, Path codePath, Table table) throws IOException {
        super(packageConfig, codePath, table);
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = super.getImports();
        imports.add("org.noear.solon.annotation.Component");
        return imports;
    }

    @Override
    protected List<String> getAnnotations() {
        List<String> annotations = super.getAnnotations();
        annotations.add("Component");
        return annotations;
    }
}
