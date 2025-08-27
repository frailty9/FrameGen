package org.framegen;

import org.framegen.core.db.DataSourceHolder;
import org.framegen.spring.db.SpringDataSourceGetter;
import org.framegen.spring.util.SpringContextHolder;
import org.springframework.context.ApplicationContext;

public class FrameGenSpringBootEntry {

    public static void run(ApplicationContext context, String dataSourceName) {
        SpringDataSourceGetter.setPreferredDataSourceName(dataSourceName);
        run(context);
    }

    public static void run(ApplicationContext context) {
        SpringContextHolder.setContext(context);
        DataSourceHolder.setDataSource(new SpringDataSourceGetter().getDataSource());
    }
}
