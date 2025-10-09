package org.framegen.core;

import lombok.extern.slf4j.Slf4j;

import org.framegen.config.FrameworkConfig;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.NamingSuffixConfig;
import org.framegen.config.PackageConfig;
import org.framegen.config.RepositoryFrameworkEnum;
import org.framegen.core.db.Query;
import org.framegen.core.file.FileUtil;
import org.framegen.core.model.Table;
import org.framegen.core.service.DataSourceHolder;
import org.framegen.util.ConsoleStyle;
import org.framegen.util.ConsoleUtils;
import org.framegen.util.StrUtil;
import org.framegen.util.StringSetter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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

        try (Query query = new Query()) {
            // === 选择表格 ===

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
                        .filter(t -> selected.contains(tableNames.indexOf(t.getTableName()) + 1))
                        .collect(Collectors.toList());
            }

            // 为表格装配列信息
            for (Table table : tables) {
                table.setColumns(query.getTableColumns(table.getTableName()));
            }

            // === 设置表前缀 ===
            boolean isNeedChange = ConsoleUtils.readYesNo("生成代码时是否需要移除表名前缀", false);

            // 自动尝试获取表前缀
            if (isNeedChange) {
                // 更新表名列表
                List<String> tableNames2 = tables.stream()
                        .map(Table::getTableName)
                        .collect(Collectors.toList());
                // 尝试自动获取表名前缀
                String tablePrefix = StrUtil.getCommonPrefix(tableNames2);
                // 手动输入表名前缀， 默认值为自动获取的表名前缀
                ConsoleUtils.print("请输入表名前缀[");
                ConsoleUtils.print(tablePrefix, ConsoleStyle.GREEN);
                ConsoleUtils.print("]: ");
                String input = ConsoleUtils.readLine("");
                if (!tablePrefix.isEmpty()) {
                    tablePrefix = input;
                }
                // 格式调整并设置到字符串工具的static变量中
                StrUtil.setTableNamePrefix(tablePrefix);
            }

            // === 选择编程语言 ===
            boolean isKotlin = ConsoleUtils.readYesNo("是否使用Kotlin", false);
            if (isKotlin) {
                GlobalConfigHolder.enableKotlin = true;
            }

            // === 选择输出目标 ===
            List<String> menu1 = new ArrayList<>();
            menu1.add("通过选择模块");
            menu1.add("通过输入路径");

            int selected1 = ConsoleUtils.selectOne("选择确定输出位置的方式", menu1, 1);
            Path outRootPath = null;
            Path resourcePath = null;
            if (selected1 == 1) {
                // 选择模块
                String moduleName = selectModuleName();
                outRootPath = FileUtil.findModulePath(moduleName);
            }
            if (null == outRootPath) {
                // 输入路径
                String moduleName = ConsoleUtils.readLine("请输入输出项目的源码根目录(包名的上一级, 通常为java/kotlin): ");
                String inputResource = ConsoleUtils.readLine("请输入输出项目的资源根目录(通常为resources): ");
                resourcePath = Paths.get(inputResource);
                if (!moduleName.isEmpty()) {
                    outRootPath = Paths.get(moduleName);
                }
            } else {
                outRootPath = outRootPath.resolve("src/main");
            }
            if (null == outRootPath) {
                ConsoleUtils.error("未选择或输入有效的输出目录");
                throw new RuntimeException("未选择或输入有效的输出目录");
            }
            executor.setOutRootPath(outRootPath);
            if (null != resourcePath) {
                executor.resourcePath = resourcePath;
            }

            // === 框架配置项 ===
            setCustomFrameworkConfig(executor.frameworkConfig);

            // === 配置生成包路径 ===
            // 命令行设置包路径
            setCustomPackage(executor.packageConfig);
            // 应用默认缺省值
            executor.packageConfig.withDefaults(executor.frameworkConfig);

            // === 配置类名后缀 ===
            setNamingSuffix(executor.namingSuffixConfig);

            // === 开始生成 ===
            // 展示信息
            ConsoleUtils.print("生成表格: ");
            String tableNamesStr = tables.stream()
                    .map(Table::getTableName)
                    .collect(Collectors.joining(", "));
            ConsoleUtils.println(tableNamesStr, ConsoleStyle.GREEN);

            // 等待确认
            ConsoleUtils.readLine("回车开始生成...");
            executor.execute(tables);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String selectModuleName() {

        String selectedName = FileUtil.getModuleName(getClass());
        ConsoleUtils.clearScreen();
        // 标记是否需要切换模块
        boolean isNeedChange = false;
        if (selectedName.isEmpty()) {
            ConsoleUtils.warn("无法自动识别当前模块(项目)根目录");
        } else {
            ConsoleUtils.print("当前默认输出目标模块(项目)名为: ");
            ConsoleUtils.print(selectedName, ConsoleStyle.GREEN);
            isNeedChange = ConsoleUtils.readYesNo(" 是否需要切换: ", false);
        }
        if (isNeedChange) {
            List<String> moduleNames = FileUtil.getModuleNames();
            if (moduleNames.isEmpty()) {
                ConsoleUtils.warn("无法找到有效的模块(项目)根目录");
            } else {
                int selected = ConsoleUtils.selectOne("请选择输出目标模块(如果菜单不包含所需模块, 输入0切换输入自定义模块名)",
                        moduleNames, 0);
                if (0 == selected) {
                    return ConsoleUtils.readLine("请输入自定义模块名: ");
                } else {
                    return moduleNames.get(selected - 1);
                }
            }
        }
        return selectedName;
    }

    private void setCustomPackage(PackageConfig packageConfig) {
        if (null == packageConfig.getRoot()) {
            String root = ConsoleUtils.readLine("请输入您的统一的前缀包名[没有则直接回车]: ");
            if (root.isEmpty()) root = null;
            packageConfig.setRoot(root);
        }
        if (null == packageConfig.getModel()) {
            String model = ConsoleUtils.readLine("请输入您的Model包名[model]: ");
            if (model.isEmpty()) model = null;
            packageConfig.setModel(model);
        }
        if (null == packageConfig.getDao()) {
            String mapper = ConsoleUtils.readLine("请输入您的Mapper包名[mapper]: ");
            if (mapper.isEmpty()) mapper = null;
            packageConfig.setDao(mapper);
        }
        if (null == packageConfig.getService()) {
            String service = ConsoleUtils.readLine("请输入您的Service包名[当框架需要时默认为service]: ");
            if (service.isEmpty()) service = null;
            packageConfig.setService(service);
        }
        if (null == packageConfig.getServiceImpl()) {
            String serviceImpl = ConsoleUtils.readLine("请输入您的ServiceImpl包名[当框架需要时默认为service.impl]: ");
            if (serviceImpl.isEmpty()) serviceImpl = null;
            packageConfig.setServiceImpl(serviceImpl);
        }
        if (null == packageConfig.getController()) {
            String controller = ConsoleUtils.readLine("请输入您的Controller包名[默认不生成控制器]: ");
            if (controller.isEmpty()) controller = null;
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
            }
            boolean enableMybatisPlus = ConsoleUtils.readYesNo("您是否使用Mybatis-Plus", false);
            if (enableMybatisPlus) {
                frameworkConfig.repositoryFramework = RepositoryFrameworkEnum.MYBATIS_PLUS;
                return;
            }
        }
    }

    private void setNamingSuffix(NamingSuffixConfig nsConfig) {
        // 菜单
            List<String> menu2 = Arrays.asList(
                    "实体类后缀[" + nsConfig.getModel() + "]",
                    "持久层类后缀[" + nsConfig.getDao() + "]",
                    "服务层接口类后缀[" + nsConfig.getService() + "]",
                    "服务层实现类后缀[" + nsConfig.getServiceImpl() + "]",
                    "控制器层类后缀[" + nsConfig.getController() + "]");
            // 控制菜单
            List<StringSetter> actions = Arrays.asList(
                    nsConfig::setModel,
                    nsConfig::setDao,
                    nsConfig::setService,
                    nsConfig::setServiceImpl,
                    nsConfig::setController);

            Set<Integer> selected2 = ConsoleUtils.selectMultiple("是否需要自定义类名后缀, 请选择需要修改的项, 输入0跳过修改", menu2);

            if (!selected2.isEmpty()) {
                for (int i : selected2) {
                    String input = ConsoleUtils.readLine("请输入新的" + menu2.get(i - 1) + ": ");
                    if (!input.isEmpty()) {
                        actions.get(i - 1).set(input);;
                    }
                }
            }
    }
}
