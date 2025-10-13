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
open class ${className} : ServiceImpl<${data.mapperClassName}, ${data.modelClassName}>(), ${data.interfaceName} {
<#else>
open class ${className} implements ${data.interfaceName} {
</#if>
}
