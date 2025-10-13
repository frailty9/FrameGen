package org.framegen.spring;

import lombok.extern.slf4j.Slf4j;

import org.framegen.config.AppFrameworkEnum;
import org.framegen.core.AbstractEntry;
import org.framegen.core.FrameGenExecutor;
import org.framegen.spring.service.SpringDataSourceFactory;
import org.framegen.spring.util.SpringContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

@Slf4j
public class FrameGenSpringBootEntry extends AbstractEntry<FrameGenSpringBootEntry> {

    public FrameGenSpringBootEntry(ApplicationContext context) {
        this(context, null);
    }

    public FrameGenSpringBootEntry(ApplicationContext context, String dataSourceName) {
        super(new SpringDataSourceFactory(context).getDataSource(), dataSourceName, AppFrameworkEnum.SPRING_BOOT);
    }

    public static FrameGenSpringBootEntry create(ApplicationContext context) {
        return new FrameGenSpringBootEntry(context);
    }

    public static FrameGenSpringBootEntry create(ApplicationContext context, String dataSourceName) {
        return new FrameGenSpringBootEntry(context, dataSourceName);
    }

    @Override
    public FrameGenSpringBootEntry self() {
        return this;
    }

    @Override
    protected Class<? extends FrameGenExecutor> getExecutorClass() {
        return FrameGenSpringExecutor.class;
    }

    @Override
    public void run() {
        super.run();
        SpringApplication.exit(SpringContextHolder.getContext());
    }
}
