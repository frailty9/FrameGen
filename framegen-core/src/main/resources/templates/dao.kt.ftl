<#--  
    data {
        tableName,
        modelClassName,
        modelFieldNames,
        modelFieldVarNames,
        modelFieldTypes,
        isAutoIncrement,
        primaryVarName
    }
  -->
<#assign tableName = data.tableName>
<#assign modelClassName = data.modelClassName>
<#assign modelFieldNames = data.modelFieldNames>
<#assign modelFieldVarNames = data.modelFieldVarNames>
<#assign modelFieldTypes = data.modelFieldTypes>
<#assign primaryVarName = data.primaryVarName>
<#--    -->
<#assign startIdx = data.isAutoIncrement?then(1, 0)>
<#assign modelVarName = modelClassName?substring(0, 1)?lower_case + modelClassName?substring(1)>
package ${packagePath}

<#list imports as import>
import ${import}
</#list>

<#if (classComment?has_content)>
/**
 * ${classComment}
 */
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
class ${className}(private val dataSource: DataSource) {

    fun create(${modelVarName}: ${modelClassName}): Int {
        val sql = "INSERT INTO ${tableName} (${modelFieldNames[startIdx..]?join(", ")}) VALUES (<#list modelFieldNames[startIdx..] as fieldName>?<#if fieldName_has_next>, </#if></#list>)"
        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                <#list modelFieldNames[startIdx..] as fieldName>
                statement.setObject(${fieldName_index + 1}, ${modelVarName}.${modelFieldVarNames[fieldName_index + startIdx]})
                </#list>
                statement.executeUpdate()
            }
        }
    }

    fun getAll(): List<${modelClassName}> {
        val sql = "SELECT <#list modelFieldNames as fieldName>${fieldName}<#if fieldName_has_next>, </#if></#list> FROM ${tableName}"
        return dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(sql).use { resultSet ->
                    val ${modelVarName}List = mutableListOf<${modelClassName}>()
                    while (resultSet.next()) {
                        ${modelClassName}(
                            <#list modelFieldVarNames as modelFieldVarName>
                            ${modelFieldVarName} = resultSet.getObject(${modelFieldVarName_index + 1}) as ${modelFieldTypes[modelFieldVarName_index]}<#if modelFieldVarName_has_next>,</#if>
                            </#list>
                        ).let { ${modelVarName}List.add(it) }
                    }
                    ${modelVarName}List
                }
            }
        }
    }

    fun getById(${primaryVarName}: ${modelFieldTypes[0]}): ${modelClassName}? {
        val sql = "SELECT <#list modelFieldNames as fieldName>${fieldName}<#if fieldName_has_next>, </#if></#list> FROM ${tableName} WHERE ${modelFieldNames[0]} = ?"
        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setObject(1, ${primaryVarName})
                statement.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        ${modelClassName}(
                            <#list modelFieldVarNames as modelFieldVarName>
                            ${modelFieldVarName} = resultSet.getObject(${modelFieldVarName_index + 1}) as ${modelFieldTypes[modelFieldVarName_index]}<#if modelFieldVarName_has_next>,</#if>
                            </#list>
                        )
                    } else null
                }
            }
        }
    }

    fun update(${modelVarName}: ${modelClassName}): Int {
        val sql = "UPDATE ${tableName} SET " + 
            <#list modelFieldNames[startIdx..] as modelFieldName>
                "${modelFieldName} = ?<#if modelFieldName_has_next>, </#if>" +
            </#list>
                " WHERE ${modelFieldNames[0]} = ?"
        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                <#list modelFieldNames[startIdx..] as fieldName>
                statement.setObject(${fieldName_index + 1}, ${modelVarName}.${modelFieldVarNames[fieldName_index + startIdx]})
                </#list>
                statement.setObject(${modelFieldNames?size - startIdx}, ${modelVarName}.${modelFieldVarNames[0]})
                statement.executeUpdate()
            }
        }
    }

    fun delete(${primaryVarName}: ${modelFieldTypes[0]}): Int {
        val sql = "DELETE FROM ${tableName} WHERE ${modelFieldNames[0]} = ?"
        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setObject(1, ${primaryVarName})
                statement.executeUpdate()
            }
        }
    }
}
