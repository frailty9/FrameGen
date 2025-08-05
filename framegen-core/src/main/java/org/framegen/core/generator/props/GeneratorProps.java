package org.framegen.core.generator.props;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class GeneratorProps<T> {
    private String packagePath;
    private Collection<String> imports;
    private Collection<String> annotations;
    private String classComment;
    private String className;
    private T data;

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static class Builder<T> {
        private String packagePath;
        private Collection<String> imports;
        private Collection<String> annotations;
        private String classComment;
        private String className;
        private T data;

        Builder() {
        }

        public Builder<T> packagePath(String packagePath) {
            this.packagePath = packagePath;
            return this;
        }

        public Builder<T> imports(Collection<String> imports) {
            this.imports = imports;
            return this;
        }

        public Builder<T> annotations(Collection<String> annotations) {
            this.annotations = annotations;
            return this;
        }

        public Builder<T> classComment(String classComment) {
            this.classComment = classComment;
            return this;
        }

        public Builder<T> className(String className) {
            this.className = className;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public GeneratorProps<T> build() {
            return new GeneratorProps<T>(packagePath, imports, annotations, classComment, className, data);
        }
        
    }
}
