package org.framegen;

import lombok.extern.slf4j.Slf4j;
import org.framegen.core.db.DataSourceHolder;
import org.framegen.solon.db.SolonDataSourceGetter;
import org.framegen.solon.util.SolonContextHolder;
import org.noear.solon.core.AppContext;

@Slf4j
public class FrameGenSolonEntry {

    public static void start(AppContext context, String dataSourceName) {
        SolonDataSourceGetter.setPreferredDataSourceName(dataSourceName);
        start(context);
    }

    public static void start(AppContext context) {
        SolonContextHolder.setContext(context);
        DataSourceHolder.setDataSource(new SolonDataSourceGetter().getDataSource());
    }
}
