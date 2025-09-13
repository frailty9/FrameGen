package org.framegen.solon;

import lombok.extern.slf4j.Slf4j;
import org.framegen.core.FrameGenEntry;
import org.framegen.solon.db.SolonDataSourceGetter;
import org.framegen.solon.util.SolonContextHolder;
import org.noear.solon.core.AppContext;

import javax.sql.DataSource;

@Slf4j
public class FrameGenSolonEntry extends FrameGenEntry {

    public FrameGenSolonEntry(AppContext context) {
        this(context, null);
    }

    public FrameGenSolonEntry(AppContext context, String dataSourceName) {
        super(getDataSourceFromContext(context, dataSourceName));
    }

    private static DataSource getDataSourceFromContext(AppContext context, String dataSourceName) {
        SolonContextHolder.setContext(context);
        SolonDataSourceGetter.setPreferredDataSourceName(dataSourceName);
        return new SolonDataSourceGetter().getDataSource();
    }

    public FrameGenSolonEntry create(AppContext context) {
        return new FrameGenSolonEntry(context);
    }

    public FrameGenSolonEntry create(AppContext context, String dataSourceName) {
        return new FrameGenSolonEntry(context, dataSourceName);
    }

    public void run() {
        super.run(SolonContextHolder.getContext().getClass());
    }

    public void start() {
        run();
    }
}
