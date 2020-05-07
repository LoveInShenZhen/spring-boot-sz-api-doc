package sz.api.doc

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ValueConstants
import sz.api.doc.annotations.Comment
import sz.api.tools.JsonDataType
import sz.api.tools.escapeMarkdown
import sz.api.tools.toJsonPretty
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmErasure

//
// Created by kk on 17/8/24.
//
@ExperimentalStdlibApi
@Suppress("DuplicatedCode", "JoinDeclarationAndAssignment", "SpellCheckingInspection")
class ApiInfo
constructor(
    // API url path
    val path: String,

    // API http method: GET or POST
    val httpMethod: String,

    // API 对应的 Controller 类名称
    val controllerClass: String,

    // API 对应的 Controller 类下的方法名称
    val methodName: String) {

    // 返回Replay 对应的 java class name
    val replyClass: String
        get() {
            return replyInfo.kotlin_class!!.javaObjectType.name
        }

    // Post 时 @RequsetBody 对应的 类名称
    var postDataClass: String = ""

    private var postDataKClass: KClass<*>? = null

    // Post 时 @RequsetBody 对应的 Sample 数据
    val postJsonSample: String
        get() {
            return if (postDataKClass == null) {
                ""
            } else {
                SampleJsonData(postDataKClass!!)
            }
        }

    // API 描述
    var apiComment: String = ""

    val groupName: String by lazy {
        apiGroup()
    }

    // 返回Replay的描述信息
    var replyInfo: FieldSchema

    // 返回结果样例
    val replySampleData: String
        get() {
            return SampleJsonData(this.replyInfo.kotlin_class!!)
        }

    // API 所有参数的描述
    var params = mutableListOf<ParameterInfo>()

    private val logger = LoggerFactory.getLogger("sz-api-doc")

    init {
        this.replyInfo = FieldSchema().apply {
            level = 0
            name = "reply"
            desc = ""
            type = JsonDataType.OBJECT.typeName
        }

        try {
            analyse()
        } catch (ex: Exception) {
            logger.error("ApiInfo analyse:WARN ${this.path} Abnormal")
        }
    }

    fun toMarkdownStr(str: String): String {
        return str.escapeMarkdown()
    }

    private fun apiGroup(): String {
        val controllerClazz = Class.forName(this.controllerClass)
        val anno = controllerClazz.getAnnotation(Comment::class.java)
        return anno?.value ?: this.controllerClass
    }

    private fun analyse() {

        analyseMethod()

        analyseReply()
    }

    private fun analyseMethod() {
        // 分析 controller 方法信息
        val controllerKClazz = Class.forName(this.controllerClass).kotlin
        val method = controllerKClazz.functions.find { it.name == this.methodName }
        // 如果方法上有 @Comment 注解, 则使用其值作为 api 描述
        val commentAnno = method!!.findAnnotation<Comment>()
        if (commentAnno != null) {
            this.apiComment = commentAnno.value
        }

        // 在此设置 reply 的返回类型信息
        this.replyInfo.kotlin_class = method.returnType.jvmErasure //Class.forName(method.returnType.javaType.typeName).kotlin

        // 扫描方法的参数
        method.parameters
            .filter { it.name != null }     // 过滤掉 实例方法的 instance 参数
            .forEach {
                val annRequestBody = it.findAnnotation<RequestBody>()
                if (annRequestBody == null) {
                    // 参数没有 @RequestBody 注解
                    val paramInfo = ParameterInfo()
                    paramInfo.name = it.name!!
                    paramInfo.type = it.type.javaType.typeName.split(".").last()

                    if (it.isOptional || it.type.isMarkedNullable) {
                        paramInfo.required = false
                    }

                    // 检查参数是否有 @Comment 注解, 如果有, 使用注解的内容作为参数描述
                    val annComment = it.findAnnotation<Comment>()
                    if (annComment != null) {
                        paramInfo.desc = annComment.value
                    }

                    // 检查参数是否有 @RequestParam 注解
                    val annRequestParam = it.findAnnotation<RequestParam>()
                    if (annRequestParam != null) {
                        // @RequestParam 有指定 value (@AliasFor("name")) , 以 @RequestParam 的设定为准
                        if (annRequestParam.value.isNotBlank()) {
                            paramInfo.name = annRequestParam.value
                        }

                        // @RequestParam 有指定 name, 以  @RequestParam 的设定为准
                        if (annRequestParam.name.isNotBlank()) {
                            paramInfo.name = annRequestParam.name
                        }

                        // @RequestParam 有指定默认值
                        if (annRequestParam.defaultValue != ValueConstants.DEFAULT_NONE) {
                            paramInfo.required = false
                            paramInfo.defaultValue = annRequestParam.defaultValue
                        }
                    }

                    this.params.add(paramInfo)
                } else {
                    // 参数上有 @RequestBody 注解, 并且参数中也最多只能有一个 @RequestBody 注解
                    this.postDataKClass = it.type.jvmErasure
                    this.postDataClass = it.type.jvmErasure.qualifiedName!!
                }

            }
    }

    private fun analyseReply() {
        // 分析返回的 reply 的信息
//        FieldSchema.resolveFields(Class.forName(this.replyClass).kotlin, replyInfo)
        FieldSchema.resolveFields(this.replyInfo.kotlin_class!!, this.replyInfo)
    }

    fun TestPage(): String {
        return "/api/builtin/doc/ApiTest.html?apiUrl=$path&httpMethod=$httpMethod"
    }

    fun anchor(): String {
//        return "${controllerClass}.${methodName}".replace(".", "_")
        return "${path}.${httpMethod}".replace(".", "_").replace("/", "_")
    }

    fun DocPage(): String {
        return "/api/builtin/doc/apiHtmlDoc#${anchor()}"
    }

    fun IsGetJsonApi(): Boolean {
        return this.httpMethod.equals("GET", ignoreCase = true)
    }

    fun IsPostJsonApi(): Boolean {
        return this.httpMethod.equals("POST", ignoreCase = true)
    }

    fun hasRequestBody(): Boolean {
        val controllerKClazz = Class.forName(this.controllerClass).kotlin
        val method = controllerKClazz.functions.find { it.name == this.methodName }
        return method!!.parameters.find { it.hasAnnotation<RequestBody>() } != null
    }

    fun postJsonSchema(): String {
        val jsonSchema = FieldSchema()
        jsonSchema.level = 0
        jsonSchema.name = "Post Json Schema"
        jsonSchema.desc = ""
        jsonSchema.type = JsonDataType.OBJECT.typeName
        jsonSchema.kotlin_class = Class.forName(this.postDataClass).kotlin

        FieldSchema.resolveFields(Class.forName(this.postDataClass).kotlin, jsonSchema)

        return jsonSchema.JsonSchema()
    }

    fun jsonStr(): String {
        return this.toJsonPretty()
    }

    companion object {

        val logger = LoggerFactory.getLogger("App")!!

        fun SampleJsonData(kClass: KClass<*>): String {
            val mockDataFunc = kClass.memberFunctions
                .find { it.name == "mockData" }
//                ?: return """请在 ${kClass.qualifiedName} 实现 fun mockData() { ... } 方法, 填充样例数据"""

            val sampleObj = kClass.java.newInstance()

            mockDataFunc?.call(sampleObj)
            return sampleObj.toJsonPretty()

        }
    }
}