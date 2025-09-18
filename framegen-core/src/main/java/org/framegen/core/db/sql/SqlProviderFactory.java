package org.framegen.core.db.sql;

import org.framegen.core.db.DatabaseProduct;

public class SqlProviderFactory {

    public static AbstractSqlProvider getSqlProvider(DatabaseProduct databaseProduct) {
        switch (databaseProduct) {
            case MYSQL:
                return new MySqlProvider();
            default:
            // 理论上不会执行，但保留以防将来添加新枚举值
            throw new IllegalStateException("未实现的数据库类型: " + databaseProduct);
        }
    }
}
