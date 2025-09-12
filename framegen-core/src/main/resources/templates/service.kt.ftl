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
<#if data.frameworkName == "MYBATIS_PLUS">
interface ${className} : IService<${modelClassName}> {
<#elseif hasImpl?boolean>
interface ${className} {
<#else>
class ${className} {
</#if>
}