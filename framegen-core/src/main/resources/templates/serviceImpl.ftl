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
public class ${className} extends ServiceImpl<${mapperClassName}, ${modelClassName}> implements ${interfaceName} {
<#else>
public class ${className} implements ${interfaceName} {
</#if>
}
