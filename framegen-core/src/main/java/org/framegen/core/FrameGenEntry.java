package org.framegen.core;

import lombok.extern.slf4j.Slf4j;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.config.RepositoryFrameworkEnum;
import org.framegen.core.db.converter.AbstractTypeConverter;
import org.framegen.core.db.converter.ConverterFactory;
import org.framegen.core.db.DataSourceHolder;
import org.framegen.core.db.Query;
import org.framegen.core.db.impl.HikariDataSourceGetter;
import org.framegen.config.JdbcConfig;
import org.framegen.core.file.FileUtil;
import org.framegen.core.generator.*;
import org.framegen.core.model.Column;
import org.framegen.core.model.Table;

import javax.sql.DataSource;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FrameGenEntry {

    protected Collection<String> includes = new ArrayList<>();
    protected Collection<String> excludes = new ArrayList<>();
    protected String outModuleName;
    protected PackageConfig packageConfig;
    protected boolean enableMybatis = false;
    protected boolean enableMybatisPlus = false;

    // 传入连接配置的构造方法
    public FrameGenEntry(JdbcConfig jdbcConfig) {
        DataSourceHolder.setDataSource(new HikariDataSourceGetter(jdbcConfig).getDataSource());
    }

    // 传入数据源的构造方法
    public FrameGenEntry(DataSource dataSource) {
        DataSourceHolder.setDataSource(dataSource);
    }

    public static FrameGenEntry create(JdbcConfig jdbcConfig) {
        return new FrameGenEntry(jdbcConfig);
    }

    public static FrameGenEntry create(DataSource dataSource) {
        return new FrameGenEntry(dataSource);
    }

    public FrameGenEntry includes(Collection<String> tableNames) {
        this.includes = tableNames;
        return this;
    }

    public FrameGenEntry includes(String... tableNames) {
        this.includes = new ArrayList<>();
        Collections.addAll(this.includes, tableNames);
        return this;
    }

    public FrameGenEntry excludes(Collection<String> tableNames) {
        this.excludes = tableNames;
        return this;
    }

    public FrameGenEntry excludes(String... tableNames) {
        this.excludes = new ArrayList<>();
        Collections.addAll(this.excludes, tableNames);
        return this;
    }

    public FrameGenEntry outModule(String outModuleName) {
        this.outModuleName = outModuleName;
        return this;
    }

    public FrameGenEntry lombok() {
        GlobalConfigHolder.enableLombok = true;
        return this;
    }

    public FrameGenEntry kotlin() {
        GlobalConfigHolder.enableKotlin = true;
        return this;
    }

    public FrameGenEntry multiThread() {
        GlobalConfigHolder.enableMultiThread = true;
        return this;
    }

    public FrameGenEntry mybatis() {
        this.enableMybatis = true;
        return this;
    }

    public FrameGenEntry mybatisPlus() {
        this.enableMybatisPlus = true;
        return this;
    }

    public FrameGenEntry setPackage(Consumer<PackageConfig.Builder> consumer) {
        PackageConfig.Builder builder = PackageConfig.builder();
        consumer.accept(builder);
        this.packageConfig = builder.build();
        return this;
    }

    public <T> void run(Class<T> clazz) {
        // 设置默认包名
        setDefaultPackages();
        
        Path outRootPath = getOutputPath(clazz);
        FrameGenExecutor executor = new FrameGenExecutor(packageConfig, enableMybatis, enableMybatisPlus, outRootPath);

        try (Query query = new Query()) {
            List<Table> tables = query.getTables();
            if (null == tables || tables.isEmpty()) {
                log.warn("FrameGen: 未找到任何表，请检查数据库配置");
                return;
            }

            // 过滤表
            Stream<Table> stream = tables.stream();
            if (!this.includes.isEmpty()) {
                stream = stream.filter(table -> this.includes.contains(table.getTableName())
                        && !this.excludes.contains(table.getTableName()));
            }
            tables = stream.peek(table -> {
                try {
                    // 查询列信息并存放到表对象中
                    table.setColumns(query.getTableColumns(table.getTableName()));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

            executor.execute(tables);
        } catch (Exception e) {
            log.error("FrameGen: 生成失败", e);
            throw new RuntimeException(e);
        }
    }

    protected void setDefaultPackages() {
        if (null == packageConfig.getModel()) {
            packageConfig.setModel("model");
        }
        if (null == packageConfig.getMapper() && (enableMybatis || enableMybatisPlus)) {
            packageConfig.setMapper("mapper");
        }
        if (null == packageConfig.getService() && enableMybatisPlus) {
            packageConfig.setService("service");
        }
        if (null == packageConfig.getServiceImpl() && enableMybatisPlus) {
            packageConfig.setServiceImpl("service.impl");
        }
    }

    protected <T> Path getOutputPath(Class<T> clazz) {
        Path outRootPath;
        try {
            if (null == this.outModuleName || this.outModuleName.isEmpty()) {
                outRootPath = FileUtil.getModulePath(clazz);
            } else {
                outRootPath = FileUtil.getModulePath(this.outModuleName);
            }
        } catch (NullPointerException | URISyntaxException e) {
            outRootPath = Paths.get(System.getProperty("user.dir"));
        }
        
        if (null == outRootPath) {
            throw new NullPointerException("无法确定输出路径");
        }
        
        outRootPath = outRootPath.resolve("src").resolve("main");
        log.debug("FrameGen: outRootPath: {}", outRootPath);
        return outRootPath;
    }
}
