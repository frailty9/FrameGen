package org.framegen.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Column {

    private String fieldName;
    private String variableName;
    private String defaultValue;
    private boolean isNullable;
    private String dataType;
    private String columnKey;
    private String extra;
    private String columnComment;

}
