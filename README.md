# FrameGen 代码生成器

FrameGen 是一个依赖引入型的代码生成器，它可以根据数据库表结构自动生成实体类、Mapper 接口、Mapper XML、Service 等代码，并提供定制化配置。

## 如何使用

### 引入

如果你使用 Maven

```xml
<!-- 引入FrameGen核心依赖 -->
<dependency>
    <groupId>io.github.framegen</groupId>
    <artifactId>framegen-core</artifactId>
    <version>0.2.0</version>
</dependency>
```

### 原生支持

```java
public class FrameGenExample {
    public static void main(String[] args) throws URISyntaxException {

        // 传入数据库连接信息进行初始化
        new FrameGenEntry( JdbcConfig.builder()
                    .url("jdbc:mysql://localhost:3306/framegen")
                    .username("root")
                    .password("123456")
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .build())
            // 选择目标模块, 否则使用运行时所在位置
            .outModule("framegen-example-corejava")
            // 设置生成的包路径
            .setPackage(builder -> {
                builder.origin("org.framegen.example")  // 起始路径
                        .model("model") // 实体类路径
                        .mapper("dso.mapper") // Mapper接口路径
                        .service("dso.service") // Service接口路径
                ;
            })
            .includes("user")   // 包含的表名
            .lombok()   // 生成lombok注解
            .mybatisPlus()   // 如果您正在使用mybatis-plus
            .run(FrameGenExample.class);
    }
}
```

### Solon

```java
@SolonMain
public class SolonExample {
    public static void main(String[] args) {
        SolonApp solonApp = Solon.start(SolonExample.class, args);

        // 通过传入上下文进行初始化, 同时可指定数据源名称, 若未指定则默认使用第一个
        new FrameGenSolonEntry(solonApp.context(), "data")
                .setPackage(builder -> {
                    builder.origin("org.framegen.example.solon");
                })
                .includes("user")
                .mybatisPlus()
                .run();
    }
}
```

### SpringBoot 集成

```java
@SpringBootApplication
public class SpringBoot3 {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBoot3.class, args);

        // 通过传入上下文进行初始化, 同时可指定数据源名称, 若未指定则默认使用第一个
        new FrameGenSpringBootEntry(context)
                .setPackage(builder -> {
                    builder.origin("springboot");
                })
                .excludes("user")
                .lombok()
                .mybatisPlus()
                .run();
    }
}
```

示例: [https://gitee.com/frailty/frame-gen-example](https://gitee.com/frailty/frame-gen-example)
