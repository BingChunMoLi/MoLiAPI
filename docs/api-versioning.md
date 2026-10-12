# API 版本控制

项目使用 Spring Boot 4.1.1 / Spring Framework 7 的原生 API 版本控制。
版本比较、受支持版本校验和方法选择均由 Spring MVC 完成。
参考 [Spring API Versioning](https://docs.spring.io/spring-framework/reference/web/webmvc-versioning.html)。

## 请求规则

| 请求 | 处理方式 |
| --- | --- |
| `/version` | 默认 v1，调用 `getVersion` |
| `/v1/version` | 显式 v1，调用 `getVersion` |
| `/v2/version` | 显式 v2，调用 `getVersionV2`，保持原有应用版本响应结构 |
| `/shici/random`、`/v1/shici/random` | 调用现有 v1 随机诗词接口 |
| `/v2/shici/random` | 当前没有 v2 实现，返回 HTTP 400，不回退到 v1 |
| `/v3/version` | 当前不支持 v3，返回 HTTP 400 |

业务路径版本使用整数主版本前缀，如 `/v1`、`/v2`。不带前缀默认 v1；`moli-version` 请求头不会改变路由。
响应中的应用版本号（例如 `3.15.11`）与 API 契约版本（v1、v2）相互独立。

现有未声明 `version` 的业务控制器方法自动归属 v1，返回结构、HTTP 方法、参数和原有路径保持兼容。
后台 `/system/**` 的认证同时覆盖版本前缀路径。静态资源、Actuator 和错误处理器不添加版本别名。
OpenAPI 的 `/v3/api-docs`、YAML 和 Swagger 配置路径保持原样；如果设置 `springdoc.api-docs.path`，
对应文档路径也不会被当成业务 API 版本。

## 添加新版接口

控制器继续使用原有业务路径，利用 Spring 注解的 `version` 属性声明版本，不要手动拼接 `/v1` 或 `/v2`：

```java
@RestController
@RequestMapping("/example")
public class ExampleController {

    @GetMapping(version = "1")
    public ResultVO<String> v1() {
        return ResultVO.ok("v1");
    }

    @GetMapping(version = "2")
    public ResultVO<String> v2() {
        return ResultVO.ok("v2");
    }
}
```

`/example` 和 `/v1/example` 选择 v1，`/v2/example` 选择 v2。
也可以在类的 `@RequestMapping(version = "2")` 上声明整组 v2 接口。
默认使用固定版本，避免未实现的新版自动调用旧实现；只有明确需要跨版本兼容时才使用 Spring 的 `1+` 等基线语义。

`ApiVersionRequestMappingHandlerMapping` 通过 Spring Boot 的 `WebMvcRegistrations` 扩展添加路径别名，
保留 Spring 原生映射条件。`ApiVersionWebMvcRegistrations` 使用原生路径版本解析器，并仅对版本前缀路径启用解析。
`spring.mvc.apiversion.default` 为 `1`，显式受支持版本为 `1`、`2`，Spring 还会检测控制器中声明的版本。

## 验证

```shell
mvn -Dtest=VersionControllerTest test
```

测试覆盖默认版本、显式 v1/v2、原生注解路由、路径变量、Servlet context path、版本错误返回、认证和文档路径。
`scripts/native-smoke-test.py` 同时检查 AOT/native 应用的实际版本路由及 OpenAPI 路径兼容性。
