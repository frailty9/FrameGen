package org.framegen.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Column {

    private String fieldName;
    private String defaultValue;
    private boolean isNullable;
    private String dataType;
    private String columnKey;
    private String extra;
    private String columnComment;

}
