## API 接口文档
<#list groups as group>
### ${group.toMarkdownStr(group.groupName)}
<#list group.apiInfoList as apiInfo>
<span id="${apiInfo.anchor()}"></span>
#### [${apiInfo.methodName}](#${apiInfo.anchor()})
* **功能描述**: ${apiInfo.toMarkdownStr(apiInfo.apiComment)}
* **API PATH**: ${apiInfo.toMarkdownStr(apiInfo.path)}
* **HTTP Method**: ${apiInfo.httpMethod}

* **Query参数说明**:
    | 参数名称 | 参数类型 | 必填 | 默认值 | 描述 |
    |  ----  | ----  | ---- | ---- | ---- |
<#list apiInfo.params as param>
    | **${param.toMarkdownStr(param.name)}** | ${param.type} | ${param.required?string('yes', 'no')} | ${param.defaultValue} |${param.toMarkdownStr(param.desc!)} |
</#list>

<#if apiInfo.IsPostJsonApi() && apiInfo.hasRequestBody()>
* Post Json 的数据结构:
```
${apiInfo.postJsonSchema!}
```
</#if>

<#if apiInfo.IsPostJsonApi() && apiInfo.hasRequestBody()>
* Post Json 样例:
```json
${apiInfo.postJsonSample!}
```
</#if>

* <a href="${apiInfo.TestPage()}" target="_blank">测试页面</a>:
* **Reply Java类名称**: _${apiInfo.replyInfo.className}_
* 返回结果的 JSON 结构:

```
${apiInfo.replyInfo.JsonSchema()}
```

* 返回结果样例:

```json
${apiInfo.replySampleData!}
```
</#list>
</#list>
