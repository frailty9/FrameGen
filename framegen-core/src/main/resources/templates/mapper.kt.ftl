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
public class ${className} {
<#elseif data.frameworkName == "MYBATIS">
public interface ${className} {
<#elseif data.frameworkName == "MYBATIS_PLUS">
public interface ${className} extends BaseMapper<${data.modelClassName}> {
</#if>
}
