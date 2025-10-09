<#--  
    data {
        modelClassName,
        primaryJType,
        primaryVarName,
        frameworkName,
    }
  -->
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
public interface ${className} {
    
    public int insert(${data.modelClassName} ${modelVarName});

    public List<${data.modelClassName}> selectAll();

    public ${data.modelClassName} selectById(${data.primaryJType} ${data.primaryVarName});

    public int update(${data.modelClassName} ${modelVarName});

    public int deleteById(${data.primaryJType} ${data.primaryVarName});

<#elseif data.frameworkName == "MYBATIS_PLUS">
public interface ${className} extends BaseMapper<${data.modelClassName}> {
</#if>
}
