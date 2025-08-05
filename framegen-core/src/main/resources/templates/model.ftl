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
}
