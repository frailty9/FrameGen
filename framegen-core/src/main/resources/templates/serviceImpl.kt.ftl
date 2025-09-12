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
class ${className} : ServiceImpl<${mapperClassName}, ${modelClassName}>(), ${interfaceName} {
<#else>
class ${className} implements ${interfaceName} {
</#if>
}
