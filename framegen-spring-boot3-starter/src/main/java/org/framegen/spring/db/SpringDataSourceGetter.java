package org.framegen.spring.db;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.framegen.core.db.DataSourceFactory;
import org.framegen.spring.util.SpringContextHolder;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
public class SpringDataSourceGetter implements DataSourceFactory {

    @Setter
    private static String preferredDataSourceName;

    @Override
    public DataSource getDataSource() {

        DataSource dataSource = null;

        if (null != preferredDataSourceName && !preferredDataSourceName.isEmpty()) {
            dataSource = SpringContextHolder.getContext().getBean(preferredDataSourceName, DataSource.class);
        } else {
//            dataSource = SpringContextHolder.getContext().getBean(DataSource.class);
            Map<String, DataSource> dataSourceMap =
                    SpringContextHolder.getContext().getBeansOfType(DataSource.class);
            if (!dataSourceMap.isEmpty()) {
                preferredDataSourceName = dataSourceMap.keySet().iterator().next();
                dataSource = dataSourceMap.get(preferredDataSourceName);
            }
        }
        if (null == dataSource) {
            log.error("FrameGen: 未找到数据源，请检查配置");
            throw new RuntimeException("No DataSource found in Spring Context.");
        }
        log.info("FrameGen: 成功加载数据源{}", preferredDataSourceName);
        return dataSource;
    }
}
