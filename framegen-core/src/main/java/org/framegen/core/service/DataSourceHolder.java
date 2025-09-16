package org.framegen.core.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class DataSourceHolder {

    @Setter
    @Getter
    private static DataSource dataSource;

    @Getter
    private static String dataSourceName;

    @Setter
    private static Map<String, DataSource> dataSourceMap = null;

    public static List<String> getDataSourceNames() {
        return new ArrayList<>(dataSourceMap.keySet());
    }

    /**
     * 切换数据源
     * @param dataSourceName 目标数据源名称，为空则使用默认数据源，错误名称则抛出IllegalArgumentException
     */
    public static void changeDataSource(String dataSourceName) {
        if (null == dataSourceName || dataSourceName.isEmpty()) {
            // 未指定数据源，则使用默认数据源(第一个)
            DataSourceHolder.dataSourceName = dataSourceMap.keySet().iterator().next();
        }
        else if (dataSourceMap.containsKey(dataSourceName)) {
            DataSourceHolder.dataSourceName = dataSourceName;
        } else {
            throw new IllegalArgumentException("FrameGen: 找不到数据源: " + dataSourceName);
        }
        DataSourceHolder.dataSource = dataSourceMap.get(DataSourceHolder.dataSourceName);
        log.info("FrameGen: 已成功加载数据源 {}", dataSourceName);
    }

    /**
     * 是否存在多个数据源
     * @return true: 存在多个数据源，false: 只有一个数据源
     */
    public static boolean isMultipleDataSource() {
        return dataSourceMap!= null && dataSourceMap.size() > 1;
    }
}
