package org.framegen.spring;

import lombok.extern.slf4j.Slf4j;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.core.db.DataSourceHolder;
import org.framegen.core.db.Query;
import org.framegen.core.file.FileUtil;
import org.framegen.core.model.Table;
import org.framegen.spring.db.SpringDataSourceGetter;
import org.framegen.spring.util.SpringContextHolder;
import org.springframework.context.ApplicationContext;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FrameGenSpringBootEntry {
    private Collection<String> includes = new ArrayList<>();
    private Collection<String> excludes = new ArrayList<>();
    private String outModuleName;
    private PackageConfig packageConfig;
    private boolean enableMybatis = false;
    private boolean enableMybatisPlus = false;

    public FrameGenSpringBootEntry(ApplicationContext context) {
        this(context, null);
    }

    public FrameGenSpringBootEntry(ApplicationContext context, String dataSourceName) {
        SpringContextHolder.setContext(context);
        SpringDataSourceGetter.setPreferredDataSourceName(dataSourceName);
        DataSourceHolder.setDataSource(new SpringDataSourceGetter().getDataSource());
    }

    public static FrameGenSpringBootEntry create(ApplicationContext context) {
        return new FrameGenSpringBootEntry(context);
    }

    public static FrameGenSpringBootEntry create(ApplicationContext context, String dataSourceName) {
        return new FrameGenSpringBootEntry(context, dataSourceName);
    }

    public FrameGenSpringBootEntry includes(Collection<String> tableNames) {
        this.includes = tableNames;
        return this;
    }

    public FrameGenSpringBootEntry includes(String... tableNames) {
        this.includes = new ArrayList<>();
        Collections.addAll(this.includes, tableNames);
        return this;
    }

    public FrameGenSpringBootEntry excludes(Collection<String> tableNames) {
        this.excludes = tableNames;
        return this;
    }

    public FrameGenSpringBootEntry excludes(String... tableNames) {
        this.excludes = new ArrayList<>();
        Collections.addAll(this.excludes, tableNames);
        return this;
    }

    public FrameGenSpringBootEntry outModule(String outModuleName) {
        this.outModuleName = outModuleName;
        return this;
    }

    public FrameGenSpringBootEntry lombok() {
        GlobalConfigHolder.enableLombok = true;
        return this;
    }

    public FrameGenSpringBootEntry kotlin() {
        GlobalConfigHolder.enableKotlin = true;
        return this;
    }

    public FrameGenSpringBootEntry multiThread() {
        GlobalConfigHolder.enableMultiThread = true;
        return this;
    }

    public FrameGenSpringBootEntry mybatis() {
        this.enableMybatis = true;
        return this;
    }

    public FrameGenSpringBootEntry mybatisPlus() {
        this.enableMybatisPlus = true;
        return this;
    }

    public FrameGenSpringBootEntry setPackage(Consumer<PackageConfig.Builder> consumer) {
        PackageConfig.Builder builder = PackageConfig.builder();
        consumer.accept(builder);
        this.packageConfig = builder.build();
        return this;
    }

    public void run() {
        // 设置默认包名
        setDefaultPackages();

        Path outRootPath = getOutputPath();
        SpringFrameGenExecutor executor = new SpringFrameGenExecutor(packageConfig, enableMybatis, enableMybatisPlus, outRootPath);

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

            log.debug("FrameGen: Tables: {}", tables);

            executor.execute(tables);
        } catch (Exception e) {
            log.error("FrameGen: 生成失败", e);
            throw new RuntimeException(e);
        }
    }

    private void setDefaultPackages() {
        if (null == packageConfig) {
            packageConfig = PackageConfig.builder().build();
        }
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

    // 获取输出路径
    private Path getOutputPath() {
        Path outRootPath;
        try {
            if (null == this.outModuleName || this.outModuleName.isEmpty()) {
                outRootPath = FileUtil.getModulePath(SpringContextHolder.getContext().getClass());
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
