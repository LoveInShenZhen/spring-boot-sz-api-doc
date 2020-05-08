package sz.api.exceptions

import jodd.exception.ExceptionUtil
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import sz.api.reply.ReplyBase

//
// Created by kk on 2020/5/7.
//

@ControllerAdvice
@Profile("api_doc", "prod")
class ApiProdExceptionHandler {
    val logger = LoggerFactory.getLogger("App")

    @ExceptionHandler
    fun onException(ex: Throwable): ResponseEntity<ReplyBase> {
        val reply = ReplyBase()

        // 只暴露 ApiException 给api的调用方
        if (ex is ApiException) {
            reply.ret = ex.errCode
            reply.errmsg = ex.localizedMessage
        } else {
            reply.ret = -1
            reply.errmsg = "Internal error"
        }

        logger.warn(ExceptionUtil.exceptionChainToString(ex))

        return ResponseEntity.ok(reply)
    }
}