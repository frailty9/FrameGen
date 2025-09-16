package org.framegen.core.db.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.framegen.core.service.DataSourceFactory;
import org.framegen.config.JdbcConfig;

import javax.sql.DataSource;
import java.util.Properties;

public class HikariDataSourceGetter implements DataSourceFactory<DataSource> {

    private final Properties configProps;

    /**
     * 如果您的项目原本就使用HikariCP作为数据库连接池,
     * 那么您能够直接传入HikariCP的配置信息,
     * @param configProps HikariCP的配置信息
     */
    public HikariDataSourceGetter(Properties configProps) {
        this.configProps = configProps;
    }

    public HikariDataSourceGetter(JdbcConfig jdbcConfig) {
        this(jdbcConfig.toProperties());
    }

    @Override
    public DataSource getDataSource() {
        HikariConfig config = new HikariConfig(this.configProps);
        return new HikariDataSource(config);
    }
}
