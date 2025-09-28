<#--  
    .var {
        mapperClassPath,
        entityClassPath,
        tableName,
        fields,
        entityFieldJNames,
        primaryJType,
        primaryVarName,
        isAutoIncrement,
    }
  -->
<#assign startIdx = isAutoIncrement?then(1, 0)>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperClassPath}">

    <insert id="insert" parameterType="${entityClassPath}">
        INSERT INTO ${tableName} 
            (${fields[startIdx..]?join(", ")}) 
        VALUES 
            (<#list entityFieldJNames[startIdx..] as field>#${field}<#if field_has_next>, </#if></#list>)
    </insert>

    <select id="selectAll" resultType="${entityClassPath}">
        SELECT 
            <#list fields as field>${field}<#if field_has_next>, </#if></#list>
        FROM ${tableName}
    </select>

    <select id="selectById" parameterType="${primaryJType}" resultType="${entityClassPath}">
        SELECT 
            <#list fields as field>${field}<#if field_has_next>, </#if></#list>
        FROM ${tableName}
        WHERE ${fields[0]} = #${primaryVarName}
    </select>

    <update id="update" parameterType="${entityClassPath}">
        UPDATE ${tableName} 
        SET 
            <#list fields[startIdx..] as field>
            <#assign idx = field_index + startIdx>
            ${field} = #${entityFieldJNames[idx]}<#if idx < fields?size - 1>, </#if>
            </#list>
        WHERE ${fields[0]} = #${entityFieldJNames[0]}
    </update>

    <delete id="deleteById" parameterType="${primaryJType}">
        DELETE FROM ${tableName}
        WHERE ${fields[0]} = #${primaryVarName}
    </delete>

</mapper>
