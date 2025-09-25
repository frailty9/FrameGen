# FrameGen API 文档

## FrameGenExecutor

`FrameGenExecutor`是负责执行代码生成逻辑的核心类，封装了原先在`FrameGenEntry`中的所有生成逻辑。

### 构造函数

```java
public FrameGenExecutor(
    PackageConfig packageConfig, 
    boolean enableMybatis,
    boolean enableMybatisPlus, 
    Path outRootPath
)
```

参数说明：
- `packageConfig`: 包名配置（entity、mapper、service等）
- `enableMybatis`: 是否启用MyBatis支持
- `enableMybatisPlus`: 是否启用MyBatis-Plus支持  
- `outRootPath`: 生成文件的根输出路径

### 方法说明

#### execute(List<Table> tables)

为指定的表执行代码生成过程。

```java
public void execute(List<Table> tables)
```

参数说明：
- `tables`: 需要生成代码的Table对象列表

### 使用示例

```java
PackageConfig config = PackageConfig.builder()
    .entity("com.example.entity")
    .mapper("com.example.mapper")
    .build();

Path outputPath = Paths.get("src/main");
FrameGenExecutor executor = new FrameGenExecutor(
    config, 
    true,  // enableMybatis
    false, // enableMybatisPlus
    outputPath
);

executor.execute(tables);
```

### 集成说明

`FrameGenExecutor`设计用于框架特定的入口点：
- `FrameGenEntry` 用于独立使用
- `FrameGenSpringBootEntry` 用于Spring Boot集成
- `FrameGenSolonEntry` 用于Solon集成

每个框架集成可以提供自己的配置机制，同时将实际的生成工作委托给`FrameGenExecutor`。
