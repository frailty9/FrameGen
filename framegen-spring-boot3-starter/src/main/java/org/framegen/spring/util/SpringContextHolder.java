package org.framegen.spring.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

public class SpringContextHolder {

    @Getter
    @Setter
    private static ApplicationContext context;

    private SpringContextHolder() {}
}
