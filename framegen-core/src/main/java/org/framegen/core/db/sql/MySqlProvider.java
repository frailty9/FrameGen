package org.framegen.core.db.sql;

public class MySqlProvider extends AbstractSqlProvider {

    @Override
    public String getTableNamesSql() {
        return "SELECT TABLE_NAME table_name, TABLE_SCHEMA table_schema, " +
                "TABLE_COMMENT table_comment\n" +
                "FROM information_schema.TABLES\n" +
                "WHERE TABLE_SCHEMA = (SELECT DATABASE());";
    }

    @Override
    public String getTableColumnsSql() {
        return "SELECT TABLE_NAME table_name, COLUMN_NAME field_name, " +
                "COLUMN_DEFAULT default_value, IS_NULLABLE is_nullable, " +
                "DATA_TYPE data_type, COLUMN_KEY column_key, EXTRA extra, " +
                "COLUMN_COMMENT column_comment " +
                "FROM information_schema.columns " +
                "WHERE TABLE_NAME = ? AND TABLE_SCHEMA = (SELECT DATABASE()) " +
                "ORDER BY ORDINAL_POSITION ASC;";
    }

}
