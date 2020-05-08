@file:Suppress("DuplicatedCode")

package sz.api.resolve

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping
import sz.api.doc.ApiGroup
import sz.api.doc.ApiInfo
import sz.api.reply.ReplyBase
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberFunctions

//
// Created by kk on 2020/5/8.
//

@ExperimentalStdlibApi
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
class DefinedApisFroReactive : IDefinedApis {

    @Autowired
    lateinit var handlerMapping: RequestMappingHandlerMapping

    override fun apiGroups(): List<ApiGroup> {
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
}