package org.framegen.core.db.converter;

import lombok.extern.slf4j.Slf4j;
import org.framegen.config.GlobalConfigHolder;

@Slf4j
public class MySqlConverter implements AbstractTypeConverter {

    @Override
    public String converterToCodeType(String dbType) {
        if (null == dbType) {
            log.error("数据库类型异常");
            throw new IllegalArgumentException("DB type is null.");
        }
        
        String lowerType = dbType.toLowerCase();

        if (GlobalConfigHolder.enableKotlin) {
            switch (lowerType) {
                case "varchar":
                case "char":
                case "text":
                case "longtext":
                case "mediumtext":
                    return "String";
                case "int":
                case "integer":
                case "tinyint":
                    return "Int";
                case "bigint":
                    return "Long";
                case "decimal":
                case "numeric":
                    return "BigDecimal";
                case "date":
                    return "LocalDate";
                case "datetime":
                case "timestamp":
                    return "LocalDateTime";
                case "boolean":
                    return "Boolean";
                case "float":
                    return "Float";
                case "double":
                    return "Double";
                default:
                    return "Any";
            }
        } else {
            switch (lowerType) {
                case "varchar":
                case "char":
                case "text":
                case "longtext":
                case "mediumtext":
                    return "String";
                case "int":
                case "integer":
                case "tinyint":
                    return "Integer";
                case "bigint":
                    return "Long";
                case "decimal":
                case "numeric":
                    return "BigDecimal";
                case "date":
                    return "LocalDate";
                case "datetime":
                case "timestamp":
                    return "LocalDateTime";
                case "boolean":
                    return "Boolean";
                case "float":
                    return "Float";
                case "double":
                    return "Double";
                default:
                    return "Object";
            }
        }
    }
}
