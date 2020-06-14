package sz.api.exceptions

import jodd.exception.ExceptionUtil
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import sz.api.reply.ReplyBase

//
// Created by kk on 2020/5/2.
//

@ControllerAdvice
@ConditionalOnProperty(name = ["sz.api_doc.working_mode"], havingValue = "dev")
class ApiDevExceptionHandler {

    val logger = LoggerFactory.getLogger("App")

    @ExceptionHandler
    fun onException(ex: Throwable): ResponseEntity<ReplyBase> {
        val reply = ReplyBase()

        if (ex is ApiException) {
            reply.ret = ex.errCode
            reply.errmsg = ex.localizedMessage
        } else {
            // dev 模式下, 会将异常的堆栈调用信息写入到 errMsg 里, 方便开发调试
            reply.ret = -1
            reply.errmsg = ExceptionUtil.exceptionChainToString(ex)
        }

        logger.debug(reply.errmsg)

        return ResponseEntity.ok(reply)
    }
}