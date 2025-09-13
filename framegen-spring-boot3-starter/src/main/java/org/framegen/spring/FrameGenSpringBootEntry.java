package org.framegen.spring;

import lombok.extern.slf4j.Slf4j;
import org.framegen.core.FrameGenEntry;
import org.framegen.spring.db.SpringDataSourceGetter;
import org.framegen.spring.util.SpringContextHolder;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

@Slf4j
public class FrameGenSpringBootEntry extends FrameGenEntry {

    public FrameGenSpringBootEntry(ApplicationContext context) {
        this(context, null);
    }

    public FrameGenSpringBootEntry(ApplicationContext context, String dataSourceName) {
        super(getDataSourceFromContext(context, dataSourceName));
    }

    private static DataSource getDataSourceFromContext(ApplicationContext context, String dataSourceName) {
        SpringContextHolder.setContext(context);
        SpringDataSourceGetter.setPreferredDataSourceName(dataSourceName);
        return new SpringDataSourceGetter().getDataSource();
    }

    public static FrameGenSpringBootEntry create(ApplicationContext context) {
        return new FrameGenSpringBootEntry(context);
    }

    public static FrameGenSpringBootEntry create(ApplicationContext context, String dataSourceName) {
        return new FrameGenSpringBootEntry(context, dataSourceName);
    }

    public void run() {
        super.run(SpringContextHolder.getContext().getClass());
    }
}
