package org.framegen.core.db;

import lombok.extern.slf4j.Slf4j;
import org.framegen.core.model.Column;
import org.framegen.core.db.sql.AbstractSqlProvider;
import org.framegen.core.db.sql.SqlProviderFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class Query {

    private final AbstractSqlProvider sqlProvider;
    private final Connection connection;
    private String sql;
    private Object[] params;

    public Query() throws SQLException {
        this.connection = DataSourceHolder.getDataSource().getConnection();
        DatabaseMetaData metaData = this.connection.getMetaData();
        this.sqlProvider = SqlProviderFactory.getSqlProvider(metaData.getDatabaseProductName());
        metaData.getURL();
    }

    public List<String> getTableNames() throws SQLException {
        this.sql = this.sqlProvider.getTableNamesSql();
        this.params = null;
        List<String> tableNames = new ArrayList<>();
        execute(resultSet -> {
            try {
                tableNames.add(resultSet.getString(1));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return tableNames;
    }

    public List<Column> getTableColumns(String tableName) throws SQLException {
        this.sql = this.sqlProvider.getTableColumnsSql();
        this.params = new Object[]{tableName};
        List<Column> columns = new ArrayList<>();
        execute(rs -> {
            try {
                columns.add(Column.builder()
                        .fieldName(rs.getString("field_name"))
                        .defaultValue(rs.getString("default_value"))
                        .isNullable("YES".equals(rs.getString("is_nullable")))
                        .dataType(rs.getString("data_type"))
                        .columnKey(rs.getString("column_key"))
                        .extra(rs.getString("extra"))
                        .columnComment(rs.getString("column_comment"))
                        .build());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return columns;
    }

    public void execute(Consumer<ResultSet> consumer) throws SQLException {
        try (PreparedStatement pstatement = buildPreparedStatement();
             ResultSet resultSet = pstatement.executeQuery()) {
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        }
    }

    /* 构建PreparedStatement对象 */
    private PreparedStatement buildPreparedStatement() throws SQLException {
        log.debug("执行SQL语句: {}", sql);
        PreparedStatement pstatement = connection.prepareStatement(sql);
        if (null == params) {
            return pstatement;
        }
        for (int i = 0; i < params.length; i++) {
            pstatement.setObject(i + 1, params[i]);
        }
        return pstatement;
    }
}
