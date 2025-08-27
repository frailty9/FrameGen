package org.framegen.solon.db;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.framegen.core.db.DataSourceFactory;
import org.framegen.solon.util.SolonContextHolder;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
public class SolonDataSourceGetter implements DataSourceFactory {

    @Setter
    private static String preferredDataSourceName;

    @Override
    public DataSource getDataSource() {
        DataSource dataSource = null;
        if (null != preferredDataSourceName && !preferredDataSourceName.isEmpty()) {
            dataSource = SolonContextHolder.getContext().getBean(preferredDataSourceName);
        } else {
            Map<String, DataSource> dataSourceMap =
                    SolonContextHolder.getContext().getBeansMapOfType(DataSource.class);
            if (!dataSourceMap.isEmpty()) {
                // 取第一个数据源
                preferredDataSourceName = dataSourceMap.keySet().iterator().next();
                dataSource = dataSourceMap.get(preferredDataSourceName);
            }
        }
        if (null == dataSource) {
            log.error("FrameGen: 未能正确加载数据源");
            throw new RuntimeException("No DataSource found in Solon Context.");
        }
        log.info("FrameGen: 已成功加载数据源 {}", preferredDataSourceName);
        return dataSource;
    }
}
