package org.framegen.core;

import lombok.extern.slf4j.Slf4j;

import org.framegen.config.AppFrameworkEnum;
import org.framegen.config.JdbcConfig;

import java.util.Map;

import javax.sql.DataSource;

@Slf4j
public class FrameGenEntry extends AbstractEntry<FrameGenEntry> {

    public FrameGenEntry(JdbcConfig jdbcConfig) {
        super(jdbcConfig);
    }

    public FrameGenEntry(DataSource dataSource) {
        super(dataSource);
    }

    public FrameGenEntry(Map<String, DataSource> dataSourceMap, String dataSourceName) {
        super(dataSourceMap, dataSourceName, AppFrameworkEnum.NONE);
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

    @Override
    protected Class<? extends FrameGenExecutor> getExecutorClass() {
        return FrameGenExecutor.class;
    }
}
