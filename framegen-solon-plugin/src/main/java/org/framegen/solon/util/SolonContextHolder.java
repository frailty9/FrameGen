package org.framegen.solon.util;

import lombok.Getter;
import lombok.Setter;
import org.noear.solon.core.AppContext;

public class SolonContextHolder {

    @Getter
    @Setter
    private static AppContext context;

    private SolonContextHolder() {}
}
