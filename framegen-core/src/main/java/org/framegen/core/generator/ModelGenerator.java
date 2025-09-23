package org.framegen.core.generator;

import lombok.extern.slf4j.Slf4j;

import org.framegen.config.FrameworkConfig;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.core.model.Column;
import org.framegen.core.model.Table;
import org.framegen.util.StrUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class ModelGenerator extends AbstractGenerator<Collection<Column>> {

    public ModelGenerator(PackageConfig packageConfig, FrameworkConfig frameworkConfig, Path codePath, Table table) throws IOException {
        super("model", packageConfig, frameworkConfig, codePath, table);
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
        if (GlobalConfigHolder.enableLombok) {
            imports.add("lombok.Data");
            imports.add("lombok.Builder");
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
        if (GlobalConfigHolder.enableLombok) {
            annotations.add("Data");
            annotations.add("Builder");
        }
        return annotations;
    }

    @Override
    protected String getClassName() {
        return table.getPascalCaseName();
    }

    @Override
    protected String getPackagePath() {
        if (null != packageConfig.getOrigin() && !packageConfig.getOrigin().isEmpty()) {
            return packageConfig.getOrigin() + "." + packageConfig.getModel();
        } else {
            return packageConfig.getModel();
        }
    }

    @Override
    protected Collection<Column> getMoreData() {
        return table.getColumns();
    }
}
