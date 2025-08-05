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
    private ${column.dataType} ${column.fieldName};
    </#list>

    <#if (!imports?seq_contains("lombok.Data"))>
    public ${className}() {
    }

    public ${className}(<#list data as column>${column.dataType} ${column.fieldName}<#sep>, </#list>) {
        <#list data as column>
        this.${column.fieldName} = ${column.fieldName};
        </#list>
    }    
    <#list data as column>
    
    public ${column.dataType} get${column.fieldName?cap_first}() {
        return ${column.fieldName};
    }

    public void set${column.fieldName?cap_first}(${column.dataType} ${column.fieldName}) {
        this.${column.fieldName} = ${column.fieldName};
    }
    </#list>
    </#if>
}
