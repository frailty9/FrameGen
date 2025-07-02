package org.framegen.core.db;

import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;

public class DataSourceHolder {

    @Setter
    @Getter
    private static DataSource dataSource;
}
