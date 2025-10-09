package org.framegen.core.generator;

import lombok.extern.slf4j.Slf4j;
import org.framegen.config.FrameworkConfig;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.core.model.Column;
import org.framegen.core.model.Table;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public class ModelGenerator extends AbstractGenerator<Collection<Column>> {

    public ModelGenerator(PackageConfig packageConfig, String classNameSuffix, FrameworkConfig frameworkConfig, Path codePath, Table table) throws IOException {
        super("model", classNameSuffix, frameworkConfig, codePath, table, packageConfig);
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();
        table.getColumns().forEach(column -> {
            String codeType = column.getDataType();
            switch (codeType) {
                case "BigDecimal":
                    imports.add("java.math.BigDecimal");
                    break;
                case "LocalDate":
                    imports.add("java.time.LocalDate");
                    break;
                case "LocalTime":
                    imports.add("java.time.LocalTime");
                    break;
                case "LocalDateTime":
                    imports.add("java.time.LocalDateTime");
                    break;
                default:
                    break;
            }
            if (GlobalConfigHolder.enableKotlin) {
                switch (codeType) {

                }
            } else {
                switch (codeType) {

                }
            }
        });
        if (GlobalConfigHolder.enableLombok && !GlobalConfigHolder.enableKotlin) {
            imports.add("lombok.AllArgsConstructor");
            imports.add("lombok.Data");
            imports.add("lombok.Builder");
            imports.add("lombok.NoArgsConstructor");
        }

        return imports;
    }

    @Override
    protected String getClassComment() {
        String tableComment;
        if (null != table.getTableComment() && !table.getTableComment().isEmpty()) {
            tableComment = table.getTableComment();
        } else {
            tableComment = table.getTableName();
        }
        return tableComment;
    }

    @Override
    protected List<String> getAnnotations() {
        List<String> annotations = new ArrayList<>();
        if (GlobalConfigHolder.enableLombok && !GlobalConfigHolder.enableKotlin) {
            annotations.add("Data");
            annotations.add("Builder");
            annotations.add("AllArgsConstructor");
            annotations.add("NoArgsConstructor");
        }
        return annotations;
    }

    @Override
    protected String getPackagePath() {
        return getFullPackage(PackageConfig::getModel);
    }

    @Override
    protected Collection<Column> getMoreData() {
        return table.getColumns();
    }
}
