package org.framegen.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

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
}
