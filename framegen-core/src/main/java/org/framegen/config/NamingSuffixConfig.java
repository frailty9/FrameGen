package org.framegen.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名后缀配置类
 */
@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public final class NamingSuffixConfig {
    
    @Default
    private String entity = "Entity";
    @Default
    private String persistence = "Persistence";
    @Default
    private String service = "Service";
    @Default
    private String serviceImpl = "ServiceImpl";
    @Default
    private String controller = "Controller";

}
