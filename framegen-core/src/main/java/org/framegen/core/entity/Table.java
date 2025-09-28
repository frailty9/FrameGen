package org.framegen.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.framegen.util.StrUtil;

import java.util.Collection;
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
    Collection<Column> columns;

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
}
