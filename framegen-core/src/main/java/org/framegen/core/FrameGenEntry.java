package org.framegen.core;

import lombok.extern.slf4j.Slf4j;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.core.db.converter.AbstractTypeConverter;
import org.framegen.core.db.converter.ConverterFactory;
import org.framegen.core.db.DataSourceHolder;
import org.framegen.core.db.Query;
import org.framegen.core.db.impl.HikariDataSourceGetter;
import org.framegen.config.JdbcConfig;
import org.framegen.core.file.FileUtil;
import org.framegen.core.generator.ModelGenerator;
import org.framegen.core.model.Column;
import org.framegen.core.model.Table;

import javax.sql.DataSource;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class FrameGenEntry {

    private Collection<String> includes = new ArrayList<>();
    private Collection<String> excludes = new ArrayList<>();
    private String outModuleName;
    private PackageConfig packageConfig;
    private boolean enableMybatis = false;
    private boolean enableMybatisPlus = false;

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
        Path outRootPath = null;
        try {
            if (null == this.outModuleName || this.outModuleName.isEmpty()) {
                // 尝试使用当前类所在的包路径作为输出路径
                outRootPath = FileUtil.getModulePath(clazz);
            } else {
                // 尝试使用指定模块名作为输出路径
                outRootPath = FileUtil.getModulePath(this.outModuleName);
            }
        } catch (NullPointerException | URISyntaxException e) {
            // 输出路径异常，将默认输出到项目根目录的src/main下
            outRootPath = Paths.get(System.getProperty("user.dir"));
        } finally {
            if (null == outRootPath) throw new NullPointerException();
            outRootPath = outRootPath.resolve("src").resolve("main");

            log.debug("FrameGen: outRootPath: {}", outRootPath);
        }
        log.info("FrameGen: 输出路径: {}", outRootPath);
        Path codePath = outRootPath.resolve(GlobalConfigHolder.enableKotlin ? "kotlin" : "java");
        Path resourcePath = outRootPath.resolve("resources");

        log.debug("FrameGen: 代码路径: {}", codePath);

        try (Query query = new Query()) {
            List<Table> tables = query.getTables();
            if (null == tables || tables.isEmpty()) {
                log.warn("FrameGen: 未找到任何表，请检查数据库配置");
                return;
            }
            tables = tables.stream()
                    .filter(table -> this.includes.contains(table.getTableName())
                            && !this.excludes.contains(table.getTableName()))
                    .collect(Collectors.toList());

            for (Table table : tables) {
                log.info("FrameGen: 正在生成表: {}", table.getTableName());
                List<Column> columns = query.getTableColumns(table.getTableName());

                table.setColumns(columns);

                // 生成Model
                ModelGenerator modelGenerator = new ModelGenerator(packageConfig, codePath, table);
                modelGenerator.generate();
                // 生成Mapper
                if ((this.enableMybatis || this.enableMybatisPlus) && null != packageConfig.getMapper()) {
//                    MapperGenerator mapperGenerator = new MapperGenerator(packageConfig);
//                    mapperGenerator.generate(packageConfig, codePath);
                }
            }
        } catch (Exception e) {
            log.error("FrameGen: 生成失败", e);
            throw new RuntimeException(e);
        }
    }
}
