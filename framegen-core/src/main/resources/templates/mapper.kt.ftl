<#assign modelVarName = data.modelClassName?substring(0, 1)?lower_case + data.modelClassName?substring(1)>
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
<#if data.frameworkName == "MYBATIS">
interface ${className} {
    
    fun insert(${modelVarName}: ${data.modelClassName}): Int

    fun selectAll(): List<${data.modelClassName}>

    fun selectById(${data.primaryVarName}: ${data.primaryJType}): ${data.modelClassName}

    fun update(${modelVarName}: ${data.modelClassName}): Int

    fun deleteById(${data.primaryVarName}: ${data.primaryJType}): Int

<#elseif data.frameworkName == "MYBATIS_PLUS">
interface ${className} : BaseMapper<${data.modelClassName}> {
</#if>
}
