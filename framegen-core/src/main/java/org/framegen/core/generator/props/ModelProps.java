package org.framegen.core.generator.props;

import lombok.Builder;
import lombok.Data;
import org.framegen.core.model.Column;

import java.util.Collection;

@Data
@Builder
public class ModelProps {

    private String packagePath;
    private Collection<String> imports;
    private Collection<String> annotations;
    private String tableComment;
    private String className;
    private Collection<Column> columns;

}
