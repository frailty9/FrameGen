package org.framegen.core;

import lombok.extern.slf4j.Slf4j;
import org.framegen.config.PackageConfig;
import org.framegen.config.JdbcConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.sql.DataSource;

@Slf4j
public class FrameGenEntry extends AbstractEntry<FrameGenEntry> {

    protected Collection<String> includes = new ArrayList<>();
    protected Collection<String> excludes = new ArrayList<>();
    protected String outModuleName;
    protected PackageConfig packageConfig;
    protected boolean enableMybatis = false;
    protected boolean enableMybatisPlus = false;

    // 传入连接配置的构造方法
    public FrameGenEntry(JdbcConfig jdbcConfig) {
        super(jdbcConfig);
    }

    // 传入数据源的构造方法
    public FrameGenEntry(DataSource dataSource) {
        super(dataSource);
    }

    public FrameGenEntry(Map<String, DataSource> dataSourceMap, String dataSourceName) {
        super(dataSourceMap, dataSourceName);
    }

    public static FrameGenEntry create(JdbcConfig jdbcConfig) {
        return new FrameGenEntry(jdbcConfig);
    }

    public static FrameGenEntry create(DataSource dataSource) {
        return new FrameGenEntry(dataSource);
    }

    @Override
    public FrameGenEntry self() {
        return this;
    }
}
