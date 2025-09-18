package org.framegen.core.db.converter;

import org.framegen.core.db.DatabaseProduct;

public class ConverterFactory {

    public static AbstractTypeConverter getConverter(DatabaseProduct databaseProduct) {
        switch (databaseProduct) {
            case MYSQL:
                return new MySqlConverter();
            default:
                // 理论上不会执行，但保留以防将来添加新枚举值
                throw new IllegalStateException("未实现的数据库类型: " + databaseProduct);
        }
    }
}
