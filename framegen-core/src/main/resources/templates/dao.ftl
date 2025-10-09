<#--  
    data {
        tableName,
        modelClassName,
        modelFieldNames,
        modelFieldTypes,
        modelFieldGetters,
        modelFieldSetters,
        isAutoIncrement,
        primaryVarName
    }
  -->
<#assign tableName = data.tableName>
<#assign modelClassName = data.modelClassName>
<#assign modelFieldNames = data.modelFieldNames>
<#assign modelFieldTypes = data.modelFieldTypes>
<#assign modelFieldGetters = data.modelFieldGetters>
<#assign modelFieldSetters = data.modelFieldSetters>
<#assign primaryVarName = data.primaryVarName>
<#--    -->
<#assign startIdx = data.isAutoIncrement?then(1, 0)>
<#assign modelVarName = modelClassName?substring(0, 1)?lower_case + modelClassName?substring(1)>
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
public class ${className} {

    private final DataSource dataSource;

    public ${className}(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int create(${modelClassName} ${modelVarName}) {
        String sql = "INSERT INTO ${tableName} (${modelFieldNames[startIdx..]?join(", ")}) VALUES (<#list modelFieldNames[startIdx..] as fieldName>?<#if fieldName_has_next>, </#if></#list>)";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            <#list modelFieldNames[startIdx..] as fieldName>
            statement.setObject(${fieldName_index + 1}, ${modelVarName}.${modelFieldGetters[fieldName_index + startIdx]}());
            </#list>
 
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<${modelClassName}> getAll() {
        String sql = "SELECT <#list modelFieldNames as fieldName>${fieldName}<#if fieldName_has_next>, </#if></#list> FROM ${tableName}";
        List<${modelClassName}> ${modelVarName}List = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                ${modelClassName} ${modelVarName} = new ${modelClassName}();
                <#list modelFieldNames as fieldName>
                ${modelVarName}.${modelFieldSetters[fieldName_index]}((${modelFieldTypes[fieldName_index]}) resultSet.getObject(${fieldName_index + 1}));
                </#list>
                ${modelVarName}List.add(${modelVarName});
            }
            return ${modelVarName}List;
        } catch (SQLException e) {
            e.printStackTrace();
            return ${modelVarName}List;
        }
    }

    public ${modelClassName} getById(${modelFieldTypes[0]} ${primaryVarName}) {
        String sql = "SELECT <#list modelFieldNames as fieldName>${fieldName}<#if fieldName_has_next>, </#if></#list> FROM ${tableName} WHERE ${modelFieldNames[0]} = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, ${primaryVarName});
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                ${modelClassName} ${modelVarName} = new ${modelClassName}();
                <#list modelFieldNames as fieldName>
                ${modelVarName}.${modelFieldSetters[fieldName_index]}((${modelFieldTypes[fieldName_index]}) resultSet.getObject(${fieldName_index + 1}));
                </#list>
                return ${modelVarName};
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int update(${modelClassName} ${modelVarName}) {
        String sql = "UPDATE " + 
            <#list modelFieldNames[startIdx..] as modelFieldName>
                "${modelFieldName} = ?<#if modelFieldName_has_next>, </#if>" +
            </#list>
                "WHERE ${modelFieldNames[0]} = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            <#list modelFieldNames[startIdx..] as fieldName>
            statement.setObject(${fieldName_index + 1}, ${modelVarName}.${modelFieldGetters[fieldName_index + startIdx]}());
            </#list>
            statement.setObject(${modelFieldNames?size - startIdx}, ${modelVarName}.${modelFieldGetters[0]}());

            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int delete(${modelFieldTypes[0]} ${primaryVarName}) {
        String sql = "DELETE FROM ${tableName} WHERE ${modelFieldNames[0]} = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, ${primaryVarName});
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
