package org.framegen.core.generator.props;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class GeneratorProps<T> {
    private String packagePath;
    private Collection<String> imports;
    private Collection<String> annotations;
    private String classComment;
    private String className;
    private T data;
}
