# 前后端分离的开发模式下, Api 接口文档管理方式

通常，设计 API 规范有两个方向，Design-First（设计优先） 或 Code-First（编码优先）

* **Design-First** 和 **Code-First** 各有优缺点, 这里不进行比较.
*  **spring-boot-sz-api-doc** 选择**Code-First**方式.

## Design-First（设计优先）

即优先设计 API 规范，设计完成后再着手进行代码开发工作。

采用 Design-First 就意味着，将设计 API 路由、参数等工作提前，后续整个软件开发的流程都需要围绕着 API 规范为核心，当然这需要有一定的设计经验的开发人员才能胜任。

## Code-First（编码优先）

即通过代码中关于 API 描述特性、注解或注释自动生成 API 描述文件的设计方式.

适合倾向于在代码上编写 API 规范，通过自动化设施自动生成文档的团队。

更多可参考: https://swagger.io/blog/api-design/design-first-or-code-first-api-development/

# spring-boot-sz-api-doc

spring-boot-sz-api-doc 选择 **Code-First** 方式, 通过注解方式, 自动生成 API 的接口文档和测试页面

## 快速指南

