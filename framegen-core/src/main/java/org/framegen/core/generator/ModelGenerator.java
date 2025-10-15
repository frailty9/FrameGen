package org.framegen.core.generator;

import lombok.extern.slf4j.Slf4j;
import org.framegen.config.FrameworkConfig;
import org.framegen.config.GlobalConfigHolder;
import org.framegen.config.PackageConfig;
import org.framegen.config.RepositoryFrameworkEnum;
import org.framegen.core.model.Column;
import org.framegen.core.model.Table;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ModelGenerator extends AbstractGenerator<Map<String, Object>> {

    private static final List<String> optimisticLockingNames = Arrays.asList("version");
    private static final List<String> createTimeNames = Arrays.asList("create_time", "created_at");
    private static final List<String> updateTimeNames = Arrays.asList("update_time", "updated_at");
    private static final List<String> logicDeletedNames = Arrays.asList("is_deleted", "deleted");

    public ModelGenerator(PackageConfig packageConfig, String classNameSuffix, FrameworkConfig frameworkConfig, Path codePath, Table table) throws IOException {
        super("model", classNameSuffix, frameworkConfig, codePath, table, packageConfig);
    }

    @Override
    protected void setImports() {
        if (null != table.getTypeImports()) {
            imports.addAll(table.getTypeImports());
        }

        if (GlobalConfigHolder.enableLombok && !GlobalConfigHolder.enableKotlin) {
            imports.add("lombok.AllArgsConstructor");
            imports.add("lombok.Data");
            imports.add("lombok.Builder");
            imports.add("lombok.NoArgsConstructor");
        }
    }

    @Override
    protected String getClassComment() {
        String tableComment;
        if (null != table.getTableComment() && !table.getTableComment().isEmpty()) {
            tableComment = table.getTableComment();
        } else {
            tableComment = table.getTableName();
        }
        return tableComment;
    }

    @Override
    protected List<String> getAnnotations() {
        List<String> annotations = new ArrayList<>();
        if (GlobalConfigHolder.enableLombok && !GlobalConfigHolder.enableKotlin) {
            annotations.add("Data");
            annotations.add("Builder");
            annotations.add("AllArgsConstructor");
            annotations.add("NoArgsConstructor");
        }
        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("com.baomidou.mybatisplus.annotation.TableName");
            annotations.add("TableName(\"" + table.getTableName() + "\")");
        }
        return annotations;
    }

    @Override
    protected String getPackagePath() {
        return getFullPackage(PackageConfig::getModel);
    }

    @Override
    protected Map<String, Object> getMoreData() {
        Map<String, Object> data = new HashMap<>();
        data.put("columns", table.getColumns());
        data.put("repositoryFramework", frameworkConfig.repositoryFramework.name());
        data.put("columnAnnotations", getColumnAnnotations());
        return data;
    }

    private List<String> getColumnAnnotations() {
        List<String> columnAnnotations = new ArrayList<>();
        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            for (Column column : table.getColumns()) {
                String fieldName = column.getFieldName();
                // 匹配ID
                if (column.getColumnKey().contains("PRI")) {
                    imports.add("com.baomidou.mybatisplus.annotation.TableId");
                    imports.add("com.baomidou.mybatisplus.annotation.IdType");
                    columnAnnotations.add("TableId(\"" + fieldName +
                            "\", type = IdType." + GlobalConfigHolder.mybatisPlusConfig.getIdType().name() + ")");
                    continue;
                }
                // 匹配creat与update时间字段
                boolean flag_create = createTimeNames.contains(fieldName);
                boolean flag_update = updateTimeNames.contains(fieldName);
                if (flag_create || flag_update) {
                    imports.add("com.baomidou.mybatisplus.annotation.TableField");

                    StringBuilder annotationBuilder = new StringBuilder();
                    annotationBuilder.append("TableField(\"").append(fieldName).append("\"");

                    switch (GlobalConfigHolder.mybatisPlusConfig.getTimeManager()) {
                        case MYBATIS_PLUS:
                            imports.add("com.baomidou.mybatisplus.annotation.FieldFill");
                            annotationBuilder.append(", fill = FieldFill.")
                                    .append(flag_create ? "INSERT" : "INSERT_UPDATE");
                            break;
                        case SQL:
                            imports.add("com.baomidou.mybatisplus.annotation.FieldStrategy");
                            annotationBuilder.append(", insertStrategy = FieldStrategy.NEVER");
                            break;
                        case CUSTOM:
                            imports.add("com.baomidou.mybatisplus.annotation.FieldStrategy");
                            annotationBuilder.append(", insertStrategy = FieldStrategy.NOT_NULL");
                            break;
                    }
                    String annotation = annotationBuilder.append(")").toString();
                    columnAnnotations.add(annotation);
                }
                // 匹配乐观锁
                else if (optimisticLockingNames.contains(fieldName)) {
                    imports.add("com.baomidou.mybatisplus.annotation.Version");
                    columnAnnotations.add("Version");
                }
                // 匹配逻辑删除
                else if (logicDeletedNames.contains(fieldName)) {
                    imports.add("com.baomidou.mybatisplus.annotation.TableLogic");
                    columnAnnotations.add("TableLogic");
                }
                // 默认
                else {
                    imports.add("com.baomidou.mybatisplus.annotation.TableField");
                    columnAnnotations.add("TableField(\"" + fieldName + "\")");
                }
            }
        }
        return columnAnnotations;
    }
}
