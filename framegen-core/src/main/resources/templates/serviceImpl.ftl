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
public class ${className} extends ServiceImpl<${data.mapperClassName}, ${data.entityClassName}> implements ${data.interfaceName} {
<#else>
public class ${className} implements ${data.interfaceName} {
</#if>
}
