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
public interface ${className} extends IService<${data.modelClassName}> {
<#elseif hasImpl?boolean>
public interface ${className} {
<#else>
public class ${className} {
</#if>
}
