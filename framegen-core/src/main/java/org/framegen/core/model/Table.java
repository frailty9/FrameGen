package org.framegen.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.framegen.util.StrUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Table {

    private String databaseProductName;
    private String tableSchema;
    private String tableName;
    private String tableComment;
    private Collection<Column> columns;
    private Collection<String> typeImports;

    public Table(String databaseProductName, String tableSchema, String tableName, String tableComment) {
        this.databaseProductName = databaseProductName;
        this.tableSchema = tableSchema;
        this.tableName = tableName;
        this.tableComment = tableComment;
    }

    public String getPascalCaseName() {
        return StrUtil.toPascalCase(tableName);
    }

    public Column getPrimaryColumn() {
        return columns.stream()
                .filter(column -> column.getColumnKey().contains("PRI"))
                .collect(Collectors.toList()).get(0);
    }

    public void setColumns(Collection<Column> columns) {
        if (columns == null) {
            this.columns = new ArrayList<>();
        } else {
            this.columns = columns;
        }
        loadTypeImports();
    }

    private void loadTypeImports() {
        typeImports = new HashSet<>();
        columns.forEach(column -> {
            String codeType = column.getDataType();
            String importType = null;
            switch (codeType) {
                case "BigDecimal":
                    importType = "java.math.BigDecimal";
                    break;
                case "LocalDate":
                    importType = "java.time.LocalDate";
                    break;
                case "LocalTime":
                    importType = "java.time.LocalTime";
                    break;
                case "LocalDateTime":
                    importType = "java.time.LocalDateTime";
                    break;
                default:
                    break;
            }
            if (importType != null) {
                typeImports.add(importType);
            }
        });
    }
}
