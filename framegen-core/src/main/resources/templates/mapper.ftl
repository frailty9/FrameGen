<#--  
    data {
        entityClassName,
        primaryJType,
        primaryVarName,
        frameworkName,
    }
  -->
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
public interface ${className} {
    
    public int insert(${data.entityClassName} ${entityVarName});

    public List<${data.entityClassName}> selectAll();

    public ${data.entityClassName} selectById(${data.primaryJType} ${data.primaryVarName});

    public int update(${data.entityClassName} ${entityVarName});

    public int deleteById(${data.primaryJType} ${data.primaryVarName});

<#elseif data.frameworkName == "MYBATIS_PLUS">
public interface ${className} extends BaseMapper<${data.entityClassName}> {
</#if>
}
