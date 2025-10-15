package org.framegen.core.generator;

import org.framegen.config.FrameworkConfig;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.core.model.Column;
import org.framegen.core.model.Table;
import org.framegen.util.StrUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DaoGenerator extends AbstractGenerator<Map<String, Object>> {

    public DaoGenerator(PackageConfig packageConfig, String classNameSuffix, FrameworkConfig frameworkConfig, Path codePath, Table table) throws IOException {
        super("dao", classNameSuffix, frameworkConfig, codePath, table, packageConfig);
    }

    @Override
    protected void setImports() {
        imports.add("javax.sql.DataSource");
        if (null != table.getTypeImports()) {
            imports.addAll(table.getTypeImports());
        }
        if (!GlobalConfigHolder.enableKotlin) {
            imports.add("java.sql.Connection");
            imports.add("java.sql.PreparedStatement");
            imports.add("java.sql.ResultSet");
            imports.add("java.sql.SQLException");
            imports.add("java.sql.Statement");
            imports.add("java.util.ArrayList");
            imports.add("java.util.List");
        }
        imports.add(getFullPackage(PackageConfig::getModel) + "." + table.getPascalCaseName());
    }

    @Override
    protected List<String> getAnnotations() {
        List<String> annotations = new ArrayList<>();
        return annotations;
    }

    @Override
    protected String getPackagePath() {
        return getFullPackage(PackageConfig::getDao);
    }

    @Override
    protected Map<String, Object> getMoreData() {
        Map<String, Object> data = new HashMap<>();

        List<String> fieldNames = table.getColumns().stream()
                .map(Column::getFieldName)
                .collect(Collectors.toList());

        Column primaryColumn = table.getPrimaryColumn();

        /*
        data {
            tableName,
            modelClassName,
            modelFieldNames,
            modelFieldVarNames,
            modelFieldTypes,
            modelFieldGetters,
            modelFieldSetters,
            isAutoIncrement,
            primaryVarName
        }
         */
        data.put("tableName", table.getTableName());
        data.put("modelClassName", table.getPascalCaseName());
        data.put("modelFieldNames", fieldNames);
        data.put("modelFieldVarNames", table.getColumns().stream()
                .map(Column::getVariableName)
                .collect(Collectors.toList()));
        data.put("modelFieldTypes", table.getColumns().stream()
                .map(Column::getDataType)
                .collect(Collectors.toList()));
        data.put("modelFieldGetters", fieldNames.stream()
                .map(f -> "get" + StrUtil.toPascalCase(f))
                .collect(Collectors.toList()));
        data.put("modelFieldSetters", fieldNames.stream()
                .map(f -> "set" + StrUtil.toPascalCase(f))
                .collect(Collectors.toList()));
        data.put("isAutoIncrement", primaryColumn.getExtra().contains("auto_increment"));
        data.put("primaryVarName", primaryColumn.getVariableName());

        return data;
    }
}
