<#assign entityVarName = data.entityClassName?substring(0, 1)?lower_case + data.entityClassName?substring(1)>
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
    
    fun insert(${entityVarName}: ${data.entityClassName}): Int

    fun selectAll(): List<${data.entityClassName}>

    fun selectById(${data.primaryVarName}: ${data.primaryJType}): ${data.entityClassName}

    fun update(${entityVarName}: ${data.entityClassName}): Int

    fun deleteById(${data.primaryVarName}: ${data.primaryJType}): Int

<#elseif data.frameworkName == "MYBATIS_PLUS">
interface ${className} : BaseMapper<${data.entityClassName}> {
</#if>
}
