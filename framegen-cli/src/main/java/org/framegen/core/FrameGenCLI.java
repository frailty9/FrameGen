package org.framegen.core;

import org.framegen.config.FrameworkConfig;
import org.framegen.config.PackageConfig;
import org.framegen.config.RepositoryFrameworkEnum;
import org.framegen.core.db.Query;
import org.framegen.core.file.FileUtil;
import org.framegen.core.model.Table;
import org.framegen.core.service.DataSourceHolder;
import org.framegen.util.ConsoleStyle;
import org.framegen.util.ConsoleUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FrameGenCLI {

    public void runCommandLine(FrameGenExecutor executor) {
        // ConsoleUtils.info("Info");
        // ConsoleUtils.success("Success");
        // ConsoleUtils.warn("Warning");
        // ConsoleUtils.error("Error");
        // ConsoleUtils.title("Title");
        // ConsoleUtils.banner("Banner");
        // ConsoleUtils.delay("Delay", 100);

        ConsoleUtils.clearScreen();
        ConsoleUtils.title("FrameGen命令行模式");

        // === 选择数据源 ===
        if (DataSourceHolder.isMultipleDataSource()) {
            List<String> dataSourceNames = DataSourceHolder.getDataSourceNames();
            String currentDataSourceName = DataSourceHolder.getDataSourceName();
            int currentIndex = dataSourceNames.indexOf(currentDataSourceName);
            int selected = ConsoleUtils.selectOne("请选择数据源", dataSourceNames, currentIndex + 1);
            DataSourceHolder.changeDataSource(dataSourceNames.get(selected - 1));
        }

        // === 选择表格 ===
        try (Query query = new Query()) {
            // 获取所有表格信息
            List<Table> tables = query.getTables();
            // 取得表格名称
            List<String> tableNames = tables.stream()
                    .map(Table::getTableName)
                    .collect(Collectors.toList());
            // 选择表格
            Set<Integer> selected = ConsoleUtils.selectMultiple("请选择表格", tableNames);

            if (!selected.isEmpty()) {
                // 过滤出选择的表格
                tables = tables.stream()
                        .filter(t -> selected.contains(tableNames.indexOf(t.getTableName())))
                        .collect(Collectors.toList());
            }

            // 为表格装配列信息
            for (Table table : tables) {
                table.setColumns(query.getTableColumns(table.getTableName()));
            }

            // === 选择输出目标模块 ===
            String moduleName = setModuleName();
            executor.outRootPath = FileUtil.findModulePath(moduleName);

            // === 框架配置项 ===
            setCustomFrameworkConfig(executor.frameworkConfig);

            // === 配置生成包路径 ===
            // 命令行设置包路径
            setCustomPackage(executor.packageConfig);
            // 应用默认缺省值
            executor.packageConfig.applyDefault(executor.frameworkConfig);

            // === 开始生成 ===
            executor.execute(tables);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String setModuleName() {

        String selectedName = FileUtil.getModuleName(getClass());

        ConsoleUtils.clearScreen();
        ConsoleUtils.print("当前默认输出目标模块(目录)名为: ");
        ConsoleUtils.print(selectedName, ConsoleStyle.GREEN);

        boolean isNeedChange = ConsoleUtils.readYesNo(" 是否需要切换: ", false);

        if (isNeedChange) {
            List<String> moduleNames = FileUtil.getModuleNames();
            int selected = ConsoleUtils.selectOne("请选择输出目标模块(如果菜单不包含所需模块, 输入0切换输入自定义模块名)", moduleNames, 0);
            if (0 == selected) {
                return ConsoleUtils.readLine("请输入自定义模块名: ");
            } else {
                return moduleNames.get(selected - 1);
            }
        }
        return selectedName;
    }

    private void setCustomPackage(PackageConfig packageConfig) {
        if (null == packageConfig.getOrigin()) {
            String origin = ConsoleUtils.readLine("请输入您的统一的前缀包名[没有则直接回车]: ");
            packageConfig.setOrigin(origin);
        }
        if (null == packageConfig.getModel()) {
            String model = ConsoleUtils.readLine("请输入您的Model包名[model]: ");
            packageConfig.setModel(model);
        }
        if (null == packageConfig.getMapper()) {
            String mapper = ConsoleUtils.readLine("请输入您的Mapper包名[mapper]: ");
            packageConfig.setMapper(mapper);
        }
        if (null == packageConfig.getService()) {
            String service = ConsoleUtils.readLine("请输入您的Service包名[当框架需要时默认为service]: ");
            packageConfig.setService(service);
        }
        if (null == packageConfig.getServiceImpl()) {
            String serviceImpl = ConsoleUtils.readLine("请输入您的ServiceImpl包名[当框架需要时默认为service.impl]: ");
            packageConfig.setServiceImpl(serviceImpl);
        }
        if (null == packageConfig.getController()) {
            String controller = ConsoleUtils.readLine("请输入您的Controller包名[默认不生成控制器]: ");
            packageConfig.setController(controller);
        }
    }

    private void setCustomFrameworkConfig(FrameworkConfig frameworkConfig) {
        if (null == frameworkConfig.getEnableSpring() && !Boolean.TRUE.equals(frameworkConfig.getEnableSolon())) {
            boolean enableSpring = ConsoleUtils.readYesNo("您是否使用SpringBoot", false);
            frameworkConfig.setEnableSpring(enableSpring);
        }
        if (null == frameworkConfig.getEnableSolon() && !Boolean.TRUE.equals(frameworkConfig.getEnableSpring())) {
            boolean enableSolon = ConsoleUtils.readYesNo("您是否使用Solon", false);
            frameworkConfig.setEnableSolon(enableSolon);
        }
        if (RepositoryFrameworkEnum.NATIVE_JDBC == frameworkConfig.repositoryFramework) {
            boolean enableMybatis = ConsoleUtils.readYesNo("您是否使用Mybatis", false);
            if (enableMybatis) {
                frameworkConfig.repositoryFramework = RepositoryFrameworkEnum.MYBATIS;
                return;
            }
            boolean enableMybatisPlus = ConsoleUtils.readYesNo("您是否使用Mybatis-Plus", false);
            if (enableMybatisPlus) {
                frameworkConfig.repositoryFramework = RepositoryFrameworkEnum.MYBATIS_PLUS;
                return;
            }
        }
    }
}
