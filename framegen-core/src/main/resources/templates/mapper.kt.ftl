package ${packagePath};

<#list imports as import>
import ${import};
</#list>

<#if (classComment?has_content)>
/**
 * ${classComment}
 */
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
<#if data.frameworkName == "NATIVE_JDBC">
class ${className} {
<#elseif data.frameworkName == "MYBATIS">
interface ${className} {
<#elseif data.frameworkName == "MYBATIS_PLUS">
interface ${className} : BaseMapper<${data.modelClassName}> {
</#if>
}
