package org.framegen.core;

import lombok.extern.slf4j.Slf4j;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.core.service.DataSourceHolder;
import org.framegen.core.db.Query;
import org.framegen.core.db.impl.HikariDataSourceGetter;
import org.framegen.config.JdbcConfig;
import org.framegen.core.file.FileUtil;
import org.framegen.core.model.Table;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class AbstractEntry<T extends AbstractEntry<T>> {

    protected Collection<String> includes = new ArrayList<>();
    protected Collection<String> excludes = new ArrayList<>();
    protected String outModuleName = "";
    protected PackageConfig packageConfig;
    protected boolean enableMybatis = false;
    protected boolean enableMybatisPlus = false;

    // 传入连接配置的构造方法
    public AbstractEntry(JdbcConfig jdbcConfig) {
        DataSourceHolder.setDataSource(new HikariDataSourceGetter(jdbcConfig).getDataSource());
    }

    // 传入数据源的构造方法
    public AbstractEntry(DataSource dataSource) {
        DataSourceHolder.setDataSource(dataSource);
    }

    public AbstractEntry(Map<String, DataSource> dataSourceMap, String dataSourceName) {
        DataSourceHolder.setDataSourceMap(dataSourceMap);
        DataSourceHolder.changeDataSource(dataSourceName);
    }

    public abstract T self();

    public T includes(Collection<String> tableNames) {
        this.includes = tableNames;
        return self();
    }

    public T includes(String... tableNames) {
        this.includes = new ArrayList<>();
        Collections.addAll(this.includes, tableNames);
        return self();
    }

    public T excludes(Collection<String> tableNames) {
        this.excludes = tableNames;
        return self();
    }

    public T excludes(String... tableNames) {
        this.excludes = new ArrayList<>();
        Collections.addAll(this.excludes, tableNames);
        return self();
    }

    public T outModule(String outModuleName) {
        this.outModuleName = null == outModuleName ? "" : outModuleName;
        return self();
    }

    public T lombok() {
        GlobalConfigHolder.enableLombok = true;
        return self();
    }

    public T kotlin() {
        GlobalConfigHolder.enableKotlin = true;
        return self();
    }

    public T multiThread() {
        GlobalConfigHolder.enableMultiThread = true;
        return self();
    }

    public T mybatis() {
        this.enableMybatis = true;
        return self();
    }

    public T mybatisPlus() {
        this.enableMybatisPlus = true;
        return self();
    }

    public T setPackage(Consumer<PackageConfig.Builder> consumer) {
        PackageConfig.Builder builder = PackageConfig.builder();
        consumer.accept(builder);
        this.packageConfig = builder.build();
        return self();
    }

    public <E> void run(Class<E> clazz) {
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

    protected <E> Path getOutputPath(Class<E> clazz) {
        Path outRootPath;
        try {
            if (this.outModuleName.isEmpty()) {
                outRootPath = FileUtil.findModulePath(clazz);
            } else {
                outRootPath = FileUtil.findModulePath(this.outModuleName);
            }
        } catch (NullPointerException | URISyntaxException | IOException e) {
            log.warn("FrameGen: 无法确定输出路径, 将使用当前工作目录作为输出路径");
            outRootPath = Paths.get(System.getProperty("user.dir"));
        }

        outRootPath = outRootPath.resolve("src").resolve("main");
        log.debug("FrameGen: outRootPath: {}", outRootPath);
        return outRootPath;
    }

    /**
     * 运行命令行模式
     */
    public void runCli() {
        try {
            // 尝试动态加载FrameGenCLI类
            Class<?> frameGenCLIClass = Class.forName("org.framegen.core.FrameGenCLI");

            // 实例化FrameGenCLI类
            Object frameGenCLIInstance = frameGenCLIClass.getDeclaredConstructor().newInstance();

            // 获取runCommandLine方法
            Method runCommandLineMethod = frameGenCLIClass.getDeclaredMethod("runCommandLine");

            // 调用runCommandLine方法
            runCommandLineMethod.invoke(frameGenCLIInstance);
        } catch (ClassNotFoundException e) {
            // 如果类未找到
            String msg = "请检查是否引入了framegen-cli依赖";
            log.error(msg);
            throw new RuntimeException(msg, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
