package org.framegen.spring.service;

import lombok.extern.slf4j.Slf4j;
import org.framegen.core.service.DataSourceFactory;
import org.framegen.spring.util.SpringContextHolder;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
public class SpringDataSourceFactory implements DataSourceFactory<Map<String, DataSource>> {

    public SpringDataSourceFactory(ApplicationContext context) {
        SpringContextHolder.setContext(context);
    }

    @Override
    public Map<String, DataSource> getDataSource() {
        return SpringContextHolder.getContext().getBeansOfType(DataSource.class);
    }
}
