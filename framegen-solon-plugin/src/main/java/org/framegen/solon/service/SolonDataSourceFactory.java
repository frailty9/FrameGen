package org.framegen.solon.service;

import lombok.extern.slf4j.Slf4j;
import org.framegen.core.service.DataSourceFactory;
import org.framegen.solon.util.SolonContextHolder;
import org.noear.solon.core.AppContext;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
public class SolonDataSourceFactory implements DataSourceFactory<Map<String, DataSource>> {

    public SolonDataSourceFactory(AppContext context) {
        SolonContextHolder.setContext(context);
    }

    @Override
    public Map<String, DataSource> getDataSource() {
        return SolonContextHolder.getContext().getBeansMapOfType(DataSource.class);
    }
}
