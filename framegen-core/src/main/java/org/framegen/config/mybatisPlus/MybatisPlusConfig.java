package org.framegen.config.mybatisPlus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class MybatisPlusConfig {

    @lombok.Builder.Default
    private IdType idType = IdType.AUTO;

    @lombok.Builder.Default
    private TimeManager timeManager = TimeManager.SQL;

}
