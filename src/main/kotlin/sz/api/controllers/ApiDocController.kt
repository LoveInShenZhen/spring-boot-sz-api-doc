package sz.api.controllers

import freemarker.template.Configuration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import sz.api.doc.ApiGroup
import sz.api.doc.annotations.Comment
import sz.api.resolve.IDefinedApis
import sz.api.tools.cliColor
import java.io.StringWriter

//
// Created by kk on 2020/5/3.
//

@ExperimentalStdlibApi
@Controller
@Profile("api_doc")
@Comment("API接口文档控制器")
class ApiDocController(
    @Autowired private val definedApis: IDefinedApis,
    @Autowired freemarkerConfig: Configuration,
    @Autowired private val serverProps: ServerProperties
) {
    private val logger = LoggerFactory.getLogger("App")!!
    private val freemarkerCfg: Configuration

    init {
        val apiDocUrl = "http://${serverProps.address?.hostAddress ?: "localhost"}:${serverProps.port ?: 8080}/api/builtin/doc"
        logger.info("access api doc: ${apiDocUrl.cliColor()}")
        freemarkerCfg = freemarkerConfig.clone() as Configuration
        freemarkerCfg.setClassLoaderForTemplateLoading(ApiDocController::class.java.classLoader, "templates")
    }

    private val allApi: List<ApiGroup> by lazy {
        definedApis.apiGroups()
    }


    @GetMapping("/api/builtin/doc")
    fun index(): ResponseEntity<String> {
        val html = """
<html>
<head>
<style type="text/css">
    body {
        background: #e3dede;
    }
</style>
</head>
<body>
    <p>欢迎使用 SZ 后端快速开发框架 <a href="https://github.com/LoveInShenZhen/spring-boot-sz-api-doc">文档请看</a></p>
            <ul>
                <li><a href="/api/builtin/doc/apiIndex.html">api 列表</a></li>
                <li><a href="/api/builtin/doc/apiMarkdownDoc">api 文档的markdown格式</a></li>
                <li><a href="/api/builtin/doc/apiHtmlDoc">api 文档的html格式</a></li>
            </ul>
</body>
</html>""".trimIndent()
        return ResponseEntity.ok(html)
    }

    @GetMapping("/api/builtin/doc/apiMarkdownDoc")
    fun apiMarkdownDoc(): ResponseEntity<String> {
        val model = mapOf<String, Any>("groups" to allApi)
        val markdown = freemarkerCfg.process("ApiDocTemplates/ApiDoc.md.ftl", model)
        return ResponseEntity.ok().header("Content-Type", "text/plain; charset=utf-8").body(markdown)
    }

    @GetMapping("/api/builtin/doc/apiHtmlDoc")
    fun apiHtmlDoc(): ResponseEntity<String> {
        val model = mapOf<String, Any>("groups" to allApi)
        val markdownTxt = freemarkerCfg.process("ApiDocTemplates/ApiDoc.md.ftl", model)

        val html = freemarkerCfg.process("ApiDocTemplates/ApiDoc.html.ftl", mapOf("api_markdown" to markdownTxt))

        return html.toHtmlResponse()
    }

    @GetMapping("/api/builtin/doc/apiIndex.html")
    fun apiIndex(model: Model): ResponseEntity<String> {
        model.addAttribute("groups", allApi)
        return freemarkerCfg.process("ApiDocTemplates/ApiIndex.html.ftl", model).toHtmlResponse()
    }

    @GetMapping("/api/builtin/doc/ApiTest.html")
    fun apiSample(@RequestParam("apiUrl") apiUrl: String,
                  @RequestParam("httpMethod") httpMethod: String,
                  model: Model): ResponseEntity<String> {
        val apiInfo = allApi.flatMap { it.apiInfoList }
            .find { it.path == apiUrl && it.httpMethod == httpMethod }
            ?: throw RuntimeException("route: $apiUrl 不存在或者http method 不匹配")
        model.addAttribute("apiInfo", apiInfo)

        return freemarkerCfg.process("ApiDocTemplates/ApiTest.html.ftl", model).toHtmlResponse()
    }

    @Comment("返回 Api 分组列表")
    @GetMapping("/api/builtin/doc/apiInfo")
    @ResponseBody
    fun apiInfo(): ApiInfoReply {
        val reply = ApiInfoReply()
        reply.api_groups = allApi
        return reply
    }
}

private fun Configuration.process(templateName: String, dataModel: Any): String {
    val template = this.getTemplate(templateName)
    val sw = StringWriter()
    template.process(dataModel, sw)
    return sw.toString()
}

private fun String.toHtmlResponse(): ResponseEntity<String> {
    return ResponseEntity.ok().header("Content-Type", "text/html; charset=utf-8").body(this)
}