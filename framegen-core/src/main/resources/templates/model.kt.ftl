package ${packagePath}

<#list imports as import>
import ${import}
</#list>

/**
 * ${classComment}
 */
<#list annotations as annotation>
@${annotation}
</#list>
data class ${className}(
    <#list data as column>
    /**
    <#if column.columnComment?has_content>
    * ${column.columnComment}
    <#else>
    * ${column.fieldName}
    </#if>
    */
    <#if column.nullable>
    var ${column.variableName}: ${column.dataType}? = null<#sep>,</#sep>
    <#else>
    var ${column.variableName}: ${column.dataType}<#sep>,</#sep>
    </#if>
    </#list>
)
