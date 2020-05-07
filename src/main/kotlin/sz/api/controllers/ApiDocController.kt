package sz.api.controllers

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping
import sz.api.doc.ApiGroup
import sz.api.doc.ApiInfo
import sz.api.reply.ReplyBase
import sz.api.tools.cliColor
import java.io.StringWriter
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberFunctions

//
// Created by kk on 2020/5/3.
//

@ExperimentalStdlibApi
@Controller
@Profile("api_doc")
class ApiDocController(
    @Autowired private val handlerMapping: RequestMappingHandlerMapping,
    @Autowired private val freemarkerCfg: freemarker.template.Configuration,
    @Autowired private val serverProps: ServerProperties
) {
    private val logger = LoggerFactory.getLogger("App")!!

    init {
        val apiDocUrl = "http://${serverProps.address?.hostAddress ?: "localhost"}:${serverProps.port ?: 8080}/api/builtin/doc"
        logger.info("access api doc: ${apiDocUrl.cliColor()}")
    }

    private val allApi: List<ApiGroup> by lazy {
        resolveApiGroup()
    }

    private fun resolveApiGroup(): List<ApiGroup> {
        val apiGroupMap = mutableMapOf<String, ApiGroup>()
        val validHttpMethod = setOf(RequestMethod.POST, RequestMethod.GET)
        handlerMapping.handlerMethods.forEach { info, method ->
            info.methodsCondition.methods.filter { httpMethod -> httpMethod in validHttpMethod }
                .forEach { httpMethod ->
                    info.patternsCondition.patterns.map { it.patternString }.forEach { path ->
                        val controllerFun = method.beanType.kotlin.memberFunctions.find { it.name == method.method.name }!!
                        if (controllerFun.returnType.isSubtypeOf(ReplyBase::class.createType())) {
                            val apiInfo = ApiInfo(
                                path = path,
                                httpMethod = httpMethod.name,
                                controllerClass = method.beanType.name,
                                methodName = method.method.name
                            )

                            val group = apiGroupMap.getOrPut(apiInfo.groupName) {
                                ApiGroup(groupName = apiInfo.groupName)
                            }
                            group.apiInfoList.add(apiInfo)
                        }
                    }
                }
        }

        return apiGroupMap.map { it.value }.sortedBy { it.groupName }.toList()
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

        val template = freemarkerCfg.getTemplate("ApiDocTemplates/ApiDoc.md.ftl")
        val sw = StringWriter()
        template.process(model, sw)
        return ResponseEntity.ok().header("Content-Type", "text/plain; charset=utf-8").body(sw.toString())
    }

    @GetMapping("/api/builtin/doc/apiHtmlDoc")
    fun apiHtmlDoc(): ResponseEntity<String> {
        val model = mapOf<String, Any>("groups" to allApi)
        val mdTemplate = freemarkerCfg.getTemplate("ApiDocTemplates/ApiDoc.md.ftl")
        val mdsw = StringWriter()
        mdTemplate.process(model, mdsw)

        val docTemplate = freemarkerCfg.getTemplate("ApiDocTemplates/ApiDoc.html.ftl")
        val docsw = StringWriter()
        docTemplate.process(mapOf("api_markdown" to mdsw.toString()), docsw)

        return ResponseEntity.ok().header("Content-Type", "text/html; charset=utf-8").body(docsw.toString())
    }

    @GetMapping("/api/builtin/doc/apiIndex.html")
    fun apiIndex(model: Model): String {
        model.addAttribute("groups", allApi)
        return "ApiDocTemplates/ApiIndex.html"
    }

    @GetMapping("/api/builtin/doc/ApiTest.html")
    fun apiSample(@RequestParam("apiUrl") apiUrl: String,
                  @RequestParam("httpMethod") httpMethod: String,
                  model: Model): String {
        val apiInfo = allApi.flatMap { it.apiInfoList }
            .find { it.path == apiUrl && it.httpMethod == httpMethod }
            ?: throw RuntimeException("route: $apiUrl 不存在或者http method 不匹配")
        model.addAttribute("apiInfo", apiInfo)
        return "ApiDocTemplates/ApiTest.html"
    }

}