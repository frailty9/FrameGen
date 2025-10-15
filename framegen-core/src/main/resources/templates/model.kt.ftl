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
    <#list data.columns as column>
    /**
    <#if column.columnComment?has_content>
     * ${column.columnComment}
    <#else>
     * ${column.fieldName}
    </#if>
     */
    <#if data.repositoryFramework == "MYBATIS_PLUS">
    @${data.columnAnnotations[column_index]}
    </#if>
    <#if column.nullable>
    var ${column.variableName}: ${column.dataType}? = null<#sep>,</#sep>
    <#else>
    var ${column.variableName}: ${column.dataType}<#sep>,</#sep>
    </#if>
    </#list>
)
