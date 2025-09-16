package org.framegen.solon;

import lombok.extern.slf4j.Slf4j;
import org.framegen.core.FrameGenEntry;
import org.framegen.core.service.SolonDataSourceFactory;
import org.framegen.solon.util.SolonContextHolder;
import org.noear.solon.core.AppContext;

@Slf4j
public class FrameGenSolonEntry extends FrameGenEntry {

    public FrameGenSolonEntry(AppContext context) {
        this(context, null);
    }

    public FrameGenSolonEntry(AppContext context, String dataSourceName) {
        super(new SolonDataSourceFactory(context).getDataSource(), dataSourceName);
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
