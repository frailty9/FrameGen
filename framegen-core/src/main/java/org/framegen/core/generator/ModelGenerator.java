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

//    private static final String[] optimisticLockingNames = new String[] {"version"};
//    private static final String[] createTimeNames = new String[] {"create_time", "created_at"};
//    private static final String[] updateTimeNames = new String[] {"update_time", "updated_at"};
//    private static final String[] logicDeletedNames = new String[] {"is_deleted", "deleted"};
    private static final List<String> optimisticLockingNames = Arrays.asList("version");
    private static final List<String> createTimeNames = Arrays.asList("create_time", "created_at");
    private static final List<String> updateTimeNames = Arrays.asList("update_time", "updated_at");
    private static final List<String> logicDeletedNames = Arrays.asList("is_deleted", "deleted");

    private static final Map<String, List<String>> columnAnnotationMap = new HashMap<>();

    static {
        columnAnnotationMap.put("Version", optimisticLockingNames);
        columnAnnotationMap.put("TableField(fill = FieldFill.INSERT)", createTimeNames);
        columnAnnotationMap.put("TableField(fill = FieldFill.INSERT_UPDATE)", updateTimeNames);
        columnAnnotationMap.put("TableLogic", logicDeletedNames);
    }

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

        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            imports.add("com.baomidou.mybatisplus.annotation.TableName");
            imports.add("com.baomidou.mybatisplus.annotation.TableId");
            imports.add("com.baomidou.mybatisplus.annotation.TableField");
            imports.add("com.baomidou.mybatisplus.annotation.TableLogic");
            imports.add("com.baomidou.mybatisplus.annotation.Version");
            imports.add("com.baomidou.mybatisplus.annotation.FieldFill");
            imports.add("com.baomidou.mybatisplus.annotation.IdType");
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

        List<String> columnAnnotations = new ArrayList<>();
        if (frameworkConfig.repositoryFramework == RepositoryFrameworkEnum.MYBATIS_PLUS) {
            for (Column column : table.getColumns()) {
                if (column.getColumnKey().contains("PRI")) {
                    columnAnnotations.add("TableId(\"" + column.getFieldName() +
                            "\", type = IdType."+ GlobalConfigHolder.idType.name() +")");
                } else if(!"".equals(getColumnAnnotation(column.getFieldName()))) {
                    columnAnnotations.add(getColumnAnnotation(column.getFieldName()));
                } else {
                    columnAnnotations.add("TableField(\"" + column.getFieldName() + "\")");
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("columns", table.getColumns());
        data.put("repositoryFramework", frameworkConfig.repositoryFramework.name());
        data.put("columnAnnotations", columnAnnotations);

        return data;
    }

    private String getColumnAnnotation(String fieldName) {
        String result = "";
        for (String annotation : columnAnnotationMap.keySet()) {
            if (Arrays.asList(columnAnnotationMap.get(annotation)).contains(fieldName)) {
                result = annotation;
                break;
            }
        }
        return result;
    }
}
