package org.framegen.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class FrameworkConfig {
    private boolean enableSpring;
    private boolean enableSolon;

    private boolean enableMybatis;
    private boolean enableMybatisPlus;
}
