package org.framegen.core.db;

import lombok.extern.slf4j.Slf4j;
import org.framegen.core.db.converter.AbstractTypeConverter;
import org.framegen.core.db.converter.ConverterFactory;
import org.framegen.core.db.sql.AbstractSqlProvider;
import org.framegen.core.db.sql.SqlProviderFactory;
import org.framegen.core.model.Column;
import org.framegen.core.model.Table;
import org.framegen.core.service.DataSourceHolder;
import org.framegen.util.StrUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class Query implements AutoCloseable {

    private final AbstractSqlProvider sqlProvider;
    private final AbstractTypeConverter typeConverter;
    private final Connection connection;
    private String sql;
    private Object[] params;

    public Query() throws SQLException {
        this.connection = DataSourceHolder.getDataSource().getConnection();
        DatabaseMetaData metaData = this.connection.getMetaData();
        DatabaseProduct databaseProduct = DatabaseProduct.fromProductName(metaData.getDatabaseProductName());
        this.sqlProvider = SqlProviderFactory.getSqlProvider(databaseProduct);
        this.typeConverter = ConverterFactory.getConverter(databaseProduct);
    }

    public List<Table> getTables() throws SQLException {
        this.sql = this.sqlProvider.getTableNamesSql();
        this.params = null;
        List<Table> tables = new ArrayList<>();
        execute(resultSet -> {
            try {
                tables.add(Table.builder()
                        .databaseProductName(this.connection.getMetaData().getDatabaseProductName())
                        .tableSchema(resultSet.getString("table_schema"))
                        .tableName(resultSet.getString("table_name"))
                        .tableComment(resultSet.getString("table_comment"))
                        .build()
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return tables;
    }

    public List<Column> getTableColumns(String tableName) throws SQLException {
        this.sql = this.sqlProvider.getTableColumnsSql();
        this.params = new Object[]{tableName};
        List<Column> columns = new ArrayList<>();
        execute(rs -> {
            try {
                // 类型转换
                String dbDataType = rs.getString("data_type");
                String codeDataType = this.typeConverter.converterToCodeType(dbDataType);

                columns.add(Column.builder()
                        .fieldName(rs.getString("field_name"))
                        .variableName(StrUtil.toCamelCase(rs.getString("field_name")))
                        .defaultValue(rs.getString("default_value"))
                        .isNullable("YES".equals(rs.getString("is_nullable")))
                        .dataType(codeDataType)
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
            log.debug("FramGen: 执行SQL语句 => {} \n <= SQL END, 参数: {}", sql, params);
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        }
    }

    /* 构建PreparedStatement对象 */
    private PreparedStatement buildPreparedStatement() throws SQLException {
        PreparedStatement pstatement = connection.prepareStatement(sql);
        if (null == params) {
            return pstatement;
        }
        for (int i = 0; i < params.length; i++) {
            pstatement.setObject(i + 1, params[i]);
        }
        return pstatement;
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }
}
