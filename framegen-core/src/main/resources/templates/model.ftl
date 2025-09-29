package ${packagePath};

<#list imports as import>
import ${import};
</#list>

/**
 * ${classComment}
 */
<#list annotations as annotation>
@${annotation}
</#list>
public class ${className} {
    <#list data as column>
    /**
    <#if column.columnComment?has_content>
    * ${column.columnComment}
    <#else>
    * ${column.fieldName}
    </#if>
    */
    private ${column.dataType} ${column.variableName};
    </#list>

    <#if (!imports?seq_contains("lombok.Data"))>
    public ${className}() {
    }

    public ${className}(<#list data as column>${column.dataType} ${column.variableName}<#sep>, </#list>) {
        <#list data as column>
        this.${column.variableName} = ${column.variableName};
        </#list>
    }    
    <#list data as column>
    
    public ${column.dataType} get${column.variableName?cap_first}() {
        return ${column.variableName};
    }

    public void set${column.variableName?cap_first}(${column.dataType} ${column.variableName}) {
        this.${column.variableName} = ${column.variableName};
    }
    </#list>
    </#if>
}
