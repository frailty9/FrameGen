package org.framegen.core.db;

import javax.sql.DataSource;

@FunctionalInterface
public interface DataSourceFactory {
    DataSource getDataSource();
}
