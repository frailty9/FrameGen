package org.framegen.core.db.sql;

public class SqlProviderFactory {

    public static AbstractSqlProvider getSqlProvider(String databaseProductName) {
        switch (databaseProductName) {
            case "MySQL":
                return new MySqlProvider();
        }
        return null;
    }
}
