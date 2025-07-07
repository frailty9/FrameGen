package org.framegen.core.db.converter;

public class ConverterFactory {

    public static AbstractTypeConverter getConverter(String databaseProductName) {
        switch (databaseProductName) {
            case "MySQL":
                return new MySqlConverter();
        }
        return null;
    }

}
